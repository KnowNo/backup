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

package org.gradle.api.internal.file;

import org.apache.commons.io.FilenameUtils;
import org.gradle.api.internal.file.archive.compression.Bzip2Archiver;
import org.gradle.api.internal.file.archive.compression.GzipArchiver;
import org.gradle.api.resources.MissingResourceException;
import org.gradle.api.resources.ReadableResource;
import org.gradle.api.tasks.bundling.Compression;

import java.io.InputStream;
import java.net.URI;

public class MaybeCompressedFileResource implements ReadableResource {

    private final ReadableResource resource;

    public MaybeCompressedFileResource(ReadableResource resource) {
        String ext = FilenameUtils.getExtension(resource.getURI().toString());

        if (Compression.BZIP2.getSupportedExtensions().contains(ext)) {
            this.resource = new Bzip2Archiver(resource);
        } else if (Compression.GZIP.getSupportedExtensions().contains(ext)) {
            this.resource = new GzipArchiver(resource);
        } else {
            this.resource = resource;
        }
    }

    public InputStream read() throws MissingResourceException {
        return resource.read();
    }

    public ReadableResource getResource() {
        return resource;
    }

    public String getDisplayName() {
        return resource.getDisplayName();
    }

    public URI getURI() {
        return resource.getURI();
    }

    public String getBaseName() {
        return resource.getBaseName();
    }
}
