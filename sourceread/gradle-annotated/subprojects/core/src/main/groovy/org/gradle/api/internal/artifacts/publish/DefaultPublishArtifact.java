/*
 * Copyright 2007 the original author or authors.
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

package org.gradle.api.internal.artifacts.publish;

import org.gradle.api.artifacts.ConfigurablePublishArtifact;

import java.io.File;
import java.util.Date;

public class DefaultPublishArtifact extends AbstractPublishArtifact implements ConfigurablePublishArtifact {
    private String name;
    private String extension;
    private String type;
    private String classifier;
    private Date date;
    private File file;

    public DefaultPublishArtifact(String name, String extension, String type,
                                  String classifier, Date date, File file, Object... tasks) {
        super(tasks);
        this.name = name;
        this.extension = extension;
        this.type = type;
        this.date = date;
        this.classifier = classifier;
        this.file = file;
    }

    @Override
    public DefaultPublishArtifact builtBy(Object... tasks) {
        super.builtBy(tasks);
        return this;
    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }

    public String getType() {
        return type;
    }

    public String getClassifier() {
        return classifier;
    }

    public File getFile() {
        return file;
    }

    public Date getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
