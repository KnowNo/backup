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

package org.gradle.initialization.buildsrc;

import org.gradle.BuildAdapter;
import org.gradle.api.internal.GradleInternal;
import org.gradle.api.internal.plugins.EmbeddableJavaProject;
import org.gradle.api.invocation.Gradle;
import org.gradle.initialization.ModelConfigurationListener;
import org.gradle.util.WrapUtil;

import java.io.File;
import java.util.Collection;
import java.util.Set;

public class BuildSrcBuildListenerFactory {

    private static final String DEFAULT_BUILD_SOURCE_SCRIPT_RESOURCE = "defaultBuildSourceScript.txt";

    Listener create(boolean rebuild) {
        return new Listener(rebuild);
    }

    public static class Listener extends BuildAdapter implements ModelConfigurationListener {
        private Set<File> classpath;
        private final boolean rebuild;

        public Listener(boolean rebuild) {
            this.rebuild = rebuild;
        }

        @Override
        public void projectsLoaded(Gradle gradle) {
            gradle.getRootProject().apply(WrapUtil.toMap("from", BuildSrcBuildListenerFactory.class.getResource(DEFAULT_BUILD_SOURCE_SCRIPT_RESOURCE)));
        }

        public Collection<File> getRuntimeClasspath() {
            return classpath;
        }

        public void onConfigure(GradleInternal gradle) {
            EmbeddableJavaProject projectInfo = gradle.getRootProject().getConvention().getPlugin(EmbeddableJavaProject.class);
            gradle.getStartParameter().setTaskNames(rebuild ? projectInfo.getRebuildTasks() : projectInfo.getBuildTasks());
            classpath = projectInfo.getRuntimeClasspath().getFiles();
        }
    }
}
