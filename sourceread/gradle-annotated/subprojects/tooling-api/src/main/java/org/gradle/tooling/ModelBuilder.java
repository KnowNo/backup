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
package org.gradle.tooling;

import org.gradle.api.Incubating;
import org.gradle.tooling.exceptions.UnsupportedBuildArgumentException;
import org.gradle.tooling.exceptions.UnsupportedOperationConfigurationException;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A {@code ModelBuilder} allows you to fetch a snapshot of the model for a project.
 * Instances of {@code ModelBuilder} are not thread-safe.
 * <p>
 * You use a {@code ModelBuilder} as follows:
 *
 * <ul>
 * <li>Create an instance of {@code ModelBuilder} by calling {@link org.gradle.tooling.ProjectConnection#model(Class)}.
 * <li>Configure the builder as appropriate.
 * <li>Call either {@link #get()} or {@link #get(ResultHandler)} to build the model.
 * <li>Optionally, you can reuse the builder to build the model multiple times.
 * </ul>
 *
 * Example:
 * <pre autoTested=''>
 * ProjectConnection connection = GradleConnector.newConnector()
 *    .forProjectDirectory(new File("someFolder"))
 *    .connect();
 *
 * try {
 *    ModelBuilder&lt;GradleProject&gt; builder = connection.model(GradleProject.class);
 *
 *    //if you use a different than usual build file name:
 *    builder.withArguments("--build-file", "theBuild.gradle");
 *
 *    //configure the standard input in case your build is interactive:
 *    builder.setStandardInput(new ByteArrayInputStream("consume this!".getBytes()));
 *
 *    //if you want to listen to the progress events:
 *    ProgressListener listener = null; // use your implementation
 *    builder.addProgressListener(listener);
 *
 *    //get the model:
 *    GradleProject project = builder.get();
 *
 *    //query the model for information:
 *    System.out.println("Available tasks: " + project.getTasks());
 * } finally {
 *    connection.close();
 * }
 * </pre>
 *
 * @param <T> The type of model to build
 * @since 1.0-milestone-3
 */
public interface ModelBuilder<T> extends LongRunningOperation {

    /**
     * {@inheritDoc}
     * @since 1.0
     */
    ModelBuilder<T> withArguments(String ... arguments);

    /**
     * {@inheritDoc}
     * @since 1.0-milestone-3
     */
    ModelBuilder<T> setStandardOutput(OutputStream outputStream);

    /**
     * {@inheritDoc}
     * @since 1.0-milestone-3
     */
    ModelBuilder<T> setStandardError(OutputStream outputStream);

    /**
     * {@inheritDoc}
     * @since 1.0-milestone-7
     */
    ModelBuilder<T> setStandardInput(InputStream inputStream);

    /**
     * {@inheritDoc}
     * @since 1.0-milestone-8
     */
    ModelBuilder<T> setJavaHome(File javaHome);

    /**
     * {@inheritDoc}
     * @since 1.0-milestone-9
     */
    ModelBuilder<T> setJvmArguments(String... jvmArguments);

    /**
     * {@inheritDoc}
     * @since 1.0-milestone-3
     */
    ModelBuilder<T> addProgressListener(ProgressListener listener);

    /**
     * Specifies the tasks to execute before building the model. By default, no tasks are executed.
     *
     * @param tasks The paths of the tasks to be executed. Relative paths are evaluated relative to the project for which this launcher was created.
     * @return this
     *
     * @since 1.2
     */
    @Incubating
    ModelBuilder<T> forTasks(String... tasks);

    /**
     * Fetch the model, blocking until it is available.
     *
     * @return The model.
     * @throws UnsupportedVersionException When the target Gradle version does not support building models.
     * @throws UnknownModelException When the target Gradle version or build does not support the requested model.
     * @throws UnsupportedOperationConfigurationException
     *          When the target Gradle version does not support some requested configuration option such as
     *          {@link #setStandardInput(java.io.InputStream)}, {@link #setJavaHome(java.io.File)},
     *          {@link #setJvmArguments(String...)}.
     * @throws UnsupportedBuildArgumentException When there is a problem with build arguments provided by {@link #withArguments(String...)}.
     * @throws BuildException On some failure executing the Gradle build.
     * @throws GradleConnectionException On some other failure using the connection.
     * @throws IllegalStateException When the connection has been closed or is closing.
     * @since 1.0-milestone-3
     */
    T get() throws GradleConnectionException, UnsupportedVersionException, UnknownModelException, UnsupportedOperationConfigurationException, BuildException, IllegalStateException, UnsupportedBuildArgumentException;

    /**
     * Starts fetching the model, passing the result to the given handler when complete. This method returns immediately, and the result is later passed to the given
     * handler's {@link ResultHandler#onComplete(Object)} method.
     *
     * <p>If the operation fails, the handler's {@link ResultHandler#onFailure(GradleConnectionException)}
     * method is called with the appropriate exception. See {@link #get()} for a description of the various exceptions that the operation may fail with.
     *
     * @param handler The handler to supply the result to.
     * @throws IllegalStateException When the connection has been closed or is closing.
     * @since 1.0-milestone-3
     */
    void get(ResultHandler<? super T> handler) throws IllegalStateException;
}
