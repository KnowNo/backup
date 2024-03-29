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
package org.gradle.plugins.ide.eclipse

import org.gradle.integtests.fixtures.TestResources
import org.gradle.util.TextUtil
import org.junit.Rule
import org.junit.Test
import spock.lang.Issue

class EclipseIntegrationTest extends AbstractEclipseIntegrationTest {
    private static String nonAscii = "\\u7777\\u8888\\u9999"

    @Rule
    public final TestResources testResources = new TestResources(testDirectoryProvider)

    @Test
    void canCreateAndDeleteMetaData() {
        File buildFile = testFile("master/build.gradle")
        usingBuildFile(buildFile).run()
    }

    @Test
    void sourceEntriesInClasspathFileAreSortedAsPerUsualConvention() {
        def expectedOrder = [
                "src/main/java",
                "src/main/groovy",
                "src/main/resources",
                "src/test/java",
                "src/test/groovy",
                "src/test/resources",
                "src/integTest/java",
                "src/integTest/groovy",
                "src/integTest/resources"
        ]

        expectedOrder.each { testFile(it).mkdirs() }

        runEclipseTask """
apply plugin: "java"
apply plugin: "groovy"
apply plugin: "eclipse"

sourceSets {
    integTest {
        resources { srcDir "src/integTest/resources" }
        java { srcDir "src/integTest/java" }
        groovy { srcDir "src/integTest/groovy" }
    }
}
        """

        def classpath = parseClasspathFile()
        def sourceEntries = findEntries(classpath, "src")
        assert sourceEntries*.@path == expectedOrder
    }

    @Test
    void outputDirDefaultsToEclipseDefault() {
        runEclipseTask("apply plugin: 'java'; apply plugin: 'eclipse'")

        def classpath = parseClasspathFile()

        def outputs = findEntries(classpath, "output")
        assert outputs*.@path == ["bin"]

        def sources = findEntries(classpath, "src")
        sources.each { assert !it.attributes().containsKey("path") }
    }

    @Test
    void canHandleCircularModuleDependencies() {
        def artifact1 = mavenRepo.module("myGroup", "myArtifact1", "1.0").dependsOn("myGroup", "myArtifact2", "1.0").publish().artifactFile
        def artifact2 = mavenRepo.module("myGroup", "myArtifact2", "1.0").dependsOn("myGroup", "myArtifact1", "1.0").publish().artifactFile

        runEclipseTask """
apply plugin: "java"
apply plugin: "eclipse"

repositories {
    maven { url "${mavenRepo.uri}" }
}

dependencies {
    compile "myGroup:myArtifact1:1.0"
}
        """

        libEntriesInClasspathFileHaveFilenames(artifact1.name, artifact2.name)
    }

    @Test
    void eclipseFilesAreWrittenWithUtf8Encoding() {
        runEclipseTask """
apply plugin: "war"
apply plugin: "eclipse-wtp"

eclipse {
    project.name = "$nonAscii"
    classpath {
        containers "$nonAscii"
    }

    wtp {
        component {
            deployName = "$nonAscii"
        }
        facet {
            facet name: "$nonAscii"
        }
    }
}
        """

        checkIsWrittenWithUtf8Encoding(getProjectFile())
        checkIsWrittenWithUtf8Encoding(getClasspathFile())
        checkIsWrittenWithUtf8Encoding(getComponentFile())
        checkIsWrittenWithUtf8Encoding(getFacetFile())
    }

    @Test
    void triggersBeforeAndWhenConfigurationHooks() {
        //this test is a bit peculiar as it has assertions inside the gradle script
        //couldn't find a better way of asserting on before/when configured hooks
        runEclipseTask('''
apply plugin: 'java'
apply plugin: 'war'
apply plugin: 'eclipse-wtp'

def beforeConfiguredObjects = 0
def whenConfiguredObjects = 0

eclipse {
    project {
        file {
            beforeMerged {beforeConfiguredObjects++ }
            whenMerged {whenConfiguredObjects++ }
        }
    }

    classpath {
        file {
            beforeMerged {beforeConfiguredObjects++ }
            whenMerged {whenConfiguredObjects++ }
        }
    }

    wtp.component {
        file {
            beforeMerged {beforeConfiguredObjects++ }
            whenMerged {whenConfiguredObjects++ }
        }
    }

    wtp.facet {
        file {
            beforeMerged {beforeConfiguredObjects++ }
            whenMerged {whenConfiguredObjects++ }
        }
    }

    jdt {
        file {
            beforeMerged {beforeConfiguredObjects++ }
            whenMerged {whenConfiguredObjects++ }
        }
    }
}

tasks.eclipse << {
    assert beforeConfiguredObjects == 5 : "beforeConfigured() hooks shoold be fired for domain model objects"
    assert whenConfiguredObjects == 5 : "whenConfigured() hooks shoold be fired for domain model objects"
}
''')

    }

