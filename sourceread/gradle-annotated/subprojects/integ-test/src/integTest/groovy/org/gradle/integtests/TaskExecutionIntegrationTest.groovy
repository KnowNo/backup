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

package org.gradle.integtests
import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.util.TextUtil
import org.junit.Test
import spock.lang.Issue

import static org.hamcrest.Matchers.startsWith

public class TaskExecutionIntegrationTest extends AbstractIntegrationSpec {
    
    def taskCanAccessTaskGraph() {
        buildFile << """
    boolean notified = false
    task a(dependsOn: 'b') << { task ->
        assert notified
        assert gradle.taskGraph.hasTask(task)
        assert gradle.taskGraph.hasTask(':a')
        assert gradle.taskGraph.hasTask(a)
        assert gradle.taskGraph.hasTask(':b')
        assert gradle.taskGraph.hasTask(b)
        assert gradle.taskGraph.allTasks.contains(task)
        assert gradle.taskGraph.allTasks.contains(tasks.getByName('b'))
    }
    task b
    gradle.taskGraph.whenReady { graph ->
        assert graph.hasTask(':a')
        assert graph.hasTask(a)
        assert graph.hasTask(':b')
        assert graph.hasTask(b)
        assert graph.allTasks.contains(a)
        assert graph.allTasks.contains(b)
        notified = true
    }
"""
        when:
        succeeds "a"
        
        then:
        result.assertTasksExecuted(":b", ":a");
    }

    def executesAllTasksInASingleBuildAndEachTaskAtMostOnce() {
        buildFile << """
    gradle.taskGraph.whenReady { assert !project.hasProperty('graphReady'); ext.graphReady = true }
    task a << { task -> project.ext.executedA = task }
    task b << { 
        assert a == project.executedA
        assert gradle.taskGraph.hasTask(':a')
    }
    task c(dependsOn: a)
    task d(dependsOn: a)
    task e(dependsOn: [a, d]);
"""
        expect:
        run("a", "b").assertTasksExecuted(":a", ":b");
        run("a", "a").assertTasksExecuted(":a");
        run("c", "a").assertTasksExecuted(":a", ":c");
        run("c", "e").assertTasksExecuted(":a", ":c", ":d", ":e");
    }

    def executesMultiProjectsTasksInASingleBuildAndEachTaskAtMostOnce() {
        settingsFile << "include 'child1', 'child2', 'child1-2', 'child1-2-2'"
        buildFile << """
    task a
    allprojects {
        task b
        task c(dependsOn: ['b', ':a'])
    };
"""
        
        expect:
        run("a", "c").assertTasksExecuted(":a", ":b", ":c", ":child1:b", ":child1:c", ":child1-2:b", ":child1-2:c", ":child1-2-2:b", ":child1-2-2:c", ":child2:b", ":child2:c");
        run("b", ":child2:c").assertTasksExecuted(":b", ":child1:b", ":child1-2:b", ":child1-2-2:b", ":child2:b", ":a", ":child2:c");
    }

    @Test
    def executesMultiProjectDefaultTasksInASingleBuildAndEachTaskAtMostOnce() {
        settingsFile << "include 'child1', 'child2'"
        buildFile << """
    defaultTasks 'a', 'b'
    task a
    subprojects {
        task a(dependsOn: ':a')
        task b(dependsOn: ':a')
    }
"""

        expect:
        run().assertTasksExecuted(":a", ":child1:a", ":child2:a", ":child1:b", ":child2:b");
    }

    def executesProjectDefaultTasksWhenNoneSpecified() {
        buildFile << """
    task a
    task b(dependsOn: a)
    defaultTasks 'b'
"""
        expect:
        run().assertTasksExecuted(":a", ":b");
    }
    
    def doesNotExecuteTaskActionsWhenDryRunSpecified() {
        buildFile << """
    task a << { fail() }
    task b(dependsOn: a) << { fail() }
    defaultTasks 'b'
"""

        expect:
        // project defaults
        executer.withArguments("-m").run().assertTasksExecuted(":a", ":b");
        // named tasks
        executer.withArguments("-m").withTasks("b").run().assertTasksExecuted(":a", ":b");
    }

    def executesTaskActionsInCorrectEnvironment() {
        buildFile << """
    // An action attached to built-in task
    task a << { assert Thread.currentThread().contextClassLoader == getClass().classLoader }

    // An action defined by a custom task
    task b(type: CustomTask)
    class CustomTask extends DefaultTask {
        @TaskAction def go() {
            assert Thread.currentThread().contextClassLoader == getClass().classLoader
        }
    }

    // An action implementation
    task c
    c.doLast new Action<Task>() {
        void execute(Task t) {
            assert Thread.currentThread().contextClassLoader == getClass().classLoader
        }
    }
"""
        expect:
        succeeds("a", "b", "c")
    }

