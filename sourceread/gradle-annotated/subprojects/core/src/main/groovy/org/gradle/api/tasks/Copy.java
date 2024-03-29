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

package org.gradle.api.tasks;

import org.gradle.api.InvalidUserDataException;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.file.copy.CopyAction;
import org.gradle.api.internal.file.copy.CopySpecInternal;
import org.gradle.api.internal.file.copy.DestinationRootCopySpec;
import org.gradle.api.internal.file.copy.FileCopyAction;
import org.gradle.internal.reflect.Instantiator;

import java.io.File;

/**
 * Copies files into a destination directory.  This task can also rename and filter files as it copies. The task
 * implements {@link org.gradle.api.file.CopySpec CopySpec} for specifying what to copy.
 *
 * <p> Examples:
 * <pre autoTested=''>
 * task mydoc(type:Copy) {
 * from 'src/main/doc'
 * into 'build/target/doc'
 * }
 *
 * //for ant filter
 * import org.apache.tools.ant.filters.ReplaceTokens
 *
 * task initconfig(type:Copy) {
 * from('src/main/config') {
 * include '**&#47;*.properties'
 * include '**&#47;*.xml'
 * filter(ReplaceTokens, tokens:[version:'2.3.1'])
 * }
 * from('src/main/config') {
 * exclude '**&#47;*.properties', '**&#47;*.xml'
 * }
 * from('src/main/languages') {
 * rename 'EN_US_(.*)', '$1'
 * }
 * into 'build/target/config'
 * exclude '**&#47;*.bak'
 *
 * includeEmptyDirs = false
 * }
 * </pre>
 */
public class Copy extends AbstractCopyTask {

    @Override
    protected CopyAction createCopyAction() {
        File destinationDir = getDestinationDir();
        if (destinationDir == null) {
            throw new InvalidUserDataException("No copy destination directory has been specified, use 'into' to specify a target directory.");
        }
        return new FileCopyAction(getServices().get(FileResolver.class).withBaseDir(destinationDir));
    }

    @Override
    protected CopySpecInternal createRootSpec() {
        Instantiator instantiator = getServices().get(Instantiator.class);
        FileResolver fileResolver = getServices().get(FileResolver.class);

        return instantiator.newInstance(DestinationRootCopySpec.class, fileResolver, super.createRootSpec());
    }

    @Override
    public DestinationRootCopySpec getRootSpec() {
        return (DestinationRootCopySpec) super.getRootSpec();
    }

    /**
     * Returns the directory to copy files into.
     *
     * @return The destination dir.
     */
    @OutputDirectory
    public File getDestinationDir() {
        return getRootSpec().getDestinationDir();
    }

    /**
     * Sets the directory to copy files into. This is the same as calling {@link #into(Object)} on this task.
     *
     * @param destinationDir The destination directory. Must not be null.
     */
    public void setDestinationDir(File destinationDir) {
        into(destinationDir);
    }

}
