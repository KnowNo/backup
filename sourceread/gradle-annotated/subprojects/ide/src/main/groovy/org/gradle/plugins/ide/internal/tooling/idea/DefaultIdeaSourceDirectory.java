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

package org.gradle.plugins.ide.internal.tooling.idea;

import org.gradle.tooling.model.idea.IdeaSourceDirectory;

import java.io.File;
import java.io.Serializable;

public class DefaultIdeaSourceDirectory implements IdeaSourceDirectory, Serializable {

    private File directory;

    public File getDirectory() {
        return directory;
    }

    public DefaultIdeaSourceDirectory setDirectory(File directory) {
        this.directory = directory;
        return this;
    }

    @Override
    public String toString() {
        return "DefaultIdeaSourceDirectory{"
                + "directory=" + directory
                + '}';
    }
}
