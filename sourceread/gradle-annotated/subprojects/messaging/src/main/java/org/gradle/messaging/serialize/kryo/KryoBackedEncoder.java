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

package org.gradle.messaging.serialize.kryo;

import com.esotericsoftware.kryo.io.Output;
import org.gradle.api.Nullable;
import org.gradle.messaging.serialize.AbstractEncoder;
import org.gradle.messaging.serialize.Encoder;

import java.io.IOException;
import java.io.OutputStream;

public class KryoBackedEncoder extends AbstractEncoder implements Encoder {
    private final Output output;

    public KryoBackedEncoder(OutputStream outputStream) {
        output = new Output(outputStream);
    }

    public void writeByte(byte value) {
        output.writeByte(value);
    }

    public void writeBytes(byte[] bytes, int offset, int count) {
        output.writeBytes(bytes, offset, count);
    }

    public void writeLong(long value) {
        output.writeLong(value);
    }

    public void writeInt(int value) throws IOException {
        output.writeInt(value);
    }

    public void writeSizeInt(int value) throws IOException {
        output.writeInt(value, true);
    }

    public void writeBoolean(boolean value) throws IOException {
        output.writeBoolean(value);
    }

    public void writeString(CharSequence value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot encode a null string.");
        }
        output.writeString(value);
    }

    public void writeNullableString(@Nullable CharSequence value) {
        output.writeString(value);
    }

    public void writeBinary(byte[] bytes, int offset, int count) throws IOException {
        output.writeInt(count, true);
        output.writeBytes(bytes, offset, count);
    }

    public void flush() {
        output.flush();
    }
}
