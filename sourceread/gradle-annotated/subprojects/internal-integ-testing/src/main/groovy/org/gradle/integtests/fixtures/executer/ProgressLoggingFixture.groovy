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

package org.gradle.integtests.fixtures.executer

import org.gradle.test.fixtures.file.TestDirectoryProvider
import org.gradle.test.fixtures.file.TestFile

class ProgressLoggingFixture extends InitScriptExecuterFixture {

    private TestFile fixtureData

    ProgressLoggingFixture(GradleExecuter executer, TestDirectoryProvider testDir) {
        super(executer, testDir)
    }

    List<String> progressContent

    @Override
    String initScriptContent() {
        fixtureData = testDir.testDirectory.file("progress-fixture.log")
        """import org.gradle.logging.internal.*
           File outputFile = file("${fixtureData.toURI()}")
           OutputEventListener outputEventListener = new OutputEventListener() {
                void onOutput(OutputEvent event) {
                    if (event instanceof ProgressStartEvent) {
                        outputFile << "[START \$event.description]\\n"
                    } else if (event instanceof ProgressEvent) {
                        outputFile << "[\$event.status]\\n"
                    } else if (event instanceof ProgressCompleteEvent) {
                        outputFile << "[END \$event.description]\\n"
                    }
                }
           }
           def loggingOutputInternal = gradle.services.get(LoggingOutputInternal)
           loggingOutputInternal.addOutputEventListener(outputEventListener)
           buildFinished{
                loggingOutputInternal.removeOutputEventListener(outputEventListener)
           }"""
    }

    @Override
    void afterBuild() {
        if (fixtureData.exists()) {
            progressContent = fixtureData.text.readLines()
            assert fixtureData.delete()
        } else {
            progressContent = []
        }
    }

    boolean downloadProgressLogged(URI url) {
        downloadProgressLogged(url.toString())
    }

    boolean downloadProgressLogged(String url) {
        return progressLogged("Download", url)
    }

    boolean uploadProgressLogged(URI url) {
        uploadProgressLogged(url.toString())
    }

    boolean uploadProgressLogged(String url) {
        return progressLogged("Upload", url)
    }

    private boolean progressLogged(String operation, String url) {
        def lines = progressContent
        def startIndex = lines.indexOf("[START " + operation + " " + url + "]")
        if (startIndex == -1) {
            return false
        }
        lines = lines[startIndex..<lines.size()]
        lines = lines[0..lines.indexOf("[END " + operation + " " + url + "]")]
        lines.size() >= 2
    }
}
