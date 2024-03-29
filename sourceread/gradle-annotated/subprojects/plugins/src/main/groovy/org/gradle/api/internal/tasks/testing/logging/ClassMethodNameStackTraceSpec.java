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

package org.gradle.api.internal.tasks.testing.logging;

import org.gradle.api.Nullable;
import org.gradle.api.specs.Spec;

public class ClassMethodNameStackTraceSpec implements Spec<StackTraceElement> {
    private final String className;
    private final String methodName;

    ClassMethodNameStackTraceSpec(@Nullable String className, @Nullable String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    public boolean isSatisfiedBy(StackTraceElement element) {
        return (className == null || className.equals(element.getClassName()))
                && (methodName == null || methodName.equals(element.getMethodName()));
    }
}