    def excludesTasksWhenExcludePatternSpecified() {
        settingsFile << "include 'sub'"
        buildFile << """
    task a
    task b(dependsOn: a)
    task c(dependsOn: [a, b])
    task d(dependsOn: c)
    defaultTasks 'd'
"""
        file("sub/build.gradle") << """
    task c
    task d(dependsOn: c)
"""

        expect:
        // Exclude entire branch
        executer.withTasks(":d").withArguments("-x", "c").run().assertTasksExecuted(":d");
        // Exclude direct dependency
        executer.withTasks(":d").withArguments("-x", "b").run().assertTasksExecuted(":a", ":c", ":d");
        // Exclude using paths and multi-project
        executer.withTasks("d").withArguments("-x", "c").run().assertTasksExecuted(":d", ":sub:d");
        executer.withTasks("d").withArguments("-x", "sub:c").run().assertTasksExecuted(":a", ":b", ":c", ":d", ":sub:d");
        executer.withTasks("d").withArguments("-x", ":sub:c").run().assertTasksExecuted(":a", ":b", ":c", ":d", ":sub:d");
        executer.withTasks("d").withArguments("-x", "d").run().assertTasksExecuted();
        // Project defaults
        executer.withArguments("-x", "b").run().assertTasksExecuted(":a", ":c", ":d", ":sub:c", ":sub:d");
        // Unknown task
        executer.withTasks("d").withArguments("-x", "unknown").runWithFailure().assertThatDescription(startsWith("Task 'unknown' not found in root project"));
    }

    @Issue("http://issues.gradle.org/browse/GRADLE-2022")
    def tryingToInstantiateTaskDirectlyFailsWithGoodErrorMessage() {
        buildFile << """
    new DefaultTask()
"""
        when:
        fails "tasks"

        then:
        failure.assertHasCause("Task of type 'org.gradle.api.DefaultTask' has been instantiated directly which is not supported")
    }

    def "sensible error message for circular task dependency"() {
        buildFile << """
    task a(dependsOn: 'b')
    task b(dependsOn: 'a')
"""
        when:
        fails 'b'

        then:
        failure.assertHasDescription TextUtil.toPlatformLineSeparators("""Circular dependency between the following tasks:
:a
\\--- :b
     \\--- :a (*)

(*) - details omitted (listed previously)""")
    }

    def "placeolder actions not triggered when not requested"() {
        when:
        buildFile << """
        task a
        tasks.addPlaceholderAction("b") {
            throw new RuntimeException()
        }
"""
        then:
        succeeds 'a'
    }

    def "explicit tasks are preferred over placeholder actions"() {
        buildFile << """
        task someTask << {println "explicit sometask"}
        tasks.addPlaceholderAction("someTask"){
            println  "placeholder action triggered"
            task someTask << {println "placeholder sometask"}
        }
"""
        when:
        succeeds 'sometask'

        then:
        output.contains("explicit sometask")
        !output.contains("placeholder action triggered")

        when:
        succeeds 'someT'

        then:
        output.contains("explicit sometask")
        !output.contains("placeholder action triggered")
    }


    def "honours mustRunAfter task ordering"() {
        buildFile << """
    task a {
        mustRunAfter 'b'
    }
    task b
    task c(dependsOn: ['a', 'b'])
    task d
    c.mustRunAfter d

"""
        when:
        succeeds 'c', 'd'

        then:
        result.assertTasksExecuted(':d', ':b', ':a', ':c')
    }

    def "finalizer task is executed if a finalized task is executed"() {
        buildFile << """
    task a
    task b {
        doLast {}
        finalizedBy a
    }
"""
        when:
        succeeds 'b'

        then:
        ":a" in executedTasks
    }

    def "finalizer task is executed even if the finalised task fails"() {
        buildFile << """
    task a
    task b  {
        doLast { throw new RuntimeException() }
        finalizedBy a
    }
"""
        when:
        fails 'b'

        then:
        ":a" in executedTasks
    }

    def "finalizer task is not executed if the finalized task does not run"() {
        buildFile << """
    task a {
        doLast { throw new RuntimeException() }
    }
    task b
    task c {
        doLast {}
        dependsOn a
        finalizedBy b
        onlyIf { false }
    }
"""
        when:
        fails 'c'

        then:
        !(":b" in executedTasks)
    }

    def "sensible error message for circular task dependency due to mustRunAfter"() {
        buildFile << """
    task a {
        mustRunAfter 'b'
    }
    task b(dependsOn: 'a')
"""
        when:
        fails 'b'

        then:
        failure.assertHasDescription TextUtil.toPlatformLineSeparators("""Circular dependency between the following tasks:
:a
\\--- :b
     \\--- :a (*)

(*) - details omitted (listed previously)""")
    }
}
