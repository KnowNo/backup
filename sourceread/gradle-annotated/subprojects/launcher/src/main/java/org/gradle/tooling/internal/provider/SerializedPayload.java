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

package org.gradle.tooling.internal.provider;

import java.io.Serializable;

public class SerializedPayload implements Serializable {
    private final byte[] serializedModel;
    private final Object header;

    public SerializedPayload(Object header, byte[] serializedModel) {
        this.header = header;
        this.serializedModel = serializedModel;
    }

    public Object getHeader() {
        return header;
    }

    public byte[] getSerializedModel() {
        return serializedModel;
    }
}
