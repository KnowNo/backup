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
package org.gradle.api.internal.file;

import org.apache.commons.io.IOUtils;
import org.gradle.api.GradleException;
import org.gradle.api.UncheckedIOException;
import org.gradle.api.file.FileTreeElement;
import org.gradle.internal.nativeplatform.filesystem.Chmod;
import org.gradle.internal.nativeplatform.filesystem.FileSystem;
import org.gradle.internal.nativeplatform.filesystem.FileSystems;
import org.gradle.util.GFileUtils;

import java.io.*;

public abstract class AbstractFileTreeElement implements FileTreeElement {
    public abstract String getDisplayName();

    @Override
    public String toString() {
        return getDisplayName();
    }

    public String getName() {
        return getRelativePath().getLastName();
    }

    public String getPath() {
        return getRelativePath().getPathString();
    }

    public void copyTo(OutputStream outstr) {
        try {
            InputStream inputStream = open();
            try {
                IOUtils.copyLarge(inputStream, outstr);
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public boolean copyTo(File target) {
        validateTimeStamps();
        try {
            if (isDirectory()) {
                GFileUtils.mkdirs(target);
            } else {
                GFileUtils.mkdirs(target.getParentFile());
                copyFile(target);
            }
            getChmod().chmod(target, getMode());
            return true;
        } catch (Exception e) {
            throw new GradleException(String.format("Could not copy %s to '%s'.", getDisplayName(), target), e);
        }
    }

    protected Chmod getChmod() {
        return FileSystems.getDefault();
    }

    private void validateTimeStamps() {
        final long lastModified = getLastModified();
        if(lastModified < 0) {
            throw new GradleException(String.format("Invalid Timestamp %s for '%s'.", lastModified, getDisplayName()));
        }
    }

    private void copyFile(File target) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(target);
        try {
            copyTo(outputStream);
        } finally {
            outputStream.close();
        }
    }

    public int getMode() {
        return isDirectory()
            ? FileSystem.DEFAULT_DIR_MODE
            : FileSystem.DEFAULT_FILE_MODE;
    }
}
