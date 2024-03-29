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

package org.gradle.integtests

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.util.TextUtil

class StdioIntegrationTest extends AbstractIntegrationSpec {

    def "build can read stdin when stdin has bounded length"() {
        given:
        executer.requireOwnGradleUserHomeDir()
        buildFile << '''
task echo << {
    def reader = new BufferedReader(new InputStreamReader(System.in))
    while (true) {
        def line = reader.readLine() // readline will chomp the newline off the end
        if (!line || line == 'close') {
            break
        }
        print "[$line]"
    }
}
'''

        when:
        executer.withStdIn("abc\n123").withArguments("-s", "--info")
        run "echo"

        then:
        output.contains("[abc][123]")
    }

    def "build can read stdin when stdin has unbounded length"() {
        def writeEnd = new PipedOutputStream()
        def readEnd = new PipedInputStream(writeEnd)

        given:
        requireOwnGradleUserHomeDir()
        buildFile << '''
task echo << {
    def reader = new BufferedReader(new InputStreamReader(System.in))
    while (true) {
        def line = reader.readLine() // readline will chomp the newline off the end
        if (!line || line == 'close') {
            break
        }
        print "[$line]"
    }
}
'''
        and:
        writeEnd.write(TextUtil.toPlatformLineSeparators("abc\n123\nclose\n").bytes)

        when:
        executer.withStdIn(readEnd).withArguments("-s", "--info")
        run "echo"

        then:
        output.contains("[abc][123]")
    }
}
