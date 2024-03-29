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

package org.gradle.api.internal.tasks.testing.testng;

import org.gradle.api.tasks.testing.testng.TestNGOptions;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class TestNGSpec implements Serializable {
    private static final long serialVersionUID = 1;

    private final String defaultSuiteName;
    private final String defaultTestName;
    private final String parallel;
    private final int threadCount;
    private final String annotations;
    private final boolean javadocAnnotations;
    private final List testResources;
    private final boolean useDefaultListener;
    private final Set<String> includeGroups;
    private final Set<String> excludeGroups;
    private final Set<String> listeners;

    public TestNGSpec(TestNGOptions options) {
        this.defaultSuiteName = options.getSuiteName();
        this.defaultTestName = options.getTestName();
        this.parallel = options.getParallel();
        this.threadCount = options.getThreadCount();
        this.annotations = options.getAnnotations();
        this.javadocAnnotations = options.getJavadocAnnotations();
        this.testResources = options.getTestResources();
        this.useDefaultListener = options.getUseDefaultListeners();
        this.includeGroups = options.getIncludeGroups();
        this.excludeGroups = options.getExcludeGroups();
        this.listeners = options.getListeners();
    }

    public Set<String> getListeners() {
        return listeners;
    }

    public Set<String> getExcludeGroups() {
        return excludeGroups;
    }

    public Set<String> getIncludeGroups() {
        return includeGroups;
    }

    public boolean getUseDefaultListeners() {
        return useDefaultListener;
    }

    public List getTestResources() {
        return testResources;
    }

    public boolean getJavadocAnnotations() {
        return javadocAnnotations;
    }

    public String getAnnotations() {
        return annotations;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public String getParallel() {
        return parallel;
    }

    public String getDefaultTestName() {
        return defaultTestName;
    }

    public String getDefaultSuiteName() {
        return defaultSuiteName;
    }
}
