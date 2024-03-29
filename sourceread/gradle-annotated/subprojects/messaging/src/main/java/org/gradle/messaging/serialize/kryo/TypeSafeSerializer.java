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

package org.gradle.messaging.serialize.kryo;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.gradle.messaging.serialize.ObjectReader;
import org.gradle.messaging.serialize.ObjectWriter;

public class TypeSafeSerializer<T> implements KryoAwareSerializer<Object> {
    private final Class<T> type;
    private final KryoAwareSerializer<T> serializer;

    public TypeSafeSerializer(Class<T> type, KryoAwareSerializer<T> serializer) {
        this.type = type;
        this.serializer = serializer;
    }

    public ObjectReader<Object> newReader(Input input) {
        final ObjectReader<T> reader = serializer.newReader(input);
        return new ObjectReader<Object>() {
            public Object read() throws Exception {
                return reader.read();
            }
        };
    }

    public ObjectWriter<Object> newWriter(final Output output) {
        final ObjectWriter<T> writer = serializer.newWriter(output);
        return new ObjectWriter<Object>() {
            public void write(Object value) throws Exception {
                writer.write(type.cast(value));
            }
        };
    }
}
