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
import org.gradle.api.artifacts.ModuleVersionSelector;

/**
 * A dependency that could not be resolved.
 */
@Incubating
public interface UnresolvedDependencyResult extends DependencyResult {
    /**
     * Returns the module version selector that was attempted to be resolved. This may not be the same as the requested module version.
     */
    ModuleVersionSelector getAttempted();

    /**
     * Returns the reasons why the failed selector was attempted.
     */
    ModuleVersionSelectionReason getAttemptedReason();

    /**
     * The failure that occurred.
     */
    Throwable getFailure();
}
