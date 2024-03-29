/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.api.internal.artifacts.ivyservice.dynamicversions;

import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ModuleVersionSelector;
import org.gradle.util.BuildCommencedTimeProvider;

class DefaultCachedModuleResolution implements ModuleResolutionCache.CachedModuleResolution {
    private final ModuleVersionSelector requestedVersion;
    private final ModuleVersionIdentifier resolvedVersion;
    private final long ageMillis;

    public DefaultCachedModuleResolution(ModuleVersionSelector requestedVersion, ModuleResolutionCacheEntry entry, BuildCommencedTimeProvider timeProvider) {
        this.requestedVersion = requestedVersion;
        this.resolvedVersion = entry.moduleVersionIdentifier;
        ageMillis = timeProvider.getCurrentTime() - entry.createTimestamp;
    }

    public ModuleVersionIdentifier getResolvedVersion() {
        return resolvedVersion;
    }

    public long getAgeMillis() {
        return ageMillis;
    }

    public boolean isDynamicVersion() {
        return !requestedVersion.matchesStrictly(resolvedVersion);
    }
}
