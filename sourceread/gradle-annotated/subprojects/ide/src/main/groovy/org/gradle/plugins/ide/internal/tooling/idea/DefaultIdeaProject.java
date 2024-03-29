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

import org.gradle.tooling.internal.protocol.InternalIdeaProject;
import org.gradle.tooling.model.HierarchicalElement;
import org.gradle.tooling.model.idea.IdeaLanguageLevel;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

public class DefaultIdeaProject implements InternalIdeaProject, Serializable {
    private String name;
    private String description;
    private Collection<DefaultIdeaModule> children = new LinkedList<DefaultIdeaModule>();
    private IdeaLanguageLevel languageLevel;
    private String jdkName;

    public IdeaLanguageLevel getLanguageLevel() {
        return languageLevel;
    }

    public DefaultIdeaProject setLanguageLevel(IdeaLanguageLevel languageLevel) {
        this.languageLevel = languageLevel;
        return this;
    }

    public String getJdkName() {
        return jdkName;
    }

    public DefaultIdeaProject setJdkName(String jdkName) {
        this.jdkName = jdkName;
        return this;
    }

    public String getName() {
        return name;
    }

    public DefaultIdeaProject setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public DefaultIdeaProject setDescription(String description) {
        this.description = description;
        return this;
    }

    public HierarchicalElement getParent() {
        return null;
    }

    public File getProjectDirectory() {
        throw new UnsupportedOperationException("This method should not be used.");
    }

    public String getPath() {
        throw new UnsupportedOperationException("This method should not be used.");
    }

    public DefaultIdeaProject setChildren(Collection<? extends DefaultIdeaModule> children) {
        this.children.clear();
        this.children.addAll(children);
        return this;
    }

    public Collection<DefaultIdeaModule> getChildren() {
        return children;
    }

    public Collection<DefaultIdeaModule> getModules() {
        return children;
    }

    @Override
    public String toString() {
        return "DefaultIdeaProject{"
                + " name='" + name + '\''
                + ", description='" + description + '\''
                + ", children count=" + children.size()
                + ", languageLevel='" + languageLevel + '\''
                + ", jdkName='" + jdkName + '\''
                + '}';
    }
}