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
package org.gradle.api.internal.artifacts.ivyservice.resolveengine.result

import org.gradle.api.internal.cache.BinaryStore

public class DummyBinaryStore implements BinaryStore {

    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream()
    private DataOutputStream output = new DataOutputStream(bytes)

    void write(BinaryStore.WriteAction write) {
        write.write(output)
    }

    BinaryStore.BinaryData done() {
        new BinaryStore.BinaryData() {
            DataInputStream input
            def <T> T read(BinaryStore.ReadAction<T> readAction) {
                if (input == null) {
                    input = new DataInputStream(new ByteArrayInputStream(bytes.toByteArray()))
                }
                readAction.read(input)
            }

            void done() {
                input = null
            }
        }
    }
}