/*
 * Copyright 2010 the original author or authors.
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

package org.gradle.api.internal.project

import org.gradle.api.artifacts.dsl.ArtifactHandler
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.artifacts.configurations.ConfigurationContainerInternal
import org.gradle.util.TestUtil
import spock.lang.Specification

class NewDefaultProjectTest extends Specification {

    def project = TestUtil.createRootProject()

    void "delegates to artifacts handler"() {
        def handler = Mock(ArtifactHandler)
        project.artifactHandler = handler

        when:
        project.artifacts {
            add('conf', 'art')
        }

        then:
        1 * handler.add('conf', 'art')
    }

    void "delegates to dependency handler"() {
        def handler = Mock(DependencyHandler)
        project.dependencyHandler = handler

        when:
        project.dependencies {
            add('conf', 'dep')
        }

        then:
        1 * handler.add('conf', 'dep')
    }

    void "delegates to configuration container"() {
        Closure cl = {}
        def container = Mock(ConfigurationContainerInternal)
        project.configurationContainer = container

        when:
        project.configurations cl

        then:
        1 * container.configure(cl)
    }
}
