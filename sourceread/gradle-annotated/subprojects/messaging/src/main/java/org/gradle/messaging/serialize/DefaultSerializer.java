/*
 * Copyright 2009 the original author or authors.
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
package org.gradle.messaging.serialize;

import org.gradle.internal.io.ClassLoaderObjectInputStream;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class DefaultSerializer<T> implements Serializer<T> {
    private ClassLoader classLoader;

    public DefaultSerializer() {
        classLoader = getClass().getClassLoader();
    }

    public DefaultSerializer(ClassLoader classLoader) {
        this.classLoader = classLoader != null ? classLoader : getClass().getClassLoader();
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public T read(Decoder decoder) throws Exception {
        try {
            return (T) new ClassLoaderObjectInputStream(decoder.getInputStream(), classLoader).readObject();
        } catch (StreamCorruptedException e) {
            return null;
        }
    }

    public void write(Encoder encoder, T value) throws IOException {
        ObjectOutputStream objectStr = new ObjectOutputStream(encoder.getOutputStream());
        objectStr.writeObject(value);
        objectStr.flush();
    }
}
