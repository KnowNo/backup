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

import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.test.fixtures.file.TestNameTestDirectoryProvider
import org.gradle.util.TestUtil
import org.junit.Rule
import spock.lang.Specification

class SigningProjectSpec extends Specification {
    
    static final DEFAULT_KEY_SET = "gradle"
    
    @Rule public TestNameTestDirectoryProvider tmpDir = new TestNameTestDirectoryProvider()
        
    Project project = TestUtil.createRootProject()
    
    private assertProject() {
        assert project != null : "You haven't created a project"
    }
    
    def methodMissing(String name, args) {
        assertProject()
        project."$name"(*args)
    }
    
    def propertyMissing(String name) {
        project."$name"
    }
    
    def propertyMissing(String name, value) {
        project."$name" = value
    }
    
    def applyPlugin() {
        apply plugin: "signing"
    }
    
    def addProperties(Map props) {
        props.each { k, v ->
            project.setProperty(k, v)
        }
    }
    
    def addSigningProperties(keyId, secretKeyRingFile, password) {
        addPrefixedSigningProperties(null, keyId, secretKeyRingFile, password)
    }
    
    def addPrefixedSigningProperties(prefix, keyId, secretKeyRingFile, password) {
        def truePrefix = prefix ? "${prefix}." : "" 
        def properties = [:]
        def values = [keyId: keyId, secretKeyRingFile: secretKeyRingFile, password: password]
        values.each { k, v ->
            properties["signing.${truePrefix}${k}"] = v
        }
        addProperties(properties)
        values
    }
    
    def getSigningPropertiesSet(setName = DEFAULT_KEY_SET) {
        def properties = [:]
        properties.keyId = getKeyResourceFile(setName, "keyId.txt").text.trim()
        properties.secretKeyRingFile = getKeyResourceFile(setName, "secring.gpg").absolutePath
        properties.password = getKeyResourceFile(setName, "password.txt").text.trim()
        properties
    }
    
    def addSigningProperties(Map args = [:]) {
        def properties = getSigningPropertiesSet(args.set ?: DEFAULT_KEY_SET)
        addPrefixedSigningProperties(args.prefix, properties.keyId, properties.secretKeyRingFile, properties.password)
    }
    
    def getKeyResourceFile(setName, fileName) {
        getResourceFile("keys/$setName/$fileName")
    }
    
    def getResourceFile(path) {
        def copiedFile = tmpDir.file(path)
        if (!copiedFile.exists()) {
            
            def url = getClass().classLoader.getResource(path)
            def file = new File(url.toURI())
            if (file.exists()) {
                copiedFile.copyFrom(file)
            }
        }
        
        copiedFile
    }
    
    def useJavadocAndSourceJars() {
        apply plugin: "java"
        
        task("sourcesJar", type: Jar, dependsOn: classes) { 
            classifier = 'sources' 
            from sourceSets.main.allSource
        } 

        task("javadocJar", type: Jar, dependsOn: javadoc) { 
            classifier = 'javadoc' 
            from javadoc.destinationDir 
        } 
    }
}