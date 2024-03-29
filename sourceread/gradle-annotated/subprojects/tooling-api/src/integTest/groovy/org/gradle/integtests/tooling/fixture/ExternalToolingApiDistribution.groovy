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

package org.gradle.integtests.tooling.fixture

import org.gradle.internal.classloader.DefaultClassLoaderFactory
import org.gradle.util.GradleVersion

class ExternalToolingApiDistribution implements ToolingApiDistribution {
    private final GradleVersion version
    private final Collection<File> classpath

    ExternalToolingApiDistribution(String version, Collection<File> classpath) {
        this.version = GradleVersion.version(version)
        this.classpath = classpath
    }

    GradleVersion getVersion() {
        version
    }
    
    Collection<File> getClasspath() {
        classpath
    }
    
    ClassLoader getClassLoader() {
        def classLoaderFactory = new DefaultClassLoaderFactory()
        classLoaderFactory.createIsolatedClassLoader(classpath.collect { it.toURI() })
    }
    
    String toString() {
        "Tooling API $version.version"
    }
}
