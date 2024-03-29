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

package org.gradle.test.fixtures.maven

import org.gradle.test.fixtures.file.TestFile
import org.gradle.test.fixtures.server.http.HttpServer

class PomHttpArtifact extends HttpArtifact {
    MavenFileModule backingModule

    PomHttpArtifact(HttpServer httpServer, String path, MavenFileModule backingModule) {
        super(httpServer, path)
        this.backingModule = backingModule
    }

    @Override
    void expectGetMissing() {
        server.expectGetMissing(getPath() - getFile().name + getMissingPomName());
    }

    @Override
    TestFile getSha1File() {
        backingModule.getSha1File(file)
    }

    @Override
    TestFile getMd5File() {
        backingModule.getMd5File(file)
    }

    @Override
    TestFile getFile() {
        return backingModule.pomFile
    }

    private String getMissingPomName() {
        if (backingModule.version.endsWith("-SNAPSHOT")) {
            return "${backingModule.artifactId}-${backingModule.version}.pom"
        } else {
            return getFile().name
        }
    }
}
