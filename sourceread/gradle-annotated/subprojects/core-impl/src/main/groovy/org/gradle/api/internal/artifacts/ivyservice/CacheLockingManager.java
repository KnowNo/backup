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
package org.gradle.api.internal.artifacts.ivyservice;

import net.jcip.annotations.ThreadSafe;
import org.gradle.api.internal.filestore.PathKeyFileStore;
import org.gradle.cache.CacheAccess;
import org.gradle.cache.PersistentIndexedCache;
import org.gradle.messaging.serialize.Serializer;

/**
 * Provides synchronized access to the artifact cache.
 */
@ThreadSafe
public interface CacheLockingManager extends ArtifactCacheMetaData, CacheAccess {
    /**
     * Creates a cache implementation that is managed by this locking manager. This method may be used at any time.
     *
     * <p>The returned cache may only be used by an action being run from {@link #useCache(String, org.gradle.internal.Factory)}.
     * In this instance, an exclusive lock will be held on the cache.
     *
     * <p>The returned cache may not be used by an action being run from {@link #longRunningOperation(String, org.gradle.internal.Factory)}.
     */
    <K, V> PersistentIndexedCache<K, V> createCache(String cacheFile, Serializer<K> keySerializer, Serializer<V> valueSerializer);

    /**
     * Creates the file store location relative to the base cache directory.
     *
     * @return File store location
     */
    PathKeyFileStore createFileStore();

    /**
     * Creates the metadata store location relative to the base cache directory.
     *
     * @return Metadata store location
     */
    PathKeyFileStore createMetaDataStore();
}
