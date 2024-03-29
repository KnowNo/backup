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

package org.gradle.plugins.javascript.envjs

import org.gradle.integtests.fixtures.WellBehavedPluginTest

import static org.gradle.plugins.javascript.base.JavaScriptBasePluginTestFixtures.addGradlePublicJsRepoScript
import org.gradle.plugins.javascript.envjs.browser.BrowserEvaluate

import static org.gradle.plugins.javascript.base.JavaScriptBasePluginTestFixtures.addGoogleRepoScript

class EnvJsPluginIntegrationTest extends WellBehavedPluginTest {

    def setup() {
        applyPlugin()
        addGradlePublicJsRepoScript(buildFile)
        buildFile << """
            repositories.mavenCentral()
        """
    }

    def "can download envjs by default"() {
        given:
        buildFile << """
            task resolve(type: Copy) {
                from javaScript.envJs.js
                into "deps"
            }
        """

        when:
        run "resolve"

        then:
        def js = file("deps/envjs.rhino-1.2.js")
        js.exists()
        js.text.contains("Envjs = function")
    }

    def "can evaluate content"() {
        given:
        file("input/index.html") << """
            <html>
                <head>
                    <script src="\${jqueryFileName}" type="text/javascript"></script>
                    <script type="text/javascript">
                        \\\$(function() {
                            \\\$("body").text("Added!");
                        });
                    </script>
                </head>
            </html>
        """

        addGoogleRepoScript(buildFile)

        buildFile << """
            configurations {
                jquery
            }
            dependencies {
                jquery "jquery:jquery.min:1.7.2@js"
            }

            task gatherContent(type: Copy) {
                into "content"
                from configurations.jquery
                from "input", {
                    expand jqueryFileName: "\${->configurations.jquery.singleFile.name}"
                }
            }

            task evaluate(type: ${BrowserEvaluate.name}) {
                content gatherContent
                resource "index.html"
                result "result.html"
            }
        """

        when:
        succeeds "evaluate"

        then:
        file("result.html").text.contains("<body>Added!</body>")
    }
}
