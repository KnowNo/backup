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
package org.gradle.plugins.ide.eclipse

import org.gradle.internal.reflect.Instantiator
import org.gradle.plugins.ide.api.XmlFileContentMerger
import org.gradle.plugins.ide.api.XmlGeneratorTask
import org.gradle.plugins.ide.eclipse.model.EclipseWtpFacet
import org.gradle.plugins.ide.eclipse.model.WtpFacet

/**
 * Generates the org.eclipse.wst.common.project.facet.core settings file for Eclipse WTP.
 * If you want to fine tune the eclipse configuration
 * <p>
 * At this moment nearly all configuration is done via {@link EclipseWtpFacet}.
 */
class GenerateEclipseWtpFacet extends XmlGeneratorTask<WtpFacet> {

    EclipseWtpFacet facet

    GenerateEclipseWtpFacet() {
        xmlTransformer.indentation = "\t"
        facet = services.get(Instantiator).newInstance(EclipseWtpFacet, new XmlFileContentMerger(xmlTransformer))
    }

    @Override protected WtpFacet create() {
        new WtpFacet(xmlTransformer)
    }

    @Override protected void configure(WtpFacet xmlFacet) {
        facet.mergeXmlFacet(xmlFacet)
    }

}
