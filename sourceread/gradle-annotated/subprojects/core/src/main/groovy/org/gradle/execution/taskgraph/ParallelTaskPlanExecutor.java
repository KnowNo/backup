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

package org.gradle.execution.taskgraph;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionListener;
import org.gradle.api.internal.changedetection.state.TaskArtifactStateCacheAccess;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.internal.concurrent.DefaultExecutorFactory;
import org.gradle.internal.concurrent.ExecutorFactory;
import org.gradle.internal.concurrent.StoppableExecutor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

class ParallelTaskPlanExecutor extends AbstractTaskPlanExecutor {
    private static final Logger LOGGER = Logging.getLogger(ParallelTaskPlanExecutor.class);
    private final int executorCount;
    private final TaskArtifactStateCacheAccess cacheAccess;

    public ParallelTaskPlanExecutor(TaskArtifactStateCacheAccess cacheAccess, int numberOfParallelExecutors) {
        this.cacheAccess = cacheAccess;
        if (numberOfParallelExecutors < 1) {
            throw new IllegalArgumentException("Not a valid number of parallel executors: " + numberOfParallelExecutors);
        }

        this.executorCount = numberOfParallelExecutors;
    }

    public void process(final TaskExecutionPlan taskExecutionPlan, final TaskExecutionListener taskListener) {
        // The main thread holds the lock for the task cache. Need to release the lock while executing the tasks.
        // This locking needs to be pushed down closer to the things that need the lock and removed from here.
        cacheAccess.longRunningOperation("Executing all tasks", new Runnable() {
            public void run() {
                DefaultExecutorFactory factory = new DefaultExecutorFactory();
                try {
                    doProcess(taskExecutionPlan, taskListener, factory);
                    taskExecutionPlan.awaitCompletion();
                } finally {
                    factory.stop();
                }
            }
        });
    }

    private void doProcess(TaskExecutionPlan taskExecutionPlan, TaskExecutionListener taskListener, ExecutorFactory factory) {
        List<Project> projects = getAllProjects(taskExecutionPlan);
        int numExecutors = Math.min(executorCount, projects.size());

        LOGGER.info("Using {} parallel executor threads", numExecutors);

        for (int i = 0; i < numExecutors; i++) {
            Runnable worker = taskWorker(taskExecutionPlan, taskListener);
            StoppableExecutor executor = factory.create("Task worker " + (i+1));
            // TODO A bunch more stuff to contextualise the thread
            executor.execute(worker);
        }
    }

    private List<Project> getAllProjects(TaskExecutionPlan taskExecutionPlan) {
        final Set<Project> uniqueProjects = new LinkedHashSet<Project>();
        for (Task task : taskExecutionPlan.getTasks()) {
            uniqueProjects.add(task.getProject());
        }
        return new ArrayList<Project>(uniqueProjects);
    }
}
