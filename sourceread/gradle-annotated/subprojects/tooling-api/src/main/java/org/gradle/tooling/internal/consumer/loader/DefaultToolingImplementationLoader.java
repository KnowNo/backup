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
package org.gradle.tooling.internal.consumer.loader;

import org.gradle.internal.Factory;
import org.gradle.internal.classloader.FilteringClassLoader;
import org.gradle.internal.classloader.MultiParentClassLoader;
import org.gradle.internal.classloader.MutableURLClassLoader;
import org.gradle.internal.classpath.ClassPath;
import org.gradle.internal.service.ServiceLocator;
import org.gradle.logging.ProgressLoggerFactory;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.UnsupportedVersionException;
import org.gradle.tooling.internal.adapter.ProtocolToModelAdapter;
import org.gradle.tooling.internal.consumer.Distribution;
import org.gradle.tooling.internal.consumer.connection.*;
import org.gradle.tooling.internal.consumer.converters.ConsumerTargetTypeProvider;
import org.gradle.tooling.internal.consumer.parameters.ConsumerConnectionParameters;
import org.gradle.tooling.internal.consumer.versioning.ModelMapping;
import org.gradle.tooling.internal.protocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultToolingImplementationLoader implements ToolingImplementationLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultToolingImplementationLoader.class);
    private final ClassLoader classLoader;

    public DefaultToolingImplementationLoader() {
        this(DefaultToolingImplementationLoader.class.getClassLoader());
    }

    DefaultToolingImplementationLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ConsumerConnection create(Distribution distribution, ProgressLoggerFactory progressLoggerFactory, ConsumerConnectionParameters connectionParameters) {
        LOGGER.debug("Using tooling provider from {}", distribution.getDisplayName());
        ClassLoader classLoader = createImplementationClassLoader(distribution, progressLoggerFactory);
        ServiceLocator serviceLocator = new ServiceLocator(classLoader);
        try {
            Factory<ConnectionVersion4> factory = serviceLocator.findFactory(ConnectionVersion4.class);
            if (factory == null) {
                return new NoToolingApiConnection(distribution);
            }
            // ConnectionVersion4 is a part of the protocol and cannot be easily changed.
            ConnectionVersion4 connection = factory.create();

            ProtocolToModelAdapter adapter = new ProtocolToModelAdapter(new ConsumerTargetTypeProvider());
            ModelMapping modelMapping = new ModelMapping();

            // Adopting the connection to a refactoring friendly type that the consumer owns
            AbstractConsumerConnection adaptedConnection;
            if (connection instanceof ModelBuilder && connection instanceof InternalBuildActionExecutor) {
                adaptedConnection = new ActionAwareConsumerConnection(connection, modelMapping, adapter);
            } else if (connection instanceof ModelBuilder) {
                adaptedConnection = new ModelBuilderBackedConsumerConnection(connection, modelMapping, adapter);
            } else if (connection instanceof BuildActionRunner) {
                adaptedConnection = new BuildActionRunnerBackedConsumerConnection(connection, modelMapping, adapter);
            } else if (connection instanceof InternalConnection) {
                adaptedConnection = new InternalConnectionBackedConsumerConnection(connection, modelMapping, adapter);
            } else {
                adaptedConnection = new ConnectionVersion4BackedConsumerConnection(connection, modelMapping, adapter);
            }
            adaptedConnection.configure(connectionParameters);
            return adaptedConnection;
        } catch (UnsupportedVersionException e) {
            throw e;
        } catch (Throwable t) {
            throw new GradleConnectionException(String.format("Could not create an instance of Tooling API implementation using the specified %s.", distribution.getDisplayName()), t);
        }
    }

    private ClassLoader createImplementationClassLoader(Distribution distribution, ProgressLoggerFactory progressLoggerFactory) {
        ClassPath implementationClasspath = distribution.getToolingImplementationClasspath(progressLoggerFactory);
        LOGGER.debug("Using tooling provider classpath: {}", implementationClasspath);
        // On IBM JVM 5, ClassLoader.getResources() uses a combination of findResources() and getParent() and traverses the hierarchy rather than just calling getResources()
        // Wrap our real classloader in one that hides the parent.
        // TODO - move this into FilteringClassLoader
        MultiParentClassLoader parentObfuscatingClassLoader = new MultiParentClassLoader(classLoader);
        FilteringClassLoader filteringClassLoader = new FilteringClassLoader(parentObfuscatingClassLoader);
        filteringClassLoader.allowPackage("org.gradle.tooling.internal.protocol");
        return new MutableURLClassLoader(filteringClassLoader, implementationClasspath.getAsURLArray()) {
            @Override
            public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                //TODO:ADAM - remove this.
                if (name.startsWith("com.sun.jdi.")) {
                    System.out.println(String.format("=> Loading JDI class %s in provider ClassLoader. Should not be.", name));
                }
                return super.loadClass(name, resolve);
            }
        };
    }
}
