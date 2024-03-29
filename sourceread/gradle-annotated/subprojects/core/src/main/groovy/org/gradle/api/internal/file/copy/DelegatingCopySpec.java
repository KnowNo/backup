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

package org.gradle.api.internal.file.copy;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.file.*;
import org.gradle.api.specs.Spec;

import java.io.FilterReader;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

abstract public class DelegatingCopySpec implements CopySpecInternal {

    abstract protected CopySpecInternal getDelegateCopySpec();

    public RelativePath getDestPath() {
        return getDelegateCopySpec().getDestPath();
    }

    public FileTree getSource() {
        return getDelegateCopySpec().getSource();
    }

    public boolean hasSource() {
        return getDelegateCopySpec().hasSource();
    }

    public Collection<? extends Action<? super FileCopyDetails>> getAllCopyActions() {
        return getDelegateCopySpec().getAllCopyActions();
    }

    public boolean isCaseSensitive() {
        return getDelegateCopySpec().isCaseSensitive();
    }

    public void setCaseSensitive(boolean caseSensitive) {
        getDelegateCopySpec().setCaseSensitive(caseSensitive);
    }

    public boolean getIncludeEmptyDirs() {
        return getDelegateCopySpec().getIncludeEmptyDirs();
    }

    public void setIncludeEmptyDirs(boolean includeEmptyDirs) {
        getDelegateCopySpec().setIncludeEmptyDirs(includeEmptyDirs);
    }

    public DuplicatesStrategy getDuplicatesStrategy() {
        return getDelegateCopySpec().getDuplicatesStrategy();
    }

    public void setDuplicatesStrategy(DuplicatesStrategy strategy) {
        getDelegateCopySpec().setDuplicatesStrategy(strategy);
    }

    public CopySpec filesMatching(String pattern, Action<? super FileCopyDetails> action) {
        return getDelegateCopySpec().filesMatching(pattern, action);
    }

     public CopySpec filesNotMatching(String pattern, Action<? super FileCopyDetails> action) {
        return getDelegateCopySpec().filesNotMatching(pattern, action);
    }

    public CopySpec with(CopySpec... sourceSpecs) {
        return getDelegateCopySpec().with(sourceSpecs);
    }

    public CopySpec from(Object... sourcePaths) {
        return getDelegateCopySpec().from(sourcePaths);
    }

    public CopySpec from(Object sourcePath, Closure c) {
        return getDelegateCopySpec().from(sourcePath, c);
    }

    public CopySpec setIncludes(Iterable<String> includes) {
        return getDelegateCopySpec().setIncludes(includes);
    }

    public CopySpec setExcludes(Iterable<String> excludes) {
        return getDelegateCopySpec().setExcludes(excludes);
    }

    public CopySpec include(String... includes) {
        return getDelegateCopySpec().include(includes);
    }

    public CopySpec include(Iterable<String> includes) {
        return getDelegateCopySpec().include(includes);
    }

    public CopySpec include(Spec<FileTreeElement> includeSpec) {
        return getDelegateCopySpec().include(includeSpec);
    }

    public CopySpec include(Closure includeSpec) {
        return getDelegateCopySpec().include(includeSpec);
    }

    public CopySpec exclude(String... excludes) {
        return getDelegateCopySpec().exclude(excludes);
    }

    public CopySpec exclude(Iterable<String> excludes) {
        return getDelegateCopySpec().exclude(excludes);
    }

    public CopySpec exclude(Spec<FileTreeElement> excludeSpec) {
        return getDelegateCopySpec().exclude(excludeSpec);
    }

    public CopySpec exclude(Closure excludeSpec) {
        return getDelegateCopySpec().exclude(excludeSpec);
    }

    public CopySpec into(Object destPath) {
        return getDelegateCopySpec().into(destPath);
    }

    public CopySpec into(Object destPath, Closure configureClosure) {
        return getDelegateCopySpec().into(destPath, configureClosure);
    }

    public CopySpec rename(Closure closure) {
        return getDelegateCopySpec().rename(closure);
    }

    public CopySpec rename(String sourceRegEx, String replaceWith) {
        return getDelegateCopySpec().rename(sourceRegEx, replaceWith);
    }

    public CopyProcessingSpec rename(Pattern sourceRegEx, String replaceWith) {
        return getDelegateCopySpec().rename(sourceRegEx, replaceWith);
    }

    public CopySpec filter(Map<String, ?> properties, Class<? extends FilterReader> filterType) {
        return getDelegateCopySpec().filter(properties, filterType);
    }

    public CopySpec filter(Class<? extends FilterReader> filterType) {
        return getDelegateCopySpec().filter(filterType);
    }

    public CopySpec filter(Closure closure) {
        return getDelegateCopySpec().filter(closure);
    }

    public CopySpec expand(Map<String, ?> properties) {
        return getDelegateCopySpec().expand(properties);
    }

    public CopySpec eachFile(Action<? super FileCopyDetails> action) {
        return getDelegateCopySpec().eachFile(action);
    }

    public CopySpec eachFile(Closure closure) {
        return getDelegateCopySpec().eachFile(closure);
    }

    public Integer getFileMode() {
        return getDelegateCopySpec().getFileMode();
    }

    public CopyProcessingSpec setFileMode(Integer mode) {
        return getDelegateCopySpec().setFileMode(mode);
    }

    public Integer getDirMode() {
        return getDelegateCopySpec().getDirMode();
    }

    public CopyProcessingSpec setDirMode(Integer mode) {
        return getDelegateCopySpec().setDirMode(mode);
    }

    public Set<String> getIncludes() {
        return getDelegateCopySpec().getIncludes();
    }

    public Set<String> getExcludes() {
        return getDelegateCopySpec().getExcludes();
    }

    public Iterable<CopySpecInternal> getChildren() {
        return getDelegateCopySpec().getChildren();
    }

    public FileTree getAllSource() {
        return getDelegateCopySpec().getAllSource();
    }

    public DefaultCopySpec addChild() {
        return getDelegateCopySpec().addChild();
    }

    public DefaultCopySpec addFirst() {
        return getDelegateCopySpec().addFirst();
    }

    public void walk(Action<? super CopySpecInternal> action) {
        action.execute(this);
        for (CopySpecInternal child : getChildren()) {
            child.walk(action);
        }
    }
}
