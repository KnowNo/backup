/*
 * Copyright 2010 the original author or authors.
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

package org.gradle.api.artifacts;

import org.gradle.api.internal.AbstractMultiCauseException;
import org.gradle.api.internal.Contextual;

/**
 * <p>A <code>ResolveException</code> is thrown when a dependency configuration cannot be resolved for some reason.</p>
 */
@Contextual
public class ResolveException extends AbstractMultiCauseException {
    public ResolveException(Configuration configuration, Throwable cause) {
        super(buildMessage(configuration), cause);
    }

    public ResolveException(Configuration configuration, Iterable<? extends Throwable> causes) {
        super(buildMessage(configuration), causes);
    }

    private static String buildMessage(Configuration configuration) {
        return String.format("Could not resolve all dependencies for %s.", configuration);
    }
}