    @Test
    void respectsPerConfigurationExcludes() {
        def artifact1 = mavenRepo.module("myGroup", "myArtifact1", "1.0").dependsOn("myGroup", "myArtifact2", "1.0").publish().artifactFile
        mavenRepo.module("myGroup", "myArtifact2", "1.0").publish()

        runEclipseTask """
apply plugin: 'java'
apply plugin: 'eclipse'

repositories {
    maven { url "${mavenRepo.uri}" }
}

configurations {
    compile.exclude module: 'myArtifact2'
}

dependencies {
    compile "myGroup:myArtifact1:1.0"
}
        """

        libEntriesInClasspathFileHaveFilenames(artifact1.name)
    }

    @Test
    void respectsPerDependencyExcludes() {
        def artifact1 = mavenRepo.module("myGroup", "myArtifact1", "1.0").dependsOn("myGroup", "myArtifact2", "1.0").publish().artifactFile
        mavenRepo.module("myGroup", "myArtifact2", "1.0").publish()

        runEclipseTask """
apply plugin: 'java'
apply plugin: 'eclipse'

repositories {
    maven { url "${mavenRepo.uri}" }
}

dependencies {
    compile("myGroup:myArtifact1:1.0") {
        exclude module: "myArtifact2"
    }
}
        """

        libEntriesInClasspathFileHaveFilenames(artifact1.name)
    }

    private void checkIsWrittenWithUtf8Encoding(File file) {
        def text = file.getText("UTF-8")
        assert text.contains('encoding="UTF-8"')
        String expectedNonAsciiChars = "\u7777\u8888\u9999"
        assert text.contains(expectedNonAsciiChars)
    }

    @Test
    void addsLinkToTheProjectFile() {
        runEclipseTask '''
apply plugin: 'java'
apply plugin: 'eclipse'

eclipse.project {
    linkedResource name: 'one', type: '2', location: '/xyz'
    linkedResource name: 'two', type: '3', locationUri: 'file://xyz'
}
'''

        def xml = parseProjectFile()
        assert xml.linkedResources.link[0].name.text() == 'one'
        assert xml.linkedResources.link[0].type.text() == '2'
        assert xml.linkedResources.link[0].location.text() == '/xyz'

        assert xml.linkedResources.link[1].name.text() == 'two'
        assert xml.linkedResources.link[1].type.text() == '3'
        assert xml.linkedResources.link[1].locationURI.text() == 'file://xyz'
    }

    @Test
    void allowsConfiguringJavaVersionWithSimpleTypes() {
        runEclipseTask '''
apply plugin: 'java'
apply plugin: 'eclipse'

eclipse.jdt {
    sourceCompatibility = '1.4'
    targetCompatibility = 1.3
}
'''

        def jdt = parseJdtFile()
        assert jdt.contains('source=1.4')
        assert jdt.contains('targetPlatform=1.3')
    }

    @Test
    void dslAllowsShortFormsForProject() {
        runEclipseTask '''
apply plugin: 'java'
apply plugin: 'eclipse'

eclipse.project.name = 'x'
assert eclipse.project.name == 'x'

eclipse {
    project.name += 'x'
    assert project.name == 'xx'
}

eclipse.project {
    name += 'x'
    assert name == 'xxx'
}

'''
    }

    @Test
    void dslAllowsShortForms() {
        runEclipseTask '''
apply plugin: 'java'
apply plugin: 'eclipse'

eclipse.classpath.downloadSources = false
assert eclipse.classpath.downloadSources == false

eclipse.classpath.file.withXml {}
eclipse.classpath {
    file.withXml {}
}
eclipse {
    classpath.file.withXml {}
}
'''
    }

    @Test
    @Issue("GRADLE-1157")
    void canHandleDependencyWithoutSourceJarInFlatDirRepo() {
        def repoDir = testDirectory.createDir("repo")
        repoDir.createFile("lib-1.0.jar")

        runEclipseTask """
apply plugin: "java"
apply plugin: "eclipse"

repositories {
	flatDir { dirs "${TextUtil.escapeString(repoDir)}" }
}

dependencies {
	compile "some:lib:1.0"
}
        """
    }

    @Test
    @Issue("GRADLE-1706") // doesn't prove that the issue is fixed because the test also passes with 1.0-milestone-4
    void canHandleDependencyWithoutSourceJarInMavenRepo() {
        mavenRepo.module("some", "lib", "1.0").publish()

        runEclipseTask """
apply plugin: "java"
apply plugin: "eclipse"

repositories {
    maven { url "${mavenRepo}" }
}

dependencies {
	compile "some:lib:1.0"
}
        """
    }
}
