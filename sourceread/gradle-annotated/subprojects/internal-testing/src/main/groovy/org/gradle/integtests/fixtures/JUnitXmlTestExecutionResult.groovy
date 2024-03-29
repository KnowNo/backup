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
package org.gradle.integtests.fixtures

import org.gradle.test.fixtures.file.TestFile

import static org.hamcrest.Matchers.*
import static org.junit.Assert.assertThat

class JUnitXmlTestExecutionResult implements TestExecutionResult {
    private final TestFile buildDir
    private final TestResultOutputAssociation outputAssociation

    def JUnitXmlTestExecutionResult(TestFile projectDir, String buildDirName = 'build') {
        this(projectDir, TestResultOutputAssociation.WITH_SUITE, buildDirName)
    }

    def JUnitXmlTestExecutionResult(TestFile projectDir, TestResultOutputAssociation outputAssociation, String buildDirName = 'build') {
        this.outputAssociation = outputAssociation
        this.buildDir = projectDir.file(buildDirName)
    }

    boolean hasJUnitXmlResults() {
        xmlResultsDir().list().length > 0
    }

    TestExecutionResult assertTestClassesExecuted(String... testClasses) {
        Map<String, File> classes = findClasses()
        assertThat(classes.keySet(), equalTo(testClasses as Set))
        this
    }

    def fromFileToTestClass(String s) {
        s.replaceAll(/#([\d\w][\d\w])/){
            (char)Integer.parseInt(it[1], 16)
        }
    }

    TestClassExecutionResult testClass(String testClass) {
        return new JUnitTestClassExecutionResult(findTestClass(testClass), testClass, outputAssociation)
    }

    private def findTestClass(String testClass) {
        def classes = findClasses()
        assertThat(classes.keySet(), hasItem(testClass))
        def classFile = classes.get(testClass)
        assertThat(classFile, notNullValue())
        return new XmlSlurper().parse(classFile)
    }

    private def findClasses() {
        xmlResultsDir().assertIsDir()

        Map<String, File> classes = [:]
        buildDir.file('test-results').eachFile { File file ->
            def matcher = (file.name=~/TEST-(.+)\.xml/)
            if (matcher.matches()) {
                classes[fromFileToTestClass(matcher.group(1))] = file
            }
        }
        return classes
    }

    private TestFile xmlResultsDir() {
        buildDir.file('test-results')
    }
}

