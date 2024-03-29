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

package org.gradle.api.internal.notations.parsers;

import org.gradle.api.internal.notations.api.NotationParser;
import org.gradle.util.GUtil;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Flattens or collectionizes input and passes the input notations to the delegates. Returns a set.
 */
public class FlatteningNotationParser<T> implements NotationParser<Set<T>> {

    private final NotationParser<T> delegate;

    public FlatteningNotationParser(NotationParser<T> delegate) {
        assert delegate != null : "delegate cannot be null";
        this.delegate = delegate;
    }

    public void describe(Collection<String> candidateFormats) {
        delegate.describe(candidateFormats);
        candidateFormats.add("Collections or arrays of any other supported format. Nested collections/arrays will be flattened.");
    }

    public Set<T> parseNotation(Object notation) {
        Set<T> out = new LinkedHashSet<T>();
        Collection notations = GUtil.collectionize(notation);
        for (Object n : notations) {
            out.add(delegate.parseNotation(n));
        }
        return out;
    }
}