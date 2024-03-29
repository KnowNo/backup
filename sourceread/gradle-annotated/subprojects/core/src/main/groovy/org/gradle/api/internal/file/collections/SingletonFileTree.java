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
package org.gradle.api.internal.file.collections;

import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.file.RelativePath;
import org.gradle.api.internal.file.DefaultFileTreeElement;

import java.io.File;

/**
 * A file tree with a single file entry.
 */
public class SingletonFileTree implements MinimalFileTree {
    private final File file;

    public SingletonFileTree(File file) {
        this.file = file;
    }

    public String getDisplayName() {
        return String.format("file '%s'", file);
    }

    public void visit(FileVisitor visitor) {
        visitor.visitFile(new FileVisitDetailsImpl());
    }

    private class FileVisitDetailsImpl extends DefaultFileTreeElement implements FileVisitDetails {
        private FileVisitDetailsImpl() {
            super(file, new RelativePath(true, file.getName()));
        }

        public void stopVisiting() {
        }
    }

}
