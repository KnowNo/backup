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

package org.gradle.api.internal.plugins;

import groovy.lang.MissingPropertyException;
import org.gradle.api.internal.BeanDynamicObject;
import org.gradle.api.internal.DynamicObject;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.util.DeprecationLogger;

import java.util.Map;

public class ExtraPropertiesDynamicObjectAdapter extends BeanDynamicObject {

    private final ExtraPropertiesExtension extension;
    private final Object delegate;
    private final DynamicObject dynamicOwner;

    public ExtraPropertiesDynamicObjectAdapter(Object delegate, DynamicObject dynamicOwner, ExtraPropertiesExtension extension) {
        super(extension);
        this.delegate = delegate;
        this.dynamicOwner = dynamicOwner;
        this.extension = extension;
    }

    public boolean hasProperty(String name) {
        return super.hasProperty(name) || extension.has(name);
    }

    public Map<String, ?> getProperties() {
        return extension.getProperties();
    }

    @Override
    public void setProperty(String name, Object value) throws MissingPropertyException {
        if (!dynamicOwner.hasProperty(name)) {
            DeprecationLogger.nagUserAboutDynamicProperty(name, delegate, value);
        }

        super.setProperty(name, value);
    }
}
