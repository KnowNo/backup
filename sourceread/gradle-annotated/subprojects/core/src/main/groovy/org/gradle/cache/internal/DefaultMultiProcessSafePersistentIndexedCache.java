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
package org.gradle.cache.internal;

import org.gradle.cache.PersistentIndexedCache;
import org.gradle.cache.internal.btree.BTreePersistentIndexedCache;
import org.gradle.internal.Factory;

public class DefaultMultiProcessSafePersistentIndexedCache<K, V> implements MultiProcessSafePersistentIndexedCache<K, V> {
    private final FileAccess fileAccess;
    private final Factory<BTreePersistentIndexedCache<K, V>> factory;
    private BTreePersistentIndexedCache<K, V> cache;

    public DefaultMultiProcessSafePersistentIndexedCache(Factory<BTreePersistentIndexedCache<K, V>> factory, FileAccess fileAccess) {
        this.factory = factory;
        this.fileAccess = fileAccess;
    }

    public V get(final K key) {
        final PersistentIndexedCache<K, V> cache = getCache();
        try {
            return fileAccess.readFile(new Factory<V>() {
                public V create() {
                    return cache.get(key);
                }
            });
        } catch (FileIntegrityViolationException e) {
            return null;
        }
    }

    public void put(final K key, final V value) {
        final PersistentIndexedCache<K, V> cache = getCache();
        // Use writeFile because the cache can internally recover from datafile
        // corruption, so we don't care at this level if it's corrupt
        fileAccess.writeFile(new Runnable() {
            public void run() {
                cache.put(key, value);
            }
        });
    }

    public void remove(final K key) {
        final PersistentIndexedCache<K, V> cache = getCache();
        // Use writeFile because the cache can internally recover from datafile
        // corruption, so we don't care at this level if it's corrupt
        fileAccess.writeFile(new Runnable() {
            public void run() {
                cache.remove(key);
            }
        });
    }

    public void onStartWork(String operationDisplayName, boolean lockHasNewOwner) {
    }

    public void onEndWork() {
        close();
    }

    public void close() {
        if (cache != null) {
            try {
                fileAccess.writeFile(new Runnable() {
                    public void run() {
                        cache.close();
                    }
                });
            } finally {
                cache = null;
            }
        }
    }

    private PersistentIndexedCache<K, V> getCache() {
        if (cache == null) {
            // Use writeFile because the cache can internally recover from datafile
            // corruption, so we don't care at this level if it's corrupt
            fileAccess.writeFile(new Runnable() {
                public void run() {
                    cache = factory.create();
                }
            });
        }
        return cache;
    }
}
