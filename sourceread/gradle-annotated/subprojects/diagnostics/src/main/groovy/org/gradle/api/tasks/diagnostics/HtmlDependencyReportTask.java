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

package org.gradle.api.tasks.diagnostics;

import org.gradle.api.*;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.VersionMatcher;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.diagnostics.internal.html.HtmlDependencyReporter;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Generates an HTML dependency report. This report
 * combines the features of the ASCII dependency report and those of the ASCII
 * dependency insight report. For a given project, it generates a tree of the dependencies
 * of every configuration, and each dependency can be clicked to show the insight of
 * this dependency.
 * <p>
 * This task generates a report for the task's containing project by default. But it can also generate
 * a report for multiple projects, by setting the value of the
 * <code>projects</code> property. Here's how to generate an HTML
 * dependency report for all the projects of a multi-project build, for example:
 * <pre>
 * htmlDependencyReport {
 *     projects = project.allprojects
 * }
 * </pre>
 * <p>
 * The report is generated in the <code>build/reports/project/dependencies</code> directory by default.
 * This can also be changed by setting the <code>outputDirectory</code>
 * property.
 */
@Incubating
public class HtmlDependencyReportTask extends ConventionTask {

    private Set<Project> projects;
    private File outputDirectory;

    public HtmlDependencyReportTask() {
        getOutputs().upToDateWhen(new Spec<Task>() {
            public boolean isSatisfiedBy(Task element) {
                return false;
            }
        });
    }

    @TaskAction
    public void generate() {
        try {
            HtmlDependencyReporter reporter = new HtmlDependencyReporter(getServices().get(VersionMatcher.class));
            reporter.setOutputDirectory(getOutputDirectory());
            reporter.generate(getProjects());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Returns the directory which the report will be written to.
     *
     * @return The output directory. May not be null.
     */
    @OutputDirectory
    public File getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * Sets the diretcory which the report will be written to.
     * @param outputDirectory The output directory. May not be null.
     */
    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Returns the set of projects to generate a report for. By default, the report is generated for the task's
     * containing project.
     *
     * @return The set of files.
     */
    public Set<Project> getProjects() {
        return projects;
    }

    /**
     * Specifies the set of projects to generate this report for.
     *
     * @param projects The set of projects. Must not be null.
     */
    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }
}
