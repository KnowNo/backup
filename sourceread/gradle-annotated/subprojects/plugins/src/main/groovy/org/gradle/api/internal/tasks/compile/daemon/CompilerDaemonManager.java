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
package org.gradle.api.internal.tasks.compile.daemon;

import net.jcip.annotations.ThreadSafe;
import org.gradle.BuildAdapter;
import org.gradle.BuildResult;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.internal.CompositeStoppable;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls the lifecycle of the compiler daemon and provides access to it.
 */
@ThreadSafe
public class CompilerDaemonManager implements CompilerDaemonFactory {
    private static final Logger LOGGER = Logging.getLogger(CompilerDaemonManager.class);
    private final Object lock = new Object();

    private static final CompilerDaemonManager INSTANCE = new CompilerDaemonManager(new CompilerDaemonStarter());
    
    private final List<CompilerDaemonClient> clients = new ArrayList<CompilerDaemonClient>();
    private CompilerDaemonStarter compilerDaemonStarter;

    CompilerDaemonManager(CompilerDaemonStarter compilerDaemonStarter) {
        this.compilerDaemonStarter = compilerDaemonStarter;
    }

    public static CompilerDaemonManager getInstance() {
        return INSTANCE;
    }

    public CompilerDaemon getDaemon(ProjectInternal project, DaemonForkOptions forkOptions) {
        synchronized (lock) {
            if (clients.isEmpty()) {
                registerStopOnBuildFinished(project);
            }

            for (CompilerDaemonClient client: clients) {
                if (client.isCompatibleWith(forkOptions) && client.isIdle()) {
                    client.setIdle(false);
                    return client;
                }
            }
        }

        //allow the daemon to be started concurrently
        CompilerDaemonClient client =
                compilerDaemonStarter.startDaemon(project, forkOptions)
                .setIdle(false);

        synchronized (lock) {
            clients.add(client);
        }
        return client;
    }

    private void stop() {
        LOGGER.debug("Stopping {} Gradle compiler daemon(s).", clients.size());
        CompositeStoppable.stoppable(clients).stop();
        LOGGER.info("Stopped {} Gradle compiler daemon(s).", clients.size());
        clients.clear();
    }

    private void registerStopOnBuildFinished(ProjectInternal project) {
        project.getGradle().addBuildListener(new BuildAdapter() {
            @Override
            public void buildFinished(BuildResult result) {
                stop();
            }
        });
    }
}
