/*
 * Copyright 2012 the original author or authors.
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

package org.gradle.api.artifacts.result;

import org.gradle.api.Incubating;

/**
 * A dependency that was resolved successfully.
 */
@Incubating
public interface ResolvedDependencyResult extends DependencyResult {
    /**
     * Returns the selected module version. This may not necessarily be the same as the requested module version. For example, a dynamic version
     * may have been requested, or the version may have been substituted due to conflict resolution, or by being forced, or for some other reason.
     */
    ResolvedModuleVersionResult getSelected();
}
