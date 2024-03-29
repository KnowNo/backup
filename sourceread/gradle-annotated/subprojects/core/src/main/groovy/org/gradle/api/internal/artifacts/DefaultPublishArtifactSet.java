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
package org.gradle.api.internal.artifacts;

import org.gradle.api.DomainObjectSet;
import org.gradle.api.artifacts.PublishArtifactSet;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.DelegatingDomainObjectSet;
import org.gradle.api.internal.file.AbstractFileCollection;
import org.gradle.api.internal.tasks.AbstractTaskDependency;
import org.gradle.api.internal.tasks.TaskDependencyInternal;
import org.gradle.api.internal.tasks.TaskDependencyResolveContext;
import org.gradle.api.tasks.TaskDependency;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

public class DefaultPublishArtifactSet extends DelegatingDomainObjectSet<PublishArtifact> implements PublishArtifactSet {
    private final TaskDependencyInternal builtBy = new ArtifactsTaskDependency();
    private final ArtifactsFileCollection files = new ArtifactsFileCollection();
    private final String displayName;

    public DefaultPublishArtifactSet(String displayName, DomainObjectSet<PublishArtifact> backingSet) {
        super(backingSet);
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public FileCollection getFiles() {
        return files;
    }

    public TaskDependency getBuildDependencies() {
        return builtBy;
    }

    private class ArtifactsFileCollection extends AbstractFileCollection {

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public TaskDependency getBuildDependencies() {
            return builtBy;
        }

        public Set<File> getFiles() {
            Set<File> files = new LinkedHashSet<File>();
            for (PublishArtifact artifact : DefaultPublishArtifactSet.this) {
                files.add(artifact.getFile());
            }
            return files;
        }
    }

    private class ArtifactsTaskDependency extends AbstractTaskDependency {
        public void resolve(TaskDependencyResolveContext context) {
            for (PublishArtifact publishArtifact : DefaultPublishArtifactSet.this) {
                context.add(publishArtifact);
            }
        }
    }
}
