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

package org.gradle.api.internal.cache;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface BinaryStore {
    void write(WriteAction write);
    //done writing data, release any resources
    BinaryData done();

    public static interface WriteAction {
        void write(DataOutputStream output) throws IOException;
    }

    public static interface ReadAction<T> {
        T read(DataInputStream input) throws IOException;
    }

    public static interface BinaryData {
        <T> T read(ReadAction<T> readAction);
        //done reading data, release any resources
        void done();
    }
}
