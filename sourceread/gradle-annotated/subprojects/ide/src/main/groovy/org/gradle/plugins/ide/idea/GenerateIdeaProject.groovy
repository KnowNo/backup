/*
 * Copyright 2010 the original author or authors.
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
package org.gradle.plugins.ide.idea

import org.gradle.plugins.ide.api.XmlGeneratorTask
import org.gradle.plugins.ide.idea.model.IdeaProject
import org.gradle.plugins.ide.idea.model.Project

/**
 * Generates an IDEA project file for root project *only*. If you want to fine tune the idea configuration
 * <p>
 * At this moment nearly all configuration is done via {@link IdeaProject}.
 */
public class GenerateIdeaProject extends XmlGeneratorTask<Project> {

    /**
     * idea project model
     */
    IdeaProject ideaProject;

    @Override protected void configure(Project xmlModule) {
        getIdeaProject().mergeXmlProject(xmlModule)
    }

    @Override Project create() {
        def project = new Project(xmlTransformer, ideaProject.pathFactory)
        return project
    }

    /**
     * output *.ipr file
     */
    File getOutputFile() {
        return ideaProject.outputFile
    }

    void setOutputFile(File newOutputFile) {
        ideaProject.outputFile = newOutputFile
    }
}
