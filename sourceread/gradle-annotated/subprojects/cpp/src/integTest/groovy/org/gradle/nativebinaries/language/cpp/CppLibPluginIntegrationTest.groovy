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

package org.gradle.nativebinaries.language.cpp

import org.gradle.nativebinaries.language.cpp.fixtures.AbstractInstalledToolChainIntegrationSpec

class CppLibPluginIntegrationTest extends AbstractInstalledToolChainIntegrationSpec {
    def "build simple c++ library that uses conventional layout"() {
        given:
        buildFile << """
            apply plugin: "cpp-lib"
        """
        settingsFile << "rootProject.name = 'test'"

        and:
        file("src/main/headers/helloworld.h") << """
            #ifdef _WIN32
            #define LIB_FUNC __declspec(dllexport)
            #else
            #define LIB_FUNC
            #endif

            void LIB_FUNC greeting();
        """

        file("src/main/cpp/helloworld.cpp") << """
            #include <iostream>
            #include "helloworld.h"

            void greeting() {
              std::cout << "Hello world!";
            }
        """

        when:
        run "mainSharedLibrary"

        then:
        sharedLibrary("build/binaries/mainSharedLibrary/test").assertExists()

        when:
        run "mainStaticLibrary"

        then:
        staticLibrary("build/binaries/mainStaticLibrary/test").assertExists()
    }
}
