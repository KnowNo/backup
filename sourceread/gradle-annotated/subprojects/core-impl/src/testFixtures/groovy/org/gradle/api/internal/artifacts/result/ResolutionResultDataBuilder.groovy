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

package org.gradle.api.internal.artifacts.result

import org.gradle.api.artifacts.result.ModuleVersionSelectionReason
import org.gradle.api.internal.artifacts.ivyservice.ModuleVersionResolveException
import org.gradle.api.internal.artifacts.ivyservice.resolveengine.result.VersionSelectionReasons

import static org.gradle.api.internal.artifacts.DefaultModuleVersionIdentifier.newId
import static org.gradle.api.internal.artifacts.DefaultModuleVersionSelector.newSelector

class ResolutionResultDataBuilder {

    static DefaultResolvedDependencyResult newDependency(String group='a', String module='a', String version='1', String selectedVersion='1') {
        new DefaultResolvedDependencyResult(newSelector(group, module, version), newModule(group, module, selectedVersion), newModule())
    }

    static DefaultUnresolvedDependencyResult newUnresolvedDependency(String group='x', String module='x', String version='1', String selectedVersion='1') {
        def requested = newSelector(group, module, version)
        new DefaultUnresolvedDependencyResult(requested, VersionSelectionReasons.REQUESTED, newModule(group, module, selectedVersion), new ModuleVersionResolveException(requested, "broken"))
    }

    static DefaultResolvedModuleVersionResult newModule(String group='a', String module='a', String version='1',
                                                        ModuleVersionSelectionReason selectionReason = VersionSelectionReasons.REQUESTED) {
        new DefaultResolvedModuleVersionResult(newId(group, module, version), selectionReason)
    }
}
