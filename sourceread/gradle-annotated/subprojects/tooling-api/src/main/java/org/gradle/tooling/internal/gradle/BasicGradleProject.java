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

package org.gradle.tooling.internal.gradle;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

public class BasicGradleProject extends PartialBasicGradleProject {
    private File projectDirectory;
    private Set<BasicGradleProject> children = new LinkedHashSet<BasicGradleProject>();


    public File getProjectDirectory() {
        return projectDirectory;
    }

    public BasicGradleProject setProjectDirectory(File projectDirectory) {
        this.projectDirectory = projectDirectory;
        return this;
    }

    public BasicGradleProject setPath(String path) {
        super.setPath(path);
        return this;
    }

    public BasicGradleProject setName(String name) {
        super.setName(name);
        return this;
    }

    public Set<? extends BasicGradleProject> getChildren() {
        return children;
    }

    public BasicGradleProject addChild(BasicGradleProject child) {
        children.add(child);
        return this;
    }
}
