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

package org.gradle.api.internal.artifacts.ivyservice.resolveengine.result;

import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.result.ModuleVersionSelectionReason;
import org.gradle.api.internal.artifacts.ModuleVersionIdentifierSerializer;
import org.gradle.messaging.serialize.Decoder;
import org.gradle.messaging.serialize.Encoder;
import org.gradle.messaging.serialize.Serializer;

import java.io.IOException;

public class ModuleVersionSelectionSerializer implements Serializer<ModuleVersionSelection> {

    private final ModuleVersionIdentifierSerializer idSerializer = new ModuleVersionIdentifierSerializer();
    private final ModuleVersionSelectionReasonSerializer reasonSerializer = new ModuleVersionSelectionReasonSerializer();

    public ModuleVersionSelection read(Decoder decoder) throws IOException {
        ModuleVersionIdentifier id = idSerializer.read(decoder);
        ModuleVersionSelectionReason reason = reasonSerializer.read(decoder);
        return new DefaultModuleVersionSelection(id, reason);
    }

    public void write(Encoder encoder, ModuleVersionSelection value) throws IOException {
        idSerializer.write(encoder, value.getSelectedId());
        reasonSerializer.write(encoder, value.getSelectionReason());
    }
}
