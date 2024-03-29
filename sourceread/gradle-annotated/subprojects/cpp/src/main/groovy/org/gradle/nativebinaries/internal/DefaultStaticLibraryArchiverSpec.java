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

package org.gradle.nativebinaries.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DefaultStaticLibraryArchiverSpec implements StaticLibraryArchiverSpec {

    private final List<File> objectFiles = new ArrayList<File>();
    private final List<String> args = new ArrayList<String>();
    private File outputFile;
    private File tempDir;

    public List<File> getObjectFiles() {
        return objectFiles;
    }

    public void objectFiles(Iterable<File> objectFiles) {
        for (File objectFile : objectFiles) {
            this.objectFiles.add(objectFile);
        }
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public List<String> getArgs() {
        return args;
    }

    public void args(List<String> args) {
        this.args.addAll(args);
    }

    public File getTempDir() {
        return tempDir;
    }

    public void setTempDir(File tempDir) {
        this.tempDir = tempDir;
    }
}
