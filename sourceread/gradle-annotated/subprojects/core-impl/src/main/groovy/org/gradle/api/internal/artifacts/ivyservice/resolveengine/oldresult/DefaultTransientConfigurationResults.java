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

package org.gradle.api.internal.artifacts.ivyservice.resolveengine.oldresult;

import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.ResolvedDependency;
import org.gradle.api.internal.artifacts.DefaultResolvedDependency;
import org.gradle.api.internal.artifacts.ResolvedConfigurationIdentifier;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultTransientConfigurationResults implements TransientConfigurationResults {

    final Map<ModuleDependency, ResolvedDependency> firstLevelDependencies = new LinkedHashMap<ModuleDependency, ResolvedDependency>();
    ResolvedDependency root;
    final Map<ResolvedConfigurationIdentifier, DefaultResolvedDependency> allDependencies = new HashMap<ResolvedConfigurationIdentifier, DefaultResolvedDependency>();

    public Map<ModuleDependency, ResolvedDependency> getFirstLevelDependencies() {
        return firstLevelDependencies;
    }

    public ResolvedDependency getRoot() {
        return root;
    }

    public ResolvedDependency getResolvedDependency(ResolvedConfigurationIdentifier id) {
        return allDependencies.get(id);
    }
}
