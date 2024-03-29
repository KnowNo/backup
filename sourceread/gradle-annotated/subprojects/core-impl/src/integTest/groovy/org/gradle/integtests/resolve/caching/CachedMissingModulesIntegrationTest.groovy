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

package org.gradle.integtests.resolve.caching

import org.gradle.integtests.fixtures.AbstractDependencyResolutionTest
import org.gradle.integtests.fixtures.executer.GradleContextualExecuter
import spock.lang.IgnoreIf

class CachedMissingModulesIntegrationTest extends AbstractDependencyResolutionTest {

    def "cached not-found information for dynamic version is ignored if module is not available in any repo"() {
        given:
        server.start()
        def repo1 = mavenHttpRepo("repo1")
        repo1.module("group", "projectA", "1.0")
        def repo2 = mavenHttpRepo("repo2")
        def repo2Module = repo2.module("group", "projectA", "1.0")

        buildFile << """
            repositories {
                maven {
                    name 'repo1'
                    url '${repo1.uri}'
                }
                maven {
                    name 'repo2'
                    url '${repo2.uri}'
                }
            }
            configurations { compile }
            dependencies {
                compile 'group:projectA:latest.integration'
            }

            task retrieve(type: Sync) {
                into 'libs'
                from configurations.compile
            }
            """

        when:
        repo1.expectMetaDataGetMissing("group", "projectA")
        repo1.expectMetaDataGetMissing("group", "projectA")
        repo1.expectDirectoryListGet("group", "projectA")
        repo1.expectDirectoryListGet("group", "projectA")
        repo2.expectMetaDataGetMissing("group", "projectA")
        repo2.expectMetaDataGetMissing("group", "projectA")
        repo2.expectDirectoryListGet("group", "projectA")
        repo2.expectDirectoryListGet("group", "projectA")

        then:
        runAndFail 'retrieve'

        when:
        server.resetExpectations()
        repo1.expectMetaDataGetMissing("group", "projectA")
        repo1.expectMetaDataGetMissing("group", "projectA")
        repo1.expectDirectoryListGet("group", "projectA")
        repo1.expectDirectoryListGet("group", "projectA")
        repo2Module.publish()
        repo2.expectMetaDataGet("group", "projectA")
        repo2Module.pom.expectGet()
        repo2Module.getArtifact().expectGet()

        then:
        run 'retrieve'

        when:
        server.resetExpectations()

        then:
        run 'retrieve'
    }

    def "cached not-found information for fixed version is ignored if module is not available in any repo"() {
        given:
        server.start()
        def repo1 = mavenHttpRepo("repo1")
        def repo1Module = repo1.module("group", "projectA", "1.0")
        def repo1Artifact = repo1Module.artifact

        def repo2 = mavenHttpRepo("repo2")
        def repo2Module = repo2.module("group", "projectA", "1.0")
        def repo2Artifact = repo2Module.artifact

        buildFile << """
    repositories {
        maven {
            name 'repo1'
            url '${repo1.uri}'
        }
        maven {
            name 'repo2'
            url '${repo2.uri}'
        }
    }
    configurations { compile }
    dependencies {
        compile 'group:projectA:1.0'
    }

    task retrieve(type: Sync) {
        into 'libs'
        from configurations.compile
    }
    """

        when:
        repo1Module.pom.expectGetMissing()
        repo1Artifact.expectHeadMissing()
        repo2Module.pom.expectGetMissing()
        repo2Artifact.expectHeadMissing()

        then:
        runAndFail 'retrieve'

        when:
        server.resetExpectations()
        repo1Module.pom.expectGetMissing()
        repo1Artifact.expectHeadMissing()
        repo2Module.publish()
        repo2Module.pom.expectGet()
        repo2Artifact.expectGet()

        then:
        run 'retrieve'

        when:
        server.resetExpectations()

        then:
        run 'retrieve'
    }

    @IgnoreIf({ GradleContextualExecuter.isParallel() })
    def "hit each remote repo only once per build and missing module"() {
        given:
        server.start()
        def repo1 = mavenHttpRepo("repo1")
        def repo1Module = repo1.module("group", "projectA", "1.0")
        def repo1Artifact = repo1Module.artifact
        def repo2 = mavenHttpRepo("repo2")
        def repo2Module = repo2.module("group", "projectA", "1.0")
        def repo2Artifact = repo2Module.artifact

        settingsFile << "include 'subproject'"
        buildFile << """
            allprojects{
                repositories {
                    maven {
                        name 'repo1'
                        url '${repo1.uri}'
                    }
                    maven {
                        name 'repo2'
                        url '${repo2.uri}'
                    }
                }
            }
            configurations {
                config1
            }
            dependencies {
                config1 'group:projectA:1.0'
            }

            task resolveConfig1 << {
                   configurations.config1.incoming.resolutionResult.allDependencies{
                        it instanceof UnresolvedDependencyResult
                   }
            }

            project(":subproject"){
                configurations{
                    config2
                }
                dependencies{
                    config2 'group:projectA:1.0'
                }
                task resolveConfig2 << {
                    configurations.config2.incoming.resolutionResult.allDependencies{
                        it instanceof UnresolvedDependencyResult
                    }
                }
            }
        """
        when:
        repo1Module.pom.expectGetMissing()
        repo1Artifact.expectHeadMissing()
        repo2Module.pom.expectGetMissing()
        repo2Artifact.expectHeadMissing()

        then:
        run('resolveConfig1')

        when:
        server.resetExpectations()
        repo1Module.pom.expectGetMissing()
        repo1Artifact.expectHeadMissing()
        repo2Module.pom.expectGetMissing()
        repo2Artifact.expectHeadMissing()

        then:
        run "resolveConfig1", "resolveConfig2"
    }

    def "does not hit remote repositories if version is available in local repo"() {
        given:
        server.start()
        def repo1 = mavenHttpRepo("repo1")
        def repo1Module = repo1.module("group", "projectA", "1.0")
        def repo2 = mavenRepo("repo2")
        def repo2Module = repo2.module("group", "projectA", "1.0")

        buildFile << """
        repositories {
           maven {
               name 'repo1'
               url '${repo1.uri}'
           }
           maven {
               name 'repo2'
               url '${repo2.uri}'
           }
       }
       configurations { compile }
       dependencies {
           compile 'group:projectA:1.0'
       }

       task retrieve(type: Sync) {
           into 'libs'
           from configurations.compile
       }
       """

        when:
        repo2Module.publish()
        repo1Module.pom.expectGetMissing()
        repo1Module.artifact.expectHeadMissing()

        then:
        run 'retrieve'

        when:
        server.resetExpectations()
        then:
        run 'retrieve'
    }
}
