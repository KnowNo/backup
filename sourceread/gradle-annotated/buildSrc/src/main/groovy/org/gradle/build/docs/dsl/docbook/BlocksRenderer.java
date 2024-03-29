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

package org.gradle.build.docs.dsl.docbook;

import org.gradle.build.docs.dsl.docbook.model.BlockDoc;
import org.gradle.build.docs.dsl.docbook.model.ClassDoc;
import org.gradle.build.docs.dsl.docbook.model.ClassExtensionDoc;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Collection;

public class BlocksRenderer {
    private final BlockTableRenderer blockTableRenderer = new BlockTableRenderer();
    private final ExtensionBlocksSummaryRenderer extensionBlocksSummaryRenderer;
    private final BlockDetailRenderer blockDetailRenderer;

    public BlocksRenderer(LinkRenderer linkRenderer, GenerationListener listener) {
        blockDetailRenderer = new BlockDetailRenderer(linkRenderer, listener);
        extensionBlocksSummaryRenderer = new ExtensionBlocksSummaryRenderer(blockTableRenderer);
    }

    public void renderTo(ClassDoc classDoc, Element parent) {
        Document document = parent.getOwnerDocument();

        Element summarySection = document.createElement("section");
        parent.appendChild(summarySection);

        Element title = document.createElement("title");
        summarySection.appendChild(title);
        title.appendChild(document.createTextNode("Script blocks"));

        boolean hasBlocks = false;
        Collection<BlockDoc> classBlocks = classDoc.getClassBlocks();
        if (!classBlocks.isEmpty()) {
            hasBlocks = true;

            Element table = document.createElement("table");
            summarySection.appendChild(table);

            title = document.createElement("title");
            table.appendChild(title);
            title.appendChild(document.createTextNode("Script blocks - " + classDoc.getSimpleName()));

            blockTableRenderer.renderTo(classBlocks, table);
        }

        for (ClassExtensionDoc extensionDoc : classDoc.getClassExtensions()) {
            hasBlocks |= !extensionDoc.getExtensionBlocks().isEmpty();
            extensionBlocksSummaryRenderer.renderTo(extensionDoc, summarySection);
        }

        if (!hasBlocks) {
            Element para = document.createElement("para");
            summarySection.appendChild(para);
            para.appendChild(document.createTextNode("No script blocks"));
            return;
        }


        Element detailsSection = document.createElement("section");
        parent.appendChild(detailsSection);

        title = document.createElement("title");
        detailsSection.appendChild(title);
        title.appendChild(document.createTextNode("Script block details"));

        for (BlockDoc blockDoc : classBlocks) {
            blockDetailRenderer.renderTo(blockDoc, detailsSection);
        }
        for (ClassExtensionDoc extensionDoc : classDoc.getClassExtensions()) {
            for (BlockDoc blockDoc : extensionDoc.getExtensionBlocks()) {
                blockDetailRenderer.renderTo(blockDoc, detailsSection);
            }
        }
    }
}
