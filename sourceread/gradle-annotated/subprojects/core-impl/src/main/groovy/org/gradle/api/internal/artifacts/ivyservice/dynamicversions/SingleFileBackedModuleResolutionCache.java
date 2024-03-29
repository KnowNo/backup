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
import org.gradle.api.internal.artifacts.DefaultModuleVersionSelector;
import org.gradle.api.internal.artifacts.ModuleVersionIdentifierSerializer;
import org.gradle.api.internal.artifacts.ivyservice.CacheLockingManager;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ModuleVersionRepository;
import org.gradle.cache.PersistentIndexedCache;
import org.gradle.messaging.serialize.Decoder;
import org.gradle.messaging.serialize.Encoder;
import org.gradle.messaging.serialize.Serializer;
import org.gradle.util.BuildCommencedTimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleFileBackedModuleResolutionCache implements ModuleResolutionCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleFileBackedModuleResolutionCache.class);

    private final BuildCommencedTimeProvider timeProvider;
    private final CacheLockingManager cacheLockingManager;
    private PersistentIndexedCache<RevisionKey, ModuleResolutionCacheEntry> cache;

    public SingleFileBackedModuleResolutionCache(BuildCommencedTimeProvider timeProvider, CacheLockingManager cacheLockingManager) {
        this.timeProvider = timeProvider;
        this.cacheLockingManager = cacheLockingManager;
    }

    private PersistentIndexedCache<RevisionKey, ModuleResolutionCacheEntry> getCache() {
        if (cache == null) {
            cache = initCache();
        }
        return cache;
    }

    private PersistentIndexedCache<RevisionKey, ModuleResolutionCacheEntry> initCache() {
        return cacheLockingManager.createCache("dynamic-revisions.bin", new RevisionKeySerializer(), new ModuleResolutionCacheEntrySerializer());
    }

    public void cacheModuleResolution(ModuleVersionRepository repository, ModuleVersionSelector requestedVersion, ModuleVersionIdentifier resolvedVersion) {
        if (requestedVersion.matchesStrictly(resolvedVersion)) {
            return;
        }

        LOGGER.debug("Caching resolved revision in dynamic revision cache: Will use '{}' for '{}'", resolvedVersion, requestedVersion);
        getCache().put(createKey(repository, requestedVersion), createEntry(resolvedVersion));
    }

    public CachedModuleResolution getCachedModuleResolution(ModuleVersionRepository repository, ModuleVersionSelector requestedVersion) {
        ModuleResolutionCacheEntry moduleResolutionCacheEntry = getCache().get(createKey(repository, requestedVersion));
        if (moduleResolutionCacheEntry == null) {
            return null;
        }
        return new DefaultCachedModuleResolution(requestedVersion, moduleResolutionCacheEntry, timeProvider);
    }

    private RevisionKey createKey(ModuleVersionRepository repository, ModuleVersionSelector requestedVersion) {
        return new RevisionKey(repository.getId(), requestedVersion);
    }

    private ModuleResolutionCacheEntry createEntry(ModuleVersionIdentifier moduleVersionIdentifier) {
        return new ModuleResolutionCacheEntry(moduleVersionIdentifier, timeProvider.getCurrentTime());
    }

    private static class RevisionKey {
        private final String repositoryId;
        private final ModuleVersionSelector requestedVersion;

        private RevisionKey(String repositoryId, ModuleVersionSelector requestedVersion) {
            this.repositoryId = repositoryId;
            this.requestedVersion = requestedVersion;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof RevisionKey)) {
                return false;
            }
            RevisionKey other = (RevisionKey) o;
            return repositoryId.equals(other.repositoryId) && requestedVersion.equals(other.requestedVersion);
        }

        @Override
        public int hashCode() {
            return repositoryId.hashCode() ^ requestedVersion.hashCode();
        }
    }

    private static class RevisionKeySerializer implements Serializer<RevisionKey> {
        public void write(Encoder encoder, RevisionKey value) throws Exception {
            encoder.writeString(value.repositoryId);
            encoder.writeString(value.requestedVersion.getGroup());
            encoder.writeString(value.requestedVersion.getName());
            encoder.writeString(value.requestedVersion.getVersion());
        }

        public RevisionKey read(Decoder decoder) throws Exception {
            String resolverId = decoder.readString();
            String group = decoder.readString();
            String module = decoder.readString();
            String version = decoder.readString();
            return new RevisionKey(resolverId, DefaultModuleVersionSelector.newSelector(group, module, version));
        }
    }

    private static class ModuleResolutionCacheEntrySerializer implements Serializer<ModuleResolutionCacheEntry> {
        private final ModuleVersionIdentifierSerializer identifierSerializer = new ModuleVersionIdentifierSerializer();

        public void write(Encoder encoder, ModuleResolutionCacheEntry value) throws Exception {
            identifierSerializer.write(encoder, value.moduleVersionIdentifier);
            encoder.writeLong(value.createTimestamp);
        }

        public ModuleResolutionCacheEntry read(Decoder decoder) throws Exception {
            ModuleVersionIdentifier identifier = identifierSerializer.read(decoder);
            long createTimestamp = decoder.readLong();
            return new ModuleResolutionCacheEntry(identifier, createTimestamp);
        }
    }

}
