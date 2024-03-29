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

package org.gradle.api.internal.artifacts.ivyservice.modulecache;

import org.apache.ivy.core.module.descriptor.Artifact;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.parser.DescriptorParseContext;
import org.gradle.api.internal.externalresource.LocallyAvailableExternalResource;

/**
 * Context used for parsing cached module descriptor files.
 * Will only be used for parsing ivy.xml files, as pom files are converted before caching.
 */
class CachedModuleDescriptorParseContext implements DescriptorParseContext {
    public ModuleRevisionId getCurrentRevisionId() {
        throw new UnsupportedOperationException();
    }

    public boolean artifactExists(Artifact artifact) {
        throw new UnsupportedOperationException();
    }

    public LocallyAvailableExternalResource getArtifact(Artifact artifact) {
        throw new UnsupportedOperationException();
    }
}
