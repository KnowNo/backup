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

package org.gradle.test.fixtures.concurrent

/**
 * A target for blocking. Exposes each named instant as a property, and accessing that property will block until the
 * instant has been reached.
 */
class BlockTarget {
    private final Instants instants

    BlockTarget(Instants instants) {
        this.instants = instants
    }

    def getProperty(String name) {
        instants.waitFor(name)
        return null
    }
}
