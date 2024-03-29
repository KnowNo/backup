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

package org.gradle.api.internal.externalresource.ivy;

import org.gradle.api.internal.artifacts.metadata.ModuleVersionArtifactIdentifier;

public class ArtifactAtRepositoryKey {
    private final String repositoryId;
    private final ModuleVersionArtifactIdentifier artifactId;

    public ArtifactAtRepositoryKey(String repositoryId, ModuleVersionArtifactIdentifier artifactId) {
        this.repositoryId = repositoryId;
        this.artifactId = artifactId;
    }

    public ModuleVersionArtifactIdentifier getArtifactId() {
        return artifactId;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    @Override
    public String toString() {
        return repositoryId + ":" + artifactId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ArtifactAtRepositoryKey)) {
            return false;
        }
        ArtifactAtRepositoryKey other = (ArtifactAtRepositoryKey) o;
        return repositoryId.equals(other.repositoryId) && artifactId.equals(other.artifactId);
    }

    @Override
    public int hashCode() {
        return repositoryId.hashCode() ^ artifactId.hashCode();
    }
}
