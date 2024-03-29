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
package org.gradle.nativebinaries.language.cpp
import org.gradle.integtests.fixtures.Sample
import org.gradle.nativebinaries.language.cpp.fixtures.AbstractInstalledToolChainIntegrationSpec
import org.gradle.util.Requires
import org.gradle.util.TestPrecondition
import org.junit.Rule

@Requires(TestPrecondition.CAN_INSTALL_EXECUTABLE)
class CppSamplesIntegrationTest extends AbstractInstalledToolChainIntegrationSpec {
    @Rule public final Sample c = new Sample(temporaryFolder, 'native-binaries/c')
    @Rule public final Sample assembler = new Sample(temporaryFolder, 'native-binaries/assembler')
    @Rule public final Sample cpp = new Sample(temporaryFolder, 'native-binaries/cpp')
    @Rule public final Sample customLayout = new Sample(temporaryFolder, 'native-binaries/custom-layout')
    @Rule public final Sample cppExe = new Sample(temporaryFolder, 'native-binaries/cpp-exe')
    @Rule public final Sample cppLib = new Sample(temporaryFolder, 'native-binaries/cpp-lib')
    @Rule public final Sample multiProject = new Sample(temporaryFolder, 'native-binaries/multi-project')
    @Rule public final Sample flavors = new Sample(temporaryFolder, 'native-binaries/flavors')
    @Rule public final Sample dependencies = new Sample(temporaryFolder, 'native-binaries/dependencies')

    def setup() {
        toolChain.initialiseEnvironment()
    }
    def cleanup() {
        toolChain.resetEnvironment()
    }

    def "assembler"() {
        given:
        sample assembler

        when:
        run "installMainExecutable"

        then:
        nonSkippedTasks.count { it.startsWith(":assembleMainExecutable") } == 1
        executedAndNotSkipped ":compileMainExecutableMainC", ":linkMainExecutable", ":mainExecutable"

        and:
        installation("native-binaries/assembler/build/install/mainExecutable").exec().out == "5 + 7 = 12\n"
    }

    def "c"() {
        given:
        sample c
        
        when:
        run "installMainExecutable"
        
        then:
        executedAndNotSkipped ":compileHelloSharedLibraryHelloC", ":linkHelloSharedLibrary", ":helloSharedLibrary",
                              ":compileMainExecutableMainC", ":linkMainExecutable", ":mainExecutable"

        and:
        installation("native-binaries/c/build/install/mainExecutable").exec().out == "Hello world!"
    }

    def "cpp"() {
        given:
        sample cpp

        when:
        run "installMainExecutable"

        then:
        executedAndNotSkipped ":compileHelloSharedLibraryHelloCpp", ":linkHelloSharedLibrary", ":helloSharedLibrary",
                              ":compileMainExecutableMainCpp", ":linkMainExecutable", ":mainExecutable"

        and:
        installation("native-binaries/cpp/build/install/mainExecutable").exec().out == "Hello world!\n"
    }

    def "custom layout"() {
        given:
        sample customLayout

        when:
        run "installMainExecutable"

        then:
        executedAndNotSkipped ":compileHelloStaticLibraryHelloC", ":createHelloStaticLibrary", ":helloStaticLibrary",
                              ":compileMainExecutableMainCpp", ":linkMainExecutable", ":mainExecutable"

        and:
        installation("native-binaries/custom-layout/build/install/mainExecutable").exec().out == "Hello world!"
    }

    def "exe"() {
        given:
        sample cppExe

        when:
        run "installMain"

        then:
        executedAndNotSkipped ":compileMainExecutableMainCpp", ":linkMainExecutable", ":stripMainExecutable", ":mainExecutable"

        and:
        executable("native-binaries/cpp-exe/build/binaries/mainExecutable/sampleExe").exec().out == "Hello, World!\n"
        installation("native-binaries/cpp-exe/build/install/mainExecutable").exec().out == "Hello, World!\n"
    }

    def "lib"() {
        given:
        sample cppLib
        
        when:
        run "mainSharedLibrary"
        
        then:
        executedAndNotSkipped ":compileMainSharedLibraryMainCpp", ":linkMainSharedLibrary", ":mainSharedLibrary"
        
        and:
        sharedLibrary("native-binaries/cpp-lib/build/binaries/mainSharedLibrary/sampleLib").assertExists()
        
        when:
        sample cppLib
        run "mainStaticLibrary"
        
        then:
        executedAndNotSkipped ":compileMainStaticLibraryMainCpp", ":createMainStaticLibrary", ":mainStaticLibrary"
        
        and:
        staticLibrary("native-binaries/cpp-lib/build/binaries/mainStaticLibrary/sampleLib").assertExists()
    }

    def flavors() {
        when:
        sample flavors
        run "installEnglishMainExecutable"

        then:
        executedAndNotSkipped ":compileEnglishHelloSharedLibraryLibCpp", ":linkEnglishHelloSharedLibrary", ":englishHelloSharedLibrary"
        executedAndNotSkipped ":compileEnglishMainExecutableExeCpp", ":linkEnglishMainExecutable", ":englishMainExecutable"

        and:
        executable("native-binaries/flavors/build/binaries/mainExecutable/english/main").assertExists()
        sharedLibrary("native-binaries/flavors/build/binaries/helloSharedLibrary/english/hello").assertExists()

        and:
        installation("native-binaries/flavors/build/install/mainExecutable/english").exec().out == "Hello world!\n"

        when:
        sample flavors
        run "installFrenchMainExecutable"

        then:
        executedAndNotSkipped ":compileFrenchHelloSharedLibraryLibCpp", ":linkFrenchHelloSharedLibrary", ":frenchHelloSharedLibrary"
        executedAndNotSkipped ":compileFrenchMainExecutableExeCpp", ":linkFrenchMainExecutable", ":frenchMainExecutable"

        and:
        executable("native-binaries/flavors/build/binaries/mainExecutable/french/main").assertExists()
        sharedLibrary("native-binaries/flavors/build/binaries/helloSharedLibrary/french/hello").assertExists()

        and:
        installation("native-binaries/flavors/build/install/mainExecutable/french").exec().out == "Bonjour monde!\n"
    }

    def multiProject() {
        given:
        sample multiProject

        when:
        run "installMainExecutable"

        then:
        ":exe:mainExecutable" in executedTasks

        and:
        sharedLibrary("native-binaries/multi-project/lib/build/binaries/mainSharedLibrary/lib").assertExists()
        executable("native-binaries/multi-project/exe/build/binaries/mainExecutable/exe").assertExists()
        installation("native-binaries/multi-project/exe/build/install/mainExecutable").exec().out == "Hello, World!\n"
    }

    // Does not work on windows, due to GRADLE-2118
    @Requires(TestPrecondition.NOT_WINDOWS)
    def "dependencies"() {
        when:
        sample dependencies
        run ":lib:uploadArchives"

        then:
        sharedLibrary("native-binaries/dependencies/lib/build/binaries/mainSharedLibrary/lib").assertExists()
        file("native-binaries/dependencies/lib/build/repo/some-org/some-lib/1.0/some-lib-1.0-so.so").isFile()

        when:
        sample dependencies
        run ":exe:uploadArchives"

        then:
        ":exe:mainCppExtractHeaders" in nonSkippedTasks
        ":exe:mainExecutable" in nonSkippedTasks

        and:
        executable("native-binaries/dependencies/exe/build/binaries/mainExecutable/exe").assertExists()
        file("native-binaries/dependencies/exe/build/repo/dependencies/exe/1.0/exe-1.0.exe").exists()
    }

}