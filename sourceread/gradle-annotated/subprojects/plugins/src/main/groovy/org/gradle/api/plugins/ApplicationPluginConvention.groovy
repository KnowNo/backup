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
package org.gradle.api.plugins

import org.gradle.api.Project
import org.gradle.api.file.CopySpec

/**
 * <p>A {@link Convention} used for the ApplicationPlugin.</p>
 */
class ApplicationPluginConvention {
    /**
     * The name of the application.
     */
    String applicationName

    /**
     * The fully qualified name of the application's main class.
     */
    String mainClassName

    /**
     * Array of string arguments to pass to the JVM when running the application
     */
    Iterable<String> applicationDefaultJvmArgs = []

    /**
     * <p>The specification of the contents of the distribution.</p>
     * <p>
     * Use this {@link org.gradle.api.file.CopySpec} to include extra files/resource in the application distribution.
     * <pre autoTested=''>
     * apply plugin: 'application'
     *
     * applicationDistribution.from("some/dir") {
     *   include "*.txt"
     * }
     * </pre>
     * <p>
     * Note that the application plugin pre configures this spec to; include the contents of "{@code src/dist}",
     * copy the application start scripts into the "{@code bin}" directory, and copy the built jar and its dependencies
     * into the "{@code lib}" directory.
     */
    CopySpec applicationDistribution

    final Project project

    ApplicationPluginConvention(Project project) {
        this.project = project
        applicationDistribution = project.copySpec {}
    }

}
