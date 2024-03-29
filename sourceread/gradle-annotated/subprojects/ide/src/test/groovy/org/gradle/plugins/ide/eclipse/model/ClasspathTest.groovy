/*
 * Copyright 2009 the original author or authors.
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
package org.gradle.plugins.ide.eclipse.model

import org.gradle.api.internal.xml.XmlTransformer
import org.gradle.plugins.ide.eclipse.model.internal.FileReferenceFactory
import org.gradle.test.fixtures.file.TestNameTestDirectoryProvider
import org.junit.Rule
import spock.lang.Specification

public class ClasspathTest extends Specification {
    final fileReferenceFactory = new FileReferenceFactory()
    final customEntries = [
            new ProjectDependency("/test2", null),
            new Container("org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.6"),
            new Library(fileReferenceFactory.fromPath("/apache-ant-1.7.1/lib/ant-antlr.jar")),
            new SourceFolder("src", "bin2"),
            new Variable(fileReferenceFactory.fromVariablePath("GRADLE_CACHE/ant-1.6.5.jar")),
            new Container("org.eclipse.jdt.USER_LIBRARY/gradle"),
            new Output("bin")]
    final projectDependency = [customEntries[0]]
    final allDependencies = [customEntries[0], customEntries[2], customEntries[4]]

    private final Classpath classpath = new Classpath(new XmlTransformer(), fileReferenceFactory)

    @Rule
    public TestNameTestDirectoryProvider tmpDir = new TestNameTestDirectoryProvider()

    def setup() {

    }

    def "load from reader"() {
        when:
        classpath.load(customClasspathReader)

        then:
        classpath.entries == customEntries
    }

    def "configure overwrites dependencies and appends all other entries"() {
        def constructorEntries = [createSomeLibrary()]

        when:
        classpath.load(customClasspathReader)
        def newEntries = constructorEntries + projectDependency
        classpath.configure(newEntries)

        then:
        def entriesToBeKept = customEntries - allDependencies
        classpath.entries == entriesToBeKept + newEntries
    }

    def "load defaults"() {
        when:
        classpath.loadDefaults()

        then:
        classpath.entries == []
    }

    def "toXml contains custom values"() {
        def constructorEntries = [createSomeLibrary()]

        when:
        classpath.load(customClasspathReader)
        classpath.configure(constructorEntries)
        def xml = getToXmlReader()
        def other = new Classpath(new XmlTransformer(), fileReferenceFactory)
        other.load(xml)

        then:
        classpath == other
    }

    private InputStream getCustomClasspathReader() {
        return getClass().getResourceAsStream('customClasspath.xml')
    }

    private Library createSomeLibrary() {
        Library library = new Library(fileReferenceFactory.fromPath("/somepath"))
        library.exported = true
        return library
    }

    private InputStream getToXmlReader() {
        ByteArrayOutputStream toXmlText = new ByteArrayOutputStream()
        classpath.store(toXmlText)
        return new ByteArrayInputStream(toXmlText.toByteArray())
    }
}