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
package org.gradle.internal.resource.local;

import org.gradle.internal.hash.HashUtil;
import org.gradle.internal.hash.HashValue;

public abstract class AbstractLocallyAvailableResource implements LocallyAvailableResource {
    // Calculated on demand
    private HashValue sha1;
    private Long contentLength;
    private Long lastModified;

    protected AbstractLocallyAvailableResource() {
    }

    protected AbstractLocallyAvailableResource(HashValue sha1) {
        this.sha1 = sha1;
    }

    public HashValue getSha1() {
        if (sha1 == null) {
            this.sha1 = HashUtil.sha1(getFile());
        }
        return sha1;
    }

    public long getContentLength() {
        if (contentLength == null) {
            contentLength = getFile().length();
        }
        return contentLength;
    }

    public long getLastModified() {
        if (lastModified == null) {
            lastModified = getFile().lastModified();
        }
        return lastModified;
    }

}
