/*
 * Copyright 2013 the original author or authors.
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

package org.gradle.test.fixtures.maven

import org.gradle.api.Project
import org.gradle.test.fixtures.file.TestFile
import org.gradle.util.TestUtil
import spock.lang.Specification

class MavenLocalModuleTest extends Specification {
    Project project
    TestFile testFile
    MavenModule mavenLocalModule
    MavenModule snapshotMavenLocalModule

    def setup() {
        project = TestUtil.createRootProject()
        testFile = new TestFile(project.projectDir, "build/test")
        mavenLocalModule = new MavenLocalModule(testFile, "my-company", "my-artifact", "1.0")
        snapshotMavenLocalModule = new MavenLocalModule(testFile, "my-company", "my-artifact", "1.0-SNAPSHOT")
    }

    def "Get parent and its POM section"() {
        when:
        MavenLocalModule parent = mavenLocalModule.parent("my-company", "parent", "0.1")

        then:
        parent != null
        """
<parent>
  <groupId>my-company</groupId>
  <artifactId>parent</artifactId>
  <version>0.1</version>
</parent>
""" == parent.parentPomSection
    }

    def "Add multiple dependencies without type"() {
        when:
        List dependencies = mavenLocalModule.dependsOn("dep1", "dep2").dependencies

        then:
        dependencies != null
        dependencies.size() == 2
        dependencies.get(0).groupId == 'my-company'
        dependencies.get(0).artifactId == 'dep1'
        dependencies.get(0).type == null
        dependencies.get(0).version == '1.0'
        dependencies.get(1).groupId == 'my-company'
        dependencies.get(1).artifactId == 'dep2'
        dependencies.get(1).type == null
        dependencies.get(1).version == '1.0'
    }

    def "Add single dependency"() {
        when:
        List dependencies = mavenLocalModule.dependsOn('my-company', 'dep1', 'jar', '1.0').dependencies

        then:
        dependencies != null
        dependencies.size() == 1
        dependencies.get(0) == [groupId: 'my-company', artifactId: 'dep1', version: 'jar', type: '1.0']
    }

    def "Check packaging for set packaging"() {
        when:
        String packaging = mavenLocalModule.hasPackaging('war').packaging

        then:
        packaging != null
        packaging == 'war'
    }

    def "Check packaging for no set packaging"() {
        when:
        String packaging = mavenLocalModule.packaging

        then:
        packaging == null
    }

    def "Check type for set type"() {
        when:
        String type = mavenLocalModule.hasType('war').type

        then:
        type != null
        type == 'war'
    }

    def "Check type for no set type"() {
        when:
        String type = mavenLocalModule.type

        then:
        type != null
        type == 'jar'
    }

    def "Sets non-unique snapshots"() {
        when:
        MavenModule mavenModule = mavenLocalModule.withNonUniqueSnapshots()

        then:
        mavenModule != null
    }

    def "On publishing SHA1 and MD5 files are created"() {
        given:
        TestFile pomTestFile = new TestFile(project.projectDir, "build/test/pom.xml")
        pomTestFile.createFile()

        when:
        mavenLocalModule.onPublish(pomTestFile)
        def testFiles = Arrays.asList(pomTestFile.parentFile.listFiles())

        then:
        !testFiles*.name.containsAll('pom.xml.md5', 'pom.xml.sha1')
    }

    def "Get artifact file for non-snapshot"() {
        when:
        TestFile artifactFile = mavenLocalModule.getArtifactFile([:])

        then:
        artifactFile != null
        artifactFile.name == 'my-artifact-1.0.jar'
    }

    def "Get artifact file for snapshot"() {
        when:
        TestFile artifactFile = snapshotMavenLocalModule.getArtifactFile()

        then:
        artifactFile != null
        artifactFile.name == 'my-artifact-1.0-SNAPSHOT.jar'
    }

    def "Get publish artifact version for non-snapshot"() {
        when:
        String version = mavenLocalModule.getPublishArtifactVersion()

        then:
        version == '1.0'
    }

    def "Get publish artifact version for unique snapshot"() {
        when:
        String version = snapshotMavenLocalModule.getPublishArtifactVersion()

        then:
        version == '1.0-SNAPSHOT'
    }

    def "Get publish artifact version for non-unique snapshot"() {
        given:
        snapshotMavenLocalModule.withNonUniqueSnapshots()

        when:
        String version = snapshotMavenLocalModule.getPublishArtifactVersion()

        then:
        version == '1.0-SNAPSHOT'
    }

    def "Publish artifacts for non-snapshot"() {
        when:
        MavenModule mavenModule = mavenLocalModule.publish()
        def publishedFiles = Arrays.asList(testFile.listFiles())

        then:
        mavenModule != null
        publishedFiles*.name.containsAll('my-artifact-1.0.jar', 'my-artifact-1.0.pom')
        !publishedFiles.find { it.name == 'maven-metadata.xml' }
        mavenLocalModule.assertArtifactsPublished('my-artifact-1.0.jar', 'my-artifact-1.0.pom')
    }

    def "Publish artifacts for unique snapshot"() {
        when:
        MavenModule mavenModule = snapshotMavenLocalModule.publish()
        def publishedFiles = Arrays.asList(testFile.listFiles())

        then:
        mavenModule != null
        publishedFiles*.name.containsAll('my-artifact-1.0-SNAPSHOT.jar', 'my-artifact-1.0-SNAPSHOT.pom')
        publishedFiles.find { it.name == 'maven-metadata.xml' }.exists()
        new XmlSlurper().parseText(publishedFiles.find { it.name == 'maven-metadata.xml' }.text).versioning.snapshot.localCopy.text() == 'true'
        snapshotMavenLocalModule.assertArtifactsPublished('my-artifact-1.0-SNAPSHOT.jar', 'my-artifact-1.0-SNAPSHOT.pom')
    }

    def "Publish artifacts for non-unique snapshot"() {
        given:
        snapshotMavenLocalModule.withNonUniqueSnapshots()

        when:
        MavenModule mavenModule = snapshotMavenLocalModule.publish()
        def publishedFiles = Arrays.asList(testFile.listFiles())

        then:
        mavenModule != null
        publishedFiles*.name.containsAll('my-artifact-1.0-SNAPSHOT.jar', 'my-artifact-1.0-SNAPSHOT.pom')
        publishedFiles.find { it.name == 'maven-metadata.xml' }.exists()
        new XmlSlurper().parseText(publishedFiles.find { it.name == 'maven-metadata.xml' }.text).versioning.snapshot.localCopy.text() == 'true'
        snapshotMavenLocalModule.assertArtifactsPublished('my-artifact-1.0-SNAPSHOT.jar', 'my-artifact-1.0-SNAPSHOT.pom')
    }
}
