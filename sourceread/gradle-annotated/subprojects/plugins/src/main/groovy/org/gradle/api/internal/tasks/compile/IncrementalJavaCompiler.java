/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.internal.tasks.compile;

import org.gradle.api.AntBuilder;
import org.gradle.internal.Factory;
import org.gradle.api.internal.TaskOutputsInternal;
import org.gradle.language.jvm.internal.SimpleStaleClassCleaner;
import org.gradle.language.jvm.internal.StaleClassCleaner;

public class IncrementalJavaCompiler extends IncrementalJavaCompilerSupport<JavaCompileSpec> implements Compiler<JavaCompileSpec> {
    private final Compiler<JavaCompileSpec> compiler;
    private final Factory<AntBuilder> antBuilderFactory;
    private final TaskOutputsInternal taskOutputs;

    public IncrementalJavaCompiler(Compiler<JavaCompileSpec> compiler, Factory<AntBuilder> antBuilderFactory,
                                   TaskOutputsInternal taskOutputs) {
        this.compiler = compiler;
        this.antBuilderFactory = antBuilderFactory;
        this.taskOutputs = taskOutputs;
    }

    @Override
    protected Compiler<JavaCompileSpec> getCompiler() {
        return compiler;
    }

    protected StaleClassCleaner createCleaner(JavaCompileSpec spec) {
        if (spec.getCompileOptions().isUseDepend()) {
            AntDependsStaleClassCleaner cleaner = new AntDependsStaleClassCleaner(antBuilderFactory, spec.getCompileOptions());
            cleaner.setDependencyCacheDir(spec.getDependencyCacheDir());
            return cleaner;
        } else {
            return new SimpleStaleClassCleaner(taskOutputs);
        }
    }
}
