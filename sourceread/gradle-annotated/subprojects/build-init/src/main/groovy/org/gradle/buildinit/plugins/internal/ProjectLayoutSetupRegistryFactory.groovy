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

package org.gradle.buildinit.plugins.internal

import org.gradle.api.internal.DocumentationRegistry
import org.gradle.api.internal.artifacts.mvnsettings.MavenSettingsProvider
import org.gradle.api.internal.file.FileResolver

class ProjectLayoutSetupRegistryFactory {
    private final DocumentationRegistry documentationRegistry
    private final MavenSettingsProvider mavenSettingsProvider
    private final FileResolver fileResolver

    public ProjectLayoutSetupRegistryFactory(MavenSettingsProvider mavenSettingsProvider,
                                             DocumentationRegistry documentationRegistry,
                                             FileResolver fileResolver) {
        this.mavenSettingsProvider = mavenSettingsProvider
        this.documentationRegistry = documentationRegistry
        this.fileResolver = fileResolver
    }

    ProjectLayoutSetupRegistry createProjectLayoutSetupRegistry() {
        DefaultTemplateLibraryVersionProvider libraryVersionProvider = new DefaultTemplateLibraryVersionProvider();
        ProjectLayoutSetupRegistry registry = new ProjectLayoutSetupRegistry()
        // TODO maybe referencing the implementation class here is enough and instantiation
        // should be defererred when descriptor is requested.
        SingleBuildSettingsInitDescriptor singleProjectSettingsDescriptor = new SingleBuildSettingsInitDescriptor(fileResolver, documentationRegistry)
        registry.add(BuildInitTypeIds.BASIC, new BasicProjectInitDescriptor(fileResolver, documentationRegistry, singleProjectSettingsDescriptor));
        registry.add(BuildInitTypeIds.JAVA_LIBRARY, new JavaLibraryProjectInitDescriptor(libraryVersionProvider, fileResolver, documentationRegistry, singleProjectSettingsDescriptor));
        registry.add(BuildInitTypeIds.POM, new PomProjectInitDescriptor(fileResolver, mavenSettingsProvider))
        registry.add(BuildInitTypeIds.GROOVY_LIBRARY, new GroovyLibraryProjectInitDescriptor(libraryVersionProvider, fileResolver, documentationRegistry, singleProjectSettingsDescriptor))
        registry.add(BuildInitTypeIds.SCALA_LIBRARY, new ScalaLibraryProjectInitDescriptor(libraryVersionProvider, fileResolver, documentationRegistry, singleProjectSettingsDescriptor))
        return registry
    }

}
