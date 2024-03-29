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

package org.gradle.api.publish.maven.internal.publication;

import org.gradle.api.Action;
import org.gradle.api.XmlProvider;
import org.gradle.api.internal.UserCodeAction;
import org.gradle.api.publish.maven.internal.dependencies.MavenDependencyInternal;
import org.gradle.api.publish.maven.internal.publisher.MavenProjectIdentity;
import org.gradle.listener.ActionBroadcast;
import org.gradle.util.GUtil;

import java.util.Set;

public class DefaultMavenPom implements MavenPomInternal {

    private final ActionBroadcast<XmlProvider> xmlAction = new ActionBroadcast<XmlProvider>();
    private final MavenPublicationInternal mavenPublication;
    private String packaging;

    public DefaultMavenPom(MavenPublicationInternal mavenPublication) {
        this.mavenPublication = mavenPublication;
    }

    public void withXml(Action<? super XmlProvider> action) {
        xmlAction.add(new UserCodeAction<XmlProvider>("Could not apply withXml() to generated POM", action));
    }

    public Action<XmlProvider> getXmlAction() {
        return xmlAction;
    }

    public String getPackaging() {
        return GUtil.elvis(packaging, mavenPublication.determinePackagingFromArtifacts());
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public MavenProjectIdentity getProjectIdentity() {
        return mavenPublication.getMavenProjectIdentity();
    }

    public Set<MavenDependencyInternal> getRuntimeDependencies() {
        return mavenPublication.getRuntimeDependencies();
    }
}
