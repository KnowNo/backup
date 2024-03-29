/*
 * Copyright 2009 the original author or authors.
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
package org.gradle.api.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.reporting.ReportingExtension;

/**
 * <p>A {@link Plugin} which provides the basic skeleton for reporting.</p>
 *
 * <p>This plugin adds the following convention objects to the project:</p>
 *
 * <ul>
 *
 * <li>{@link ReportingBasePluginConvention}</li>
 *
 * </ul>
 */
public class ReportingBasePlugin implements Plugin<ProjectInternal> {
    public void apply(ProjectInternal project) {
        Convention convention = project.getConvention();
        ReportingExtension extension = project.getExtensions().create(ReportingExtension.NAME, ReportingExtension.class, project);

        // This convention is deprecated
        convention.getPlugins().put("reportingBase", new ReportingBasePluginConvention(project, extension));
    }
}
