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

package org.gradle.groovy.scripts

import org.gradle.integtests.fixtures.AbstractIntegrationSpec

class StatementLabelsIntegrationTest extends AbstractIntegrationSpec {
    def "use of statement label in build script is flagged"() {
        buildFile << """
version: '1.0'
        """

        expect:
        executer.withDeprecationChecksDisabled()
        succeeds("tasks")
        output.contains("Usage of statement labels in build scripts has been deprecated")
        output.contains("version")

        // try again to make sure that warning sticks if build script is cached
        executer.withDeprecationChecksDisabled()
        succeeds("tasks")
        output.contains("Usage of statement labels in build scripts has been deprecated")
        output.contains("version")
    }

    def "all usages of statement labels are flagged"() {
        buildFile << """
version: '1.0'
group = "foo"
description: "bar"
        """

        expect:
        executer.withDeprecationChecksDisabled()
        succeeds("tasks")
        output.contains("Usage of statement labels in build scripts has been deprecated")
        output.contains("version")
        !output.contains("group")
        output.contains("description")
    }

    def "nested use of statement label in build script is flagged"() {
        buildFile << """
def foo() {
    1.times {
      for (i in 1..1) {
        another: "label"
      }
    }
}
        """

        expect:
        executer.withDeprecationChecksDisabled()
        succeeds("tasks")
        output.contains("Usage of statement labels in build scripts has been deprecated")
        output.contains("label")
    }

    def "use of statement label in class inside build script is allowed"() {
        buildFile << """
class Foo {
  def bar() {
    mylabel:
    def x = 1
  }
}
        """

        expect:
        succeeds("tasks")
    }
}
