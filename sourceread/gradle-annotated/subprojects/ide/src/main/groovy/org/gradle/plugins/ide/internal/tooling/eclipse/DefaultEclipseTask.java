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
package org.gradle.plugins.ide.internal.tooling.eclipse;

import org.gradle.tooling.internal.protocol.eclipse.EclipseProjectVersion3;
import org.gradle.tooling.internal.protocol.eclipse.EclipseTaskVersion1;

import java.io.Serializable;

public class DefaultEclipseTask implements EclipseTaskVersion1, Serializable {
    private final EclipseProjectVersion3 project;
    private final String path;
    private final String name;
    private final String description;

    public DefaultEclipseTask(EclipseProjectVersion3 project, String path, String name, String description) {
        this.project = project;
        this.path = path;
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("task '%s'", path);
    }

    public EclipseProjectVersion3 getProject() {
        return project;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
