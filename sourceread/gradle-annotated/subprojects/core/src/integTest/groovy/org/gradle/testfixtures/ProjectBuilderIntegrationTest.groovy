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

package org.gradle.testfixtures

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.test.fixtures.server.http.HttpServer
import org.junit.Rule
import org.gradle.test.fixtures.maven.MavenHttpRepository

class ProjectBuilderIntegrationTest extends AbstractIntegrationSpec {
    @Rule HttpServer server

    def "can resolve remote dependencies"() {
        def repo = new MavenHttpRepository(server, mavenRepo)
        repo.module("org.gradle", "a", "1.0").publish().allowAll()
        server.start()

        when:
        def project = ProjectBuilder.builder().build()
        project.with {
            repositories {
                maven { url repo.uri }
            }
            configurations {
                compile
                runtime { extendsFrom compile }
            }
            dependencies {
                compile "org.gradle:a:1.0"
            }
        }
        def compileFiles = project.configurations.compile.files
        def runtimeFiles = project.configurations.runtime.files

        then:
        compileFiles.size() == 1
        runtimeFiles.size() == 1
    }
}
