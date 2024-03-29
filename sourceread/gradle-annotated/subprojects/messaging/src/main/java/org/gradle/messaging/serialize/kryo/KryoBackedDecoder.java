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

import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import org.gradle.messaging.serialize.AbstractDecoder;
import org.gradle.messaging.serialize.Decoder;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class KryoBackedDecoder extends AbstractDecoder implements Decoder {
    private final Input input;

    /**
     * Note that this decoder uses buffering, so will attempt to read beyond the end of the encoded data. This means you should use this type only when this decoder will be used to decode the entire
     * stream.
     */
    public KryoBackedDecoder(InputStream inputStream) {
        input = new Input(inputStream);
    }

    @Override
    protected int maybeReadBytes(byte[] buffer, int offset, int count) {
        return input.read(buffer, offset, count);
    }

    private RuntimeException maybeEndOfStream(KryoException e) throws EOFException {
        if (e.getMessage().equals("Buffer underflow.")) {
            throw (EOFException) (new EOFException().initCause(e));
        }
        throw e;
    }

    public byte readByte() throws EOFException {
        try {
            return input.readByte();
        } catch (KryoException e) {
            throw maybeEndOfStream(e);
        }
    }

    public void readBytes(byte[] buffer, int offset, int count) throws EOFException {
        try {
            input.readBytes(buffer, offset, count);
        } catch (KryoException e) {
            throw maybeEndOfStream(e);
        }
    }

    public long readLong() throws EOFException {
        try {
            return input.readLong();
        } catch (KryoException e) {
            throw maybeEndOfStream(e);
        }
    }

    public int readInt() throws EOFException {
        try {
            return input.readInt();
        } catch (KryoException e) {
            throw maybeEndOfStream(e);
        }
    }

    public int readSizeInt() throws EOFException {
        try {
            return input.readInt(true);
        } catch (KryoException e) {
            throw maybeEndOfStream(e);
        }
    }

    public boolean readBoolean() throws EOFException {
        try {
            return input.readBoolean();
        } catch (KryoException e) {
            throw maybeEndOfStream(e);
        }
    }

    public String readString() throws EOFException {
        return readNullableString();
    }

    public String readNullableString() throws EOFException {
        try {
            return input.readString();
        } catch (KryoException e) {
            throw maybeEndOfStream(e);
        }
    }

    public byte[] readBinary() throws IOException {
        try {
            int length = input.readInt(true);
            byte[] result = new byte[length];
            input.readBytes(result);
            return result;
        } catch (KryoException e) {
            throw maybeEndOfStream(e);
        }
    }
}
