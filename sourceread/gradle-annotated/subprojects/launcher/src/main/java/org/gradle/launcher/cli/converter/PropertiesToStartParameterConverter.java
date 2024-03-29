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

package org.gradle.launcher.cli.converter;

import org.gradle.StartParameter;
import org.gradle.launcher.daemon.configuration.GradleProperties;

import java.util.Map;

import static org.gradle.launcher.daemon.configuration.GradleProperties.isTrue;

public class PropertiesToStartParameterConverter {
    public StartParameter convert(Map<String, String> properties, StartParameter startParameter) {
        startParameter.setConfigureOnDemand(isTrue(properties.get(GradleProperties.CONFIGURE_ON_DEMAND_PROPERTY)));

        String parallel = properties.get(GradleProperties.PARALLEL_PROPERTY);
        if (isTrue(parallel)) {
            startParameter.setParallelThreadCount(-1);
        }
        return startParameter;
    }
}
