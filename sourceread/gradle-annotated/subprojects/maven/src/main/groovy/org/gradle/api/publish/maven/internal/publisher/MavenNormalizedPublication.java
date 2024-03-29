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

package org.gradle.api.publish.maven.internal.publisher;

import org.gradle.api.publish.maven.MavenArtifact;

import java.io.File;
import java.util.Set;

public class MavenNormalizedPublication {

    private final String name;
    private final File pomFile;
    private final MavenProjectIdentity projectIdentity;
    private final Set<MavenArtifact> artifacts;

    public MavenNormalizedPublication(String name, File pomFile, MavenProjectIdentity projectIdentity, Set<MavenArtifact> artifacts) {
        this.name = name;
        this.pomFile = pomFile;
        this.projectIdentity = projectIdentity;
        this.artifacts = artifacts;
    }

    public String getName() {
        return name;
    }

    public File getPomFile() {
        return pomFile;
    }

    public Set<MavenArtifact> getArtifacts() {
        return artifacts;
    }

    public MavenProjectIdentity getProjectIdentity() {
        return projectIdentity;
    }
}
