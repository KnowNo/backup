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

package org.gradle.tooling.model.internal.outcomes;

import org.gradle.tooling.model.DomainObjectSet;
import org.gradle.tooling.model.HierarchicalElement;

import java.io.File;

/**
 * The outputs produced by a Gradle project.
 */
public interface ProjectOutcomes extends HierarchicalElement {
    ProjectOutcomes getParent();
    DomainObjectSet<ProjectOutcomes> getChildren();
    String getPath();
    File getProjectDirectory();
    DomainObjectSet<? extends GradleBuildOutcome> getOutcomes();
}
