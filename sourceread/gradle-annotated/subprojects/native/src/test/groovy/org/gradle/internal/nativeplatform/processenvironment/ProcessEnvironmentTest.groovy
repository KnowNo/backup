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
package org.gradle.internal.nativeplatform.processenvironment

import org.gradle.internal.nativeplatform.ProcessEnvironment
import org.gradle.internal.nativeplatform.services.NativeServices
import org.gradle.test.fixtures.file.TestNameTestDirectoryProvider
import org.gradle.util.Requires
import org.gradle.util.SetSystemProperties
import org.gradle.util.TestPrecondition
import org.junit.Rule
import spock.lang.Specification

class ProcessEnvironmentTest extends Specification {
    @Rule final TestNameTestDirectoryProvider tmpDir = new TestNameTestDirectoryProvider()
    @Rule final SetSystemProperties systemProperties = new SetSystemProperties()
    final ProcessEnvironment env = NativeServices.getInstance().get(ProcessEnvironment)

    @Requires(TestPrecondition.SET_ENV_VARIABLE)
    def "can set and remove environment variable"() {
        when:
        env.setEnvironmentVariable("TEST_ENV_1", "value")
        env.maybeSetEnvironmentVariable("TEST_ENV_2", "value")

        then:
        System.getenv("TEST_ENV_1") == "value"
        System.getenv("TEST_ENV_2") == "value"

        when:
        env.removeEnvironmentVariable("TEST_ENV_1")
        env.maybeRemoveEnvironmentVariable("TEST_ENV_2")

        then:
        System.getenv("TEST_ENV_1") == null
        System.getenv("TEST_ENV_2") == null
    }

    @Requires(TestPrecondition.WORKING_DIR)
    def "can get working directory of current process"() {
        expect:
        env.processDir.canonicalFile == new File('.').canonicalFile
    }

    @Requires(TestPrecondition.WORKING_DIR)
    def "can get set working directory of current process"() {
        File originalDir = new File(System.getProperty("user.dir"))

        when:
        env.setProcessDir(tmpDir.testDirectory)

        then:
        env.processDir.canonicalFile == tmpDir.testDirectory
        new File(".").canonicalFile == tmpDir.testDirectory

        cleanup:
        System.setProperty("user.dir", originalDir.absolutePath)
        env.setProcessDir(originalDir)
    }

    @Requires(TestPrecondition.PROCESS_ID)
    def "can get pid of current process"() {
        expect:
        env.pid != null
        env.maybeGetPid() == env.pid
    }
}
