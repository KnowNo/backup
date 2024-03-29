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
package org.gradle.language.jvm.internal

import org.gradle.api.file.SourceDirectorySet
import org.gradle.language.base.FunctionalSourceSet
import spock.lang.Specification

class DefaultResourceSetTest extends Specification {
    def "has useful String representation"() {
        def functionalSourceSet = Stub(FunctionalSourceSet) {
            getName() >> "mainX"
        }
        def resourceSet = new DefaultResourceSet("resourcesX", Stub(SourceDirectorySet), functionalSourceSet)

        expect:
        resourceSet.toString() == "resources 'mainX:resourcesX'"
    }
}
