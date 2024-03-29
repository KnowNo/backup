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

package org.gradle.nativebinaries.tasks
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Incubating
import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.FileOperations
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.nativeplatform.filesystem.FileSystem
import org.gradle.internal.os.OperatingSystem
import org.gradle.internal.reflect.Instantiator
import org.gradle.nativebinaries.ToolChain
import org.gradle.nativebinaries.toolchain.Gcc

import javax.inject.Inject
/**
 * Installs an executable with it's dependent libraries so it can be easily executed.
 */
@Incubating
public class InstallExecutable extends DefaultTask {

    private final Instantiator instantiator
    private final FileOperations fileOperations

    @Inject
    InstallExecutable(Instantiator instantiator, FileOperations fileOperations) {
        this.instantiator = instantiator
        this.fileOperations = fileOperations
        this.libs = project.files()
    }

    /**
     * The tool chain used for linking.
     */
    ToolChain toolChain

    /**
     * The directory to install files into.
     */
    @OutputDirectory
    File destinationDir

    /**
     * The executable file to install.
     */
    @InputFile
    File executable

    /**
     * The library files that should be installed.
     */
    @InputFiles
    FileCollection libs

    /**
     * Adds a set of library files to be installed.
     * The provided libs object is evaluated as per {@link org.gradle.api.Project#files(Object...)}.
     */
    void lib(Object libs) {
        this.libs.from libs
    }

    // TODO:DAZ Once we introduce a public type for OperatingSystem, make it configurable here
    private OperatingSystem os = OperatingSystem.current()

    @TaskAction
    void install() {
        if (os.windows) {
            installWindows()
        } else {
            installUnix()
        }
    }

    private void installWindows() {
        final destination = getDestinationDir()
        final File executable = getExecutable()

        installToDir(new File(destination, "lib"))

        StringBuilder toolChainPath = new StringBuilder()
        if (toolChain in Gcc) {
            // Gcc on windows requires the path to be set
            toolChainPath.append("SET PATH=")
            for (File pathEntry : ((Gcc) toolChain).path) {
                toolChainPath.append(pathEntry.absolutePath).append(";")
            }
            toolChainPath.append("%PATH%")
        }

        File script = new File(destination, os.getScriptName(executable.name));
        script.text = """
@echo off
SETLOCAL
$toolChainPath
CALL "%~dp0lib\\${executable.name}"

ENDLOCAL
"""
    }

    private void installUnix() {
        final destination = getDestinationDir()
        final executable = getExecutable()

        installToDir(new File(destination, "lib"))

        File script = new File(destination, executable.name);
        script.text = """
#/bin/sh
APP_BASE_NAME=`dirname "\$0"`
export DYLD_LIBRARY_PATH="\$APP_BASE_NAME/lib"
export LD_LIBRARY_PATH="\$APP_BASE_NAME/lib"
exec "\$APP_BASE_NAME/lib/${executable.name}" \"\$@\"
"""

        FileSystem fileSystem = getServices().get(FileSystem.class);
        fileSystem.chmod(script, 0755)
    }

    private void installToDir(File binaryDir) {
        fileOperations.sync(new Action<CopySpec>() {
            void execute(CopySpec copySpec) {
                copySpec.into(binaryDir)
                copySpec.from(getExecutable())
                copySpec.from(getLibs())
            }
        })
    }

}
