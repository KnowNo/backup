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

package org.gradle.api.internal.artifacts.repositories.resolver

import org.gradle.api.internal.artifacts.ModuleMetadataProcessor
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.parser.MetaDataParser
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.LatestStrategy
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.ResolverStrategy
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.VersionMatcher
import org.gradle.api.internal.artifacts.repositories.transport.RepositoryTransport
import org.gradle.api.internal.externalresource.local.LocallyAvailableResourceFinder
import org.gradle.api.internal.externalresource.transport.ExternalResourceRepository
import spock.lang.Specification
import spock.lang.Unroll

class MavenResolverTest extends Specification {
    def repositoryTransport = Mock(RepositoryTransport)
    def repository = Mock(ExternalResourceRepository)

    def rootUri = URI.create("localhost:8081:/testrepo/")
    def locallyAvailableResourceFinder = Mock(LocallyAvailableResourceFinder)
    def parser = Mock(MetaDataParser)
    def processor = Mock(ModuleMetadataProcessor)
    def versionMatcher = Mock(VersionMatcher)
    def latestStrategy = Mock(LatestStrategy)
    def resolverStrategy = Stub(ResolverStrategy)

    def setup() {
        repositoryTransport.getRepository() >> repository
    }

    @Unroll
    def "setUseMavenMetaData '#value' adapts versionLister to #classname"() {
        setup:
        MavenResolver testresolver = new MavenResolver("test maven resolver", rootUri, repositoryTransport,
                locallyAvailableResourceFinder, processor, versionMatcher, latestStrategy, resolverStrategy)
        when:
        testresolver.setUseMavenMetadata(value)
        then:
        testresolver.versionLister.class.name == classname
        where:
        value << [true, false]
        classname << [ChainedVersionLister.class.name, ResourceVersionLister.class.name]
    }
}
