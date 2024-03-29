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
package org.gradle.plugins.binaries.tasks

import org.gradle.api.Project
import org.gradle.api.plugins.ReportingBasePlugin
import org.gradle.api.plugins.ReportingBasePluginConvention
import org.gradle.api.reporting.ReportingExtension
import org.gradle.util.TestUtil
import spock.lang.Specification

public class ReportingBasePluginTest extends Specification {

    Project project = TestUtil.createRootProject();
    
    def setup() {
        project.plugins.apply(ReportingBasePlugin)
    }
    
    def addsTasksAndConventionToProject() {
        expect:
        project.convention.plugins.get("reportingBase") instanceof ReportingBasePluginConvention
    }
    
    def "adds reporting extension"() {
        expect:
        project.reporting instanceof ReportingExtension
        
        project.configure(project) {
            reporting {
                baseDir "somewhere"
            }
        }
    }
}
