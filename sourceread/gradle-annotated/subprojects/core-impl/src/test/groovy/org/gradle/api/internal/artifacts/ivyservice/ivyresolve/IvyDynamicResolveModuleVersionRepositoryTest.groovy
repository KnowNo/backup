/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.artifacts.ivyservice.ivyresolve

import org.apache.ivy.core.module.descriptor.DependencyDescriptor
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.gradle.api.internal.artifacts.metadata.DependencyMetaData
import org.gradle.api.internal.artifacts.metadata.MutableModuleVersionMetaData
import spock.lang.Specification

class IvyDynamicResolveModuleVersionRepositoryTest extends Specification {
    final target = Mock(LocalAwareModuleVersionRepository)
    final metaData = Mock(MutableModuleVersionMetaData)
    final requestedDependency = Mock(DependencyMetaData)
    final result = Mock(BuildableModuleVersionMetaDataResolveResult)
    final repository = new IvyDynamicResolveModuleVersionRepository(target)

    def "replaces each dependency version with revConstraint"() {
        def original = dependency('1.2+')
        def transformed = dependency()

        given:
        result.state >> BuildableModuleVersionMetaDataResolveResult.State.Resolved
        result.metaData >> metaData

        when:
        repository.getLocalDependency(requestedDependency, result)

        then:
        1 * target.getLocalDependency(requestedDependency, result)

        and:
        1 * metaData.dependencies >> [original]
        1 * original.withRequestedVersion('1.2+') >> transformed
        1 * metaData.setDependencies([transformed])
    }

    def "does nothing when dependency has not been resolved"() {
        when:
        repository.getLocalDependency(requestedDependency, result)

        then:
        1 * target.getLocalDependency(requestedDependency, result)
        _ * result.state >> BuildableModuleVersionMetaDataResolveResult.State.Missing
        0 * result._
    }

    def dependency(String revConstraint = '1.0') {
        def dep = Mock(DependencyMetaData)
        def descriptor = Mock(DependencyDescriptor)
        _ * descriptor.dynamicConstraintDependencyRevisionId >> ModuleRevisionId.newInstance('org', 'module', revConstraint)
        _ * dep.descriptor >> descriptor
        return dep
    }
}
