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

package org.gradle.api.internal.tasks.scala;

import com.google.common.collect.Lists;
import org.gradle.api.tasks.scala.ScalaCompileOptions;

import java.util.List;

public class ScalaCompilerArgumentsGenerator {
    public List<String> generate(ScalaCompileSpec spec) {
        List<String> result = Lists.newArrayList();

        ScalaCompileOptions options = spec.getScalaCompileOptions();
        addFlag("-deprecation", options.isDeprecation(), result);
        addFlag("-unchecked", options.isUnchecked(), result);
        addConcatenatedOption("-g:", options.getDebugLevel(), result);
        addFlag("-optimise", options.isOptimize(), result);
        addOption("-encoding", options.getEncoding(), result);
        addFlag("-verbose", "verbose".equals(options.getDebugLevel()), result);
        addFlag("-Ydebug", "debug".equals(options.getDebugLevel()), result);
        if (options.getLoggingPhases() != null) {
            for (String phase : options.getLoggingPhases()) {
                addConcatenatedOption("-Ylog:", phase, result);
            }
        }
        if (options.getAdditionalParameters() != null) {
            result.addAll(options.getAdditionalParameters());
        }

        return result;
    }

    private void addFlag(String name, boolean value, List<String> result) {
        if (value) {
            result.add(name);
        }
    }

    private void addOption(String name, Object value, List<String> result) {
        if (value != null) {
            result.add(name);
            result.add(value.toString());
        }
    }

    private void addConcatenatedOption(String name, Object value, List<String> result) {
        if (value != null) {
            result.add(name + value.toString());
        }
    }
}
