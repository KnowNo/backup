/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.plugins.signing

class SigningConfigurationsSpec extends SigningProjectSpec {
    
    def setup() {
        applyPlugin()
        useJavadocAndSourceJars()
        configurations {
            meta
            produced.extendsFrom meta, archives
        }
        
        artifacts {
            meta javadocJar, sourcesJar
        }
    }
        
    def "sign configuration with defaults"() {
        when:
        signing {
            sign configurations.archives, configurations.meta
        }
        
        then:
        def signingTasks = [signArchives, signMeta]

        // TODO - find way to test that the appopriate dependencies have been setup
        //        it would be easy if we could do…
        // 
        // configurations.archives.buildArtifacts in signArchives.dependsOn
        // 
        //        but we can't because of http://issues.gradle.org/browse/GRADLE-1608
        
        and:
        configurations.signatures.artifacts.size() == 3
        signingTasks.every { it.signatures.every { it in configurations.signatures.artifacts } }
    }

    def "sign configuration with inherited artifacts"() {
        when:
        signing {
            sign configurations.produced
        }
        
        then:
        configurations.signatures.artifacts.size() == 3
        signProduced.signatures.every { it in configurations.signatures.artifacts }
    }
}