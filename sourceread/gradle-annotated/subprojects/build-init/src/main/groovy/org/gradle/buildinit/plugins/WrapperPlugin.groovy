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



package org.gradle.buildinit.plugins

import org.gradle.api.Incubating
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.wrapper.Wrapper

@Incubating
class WrapperPlugin implements Plugin<Project> {
    void apply(Project project) {
        Task wrapper = project.tasks.create("wrapper", Wrapper)
        wrapper.group = BuildInitPlugin.GROUP
        wrapper.description = "Generates Gradle wrapper files. [incubating]"
    }
}
