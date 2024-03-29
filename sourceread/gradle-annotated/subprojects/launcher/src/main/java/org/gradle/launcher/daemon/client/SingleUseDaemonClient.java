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

package org.gradle.launcher.daemon.client;

import org.gradle.api.internal.DocumentationRegistry;
import org.gradle.api.internal.specs.ExplainingSpec;
import org.gradle.api.internal.specs.ExplainingSpecs;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.initialization.BuildAction;
import org.gradle.internal.concurrent.ExecutorFactory;
import org.gradle.internal.id.IdGenerator;
import org.gradle.launcher.daemon.context.DaemonContext;
import org.gradle.launcher.daemon.protocol.Build;
import org.gradle.launcher.daemon.protocol.BuildAndStop;
import org.gradle.launcher.exec.BuildActionParameters;
import org.gradle.logging.internal.OutputEventListener;

import java.io.InputStream;

public class SingleUseDaemonClient extends DaemonClient {
    public static final String MESSAGE = "To honour the JVM settings for this build a new JVM will be forked.";
    private static final Logger LOGGER = Logging.getLogger(SingleUseDaemonClient.class);
    private final DocumentationRegistry documentationRegistry;

    public SingleUseDaemonClient(DaemonConnector connector, OutputEventListener outputEventListener, ExplainingSpec<DaemonContext> compatibilitySpec, InputStream buildStandardInput,
                                 ExecutorFactory executorFactory, IdGenerator<?> idGenerator, DocumentationRegistry documentationRegistry) {
        super(connector, outputEventListener, compatibilitySpec, buildStandardInput, executorFactory, idGenerator);
        this.documentationRegistry = documentationRegistry;
    }

    @Override
    public <T> T execute(BuildAction<T> action, BuildActionParameters parameters) {
        LOGGER.lifecycle("{} Please consider using the daemon: {}.", MESSAGE, documentationRegistry.getDocumentationFor("gradle_daemon"));
        Build build = new BuildAndStop(getIdGenerator().generateId(), action, parameters);

        DaemonClientConnection daemonConnection = getConnector().startDaemon(ExplainingSpecs.<DaemonContext>satisfyAll());

        return (T) executeBuild(build, daemonConnection);
    }
}
