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
package org.gradle.launcher.daemon.client;

import org.gradle.internal.concurrent.ExecutorFactory;
import org.gradle.internal.id.CompositeIdGenerator;
import org.gradle.internal.id.IdGenerator;
import org.gradle.internal.id.LongIdGenerator;
import org.gradle.internal.id.UUIDGenerator;
import org.gradle.internal.nativeplatform.ProcessEnvironment;
import org.gradle.internal.nativeplatform.services.NativeServices;
import org.gradle.internal.service.DefaultServiceRegistry;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.launcher.daemon.context.DaemonCompatibilitySpec;
import org.gradle.launcher.daemon.context.DaemonContext;
import org.gradle.launcher.daemon.context.DaemonContextBuilder;
import org.gradle.launcher.daemon.registry.DaemonRegistry;
import org.gradle.logging.internal.OutputEventListener;
import org.gradle.messaging.remote.internal.OutgoingConnector;
import org.gradle.messaging.remote.internal.inet.TcpOutgoingConnector;

import java.io.InputStream;

/**
 * Some support wiring for daemon clients.
 * 
 * @see DaemonClientServices
 * @see EmbeddedDaemonClientServices
 */
abstract public class DaemonClientServicesSupport extends DefaultServiceRegistry {
    private final InputStream buildStandardInput;

    public DaemonClientServicesSupport(ServiceRegistry loggingServices, InputStream buildStandardInput) {
        super(NativeServices.getInstance(), loggingServices);
        this.buildStandardInput = buildStandardInput;
    }

    protected InputStream getBuildStandardInput() {
        return buildStandardInput;
    }

    protected DaemonClient createDaemonClient() {
        DaemonCompatibilitySpec matchingContextSpec = new DaemonCompatibilitySpec(get(DaemonContext.class));
        return new DaemonClient(
                get(DaemonConnector.class),
                get(OutputEventListener.class),
                matchingContextSpec,
                buildStandardInput,
                get(ExecutorFactory.class),
                get(IdGenerator.class));
    }

    protected DaemonContext createDaemonContext() {
        DaemonContextBuilder builder = new DaemonContextBuilder(get(ProcessEnvironment.class));
        configureDaemonContextBuilder(builder);
        return builder.create();
    }

    // subclass hook, allowing us to fake the context for testing
    protected void configureDaemonContextBuilder(DaemonContextBuilder builder) {
        
    }

    protected IdGenerator<?> createIdGenerator() {
        return new CompositeIdGenerator(new UUIDGenerator().generateId(), new LongIdGenerator());
    }

    protected OutgoingConnector createOutgoingConnector() {
        return new TcpOutgoingConnector();
    }

    protected DaemonConnector createDaemonConnector() {
        return new DefaultDaemonConnector(get(DaemonRegistry.class), get(OutgoingConnector.class), get(DaemonStarter.class));
    }
}