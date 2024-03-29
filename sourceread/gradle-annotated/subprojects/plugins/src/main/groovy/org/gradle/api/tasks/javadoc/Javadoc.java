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

package org.gradle.api.tasks.javadoc;

import groovy.lang.Closure;
import org.gradle.api.GradleException;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.*;
import org.gradle.external.javadoc.MinimalJavadocOptions;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;
import org.gradle.external.javadoc.internal.JavadocExecHandleBuilder;
import org.gradle.process.internal.ExecAction;
import org.gradle.process.internal.ExecActionFactory;
import org.gradle.process.internal.ExecException;
import org.gradle.util.GUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Generates HTML API documentation for Java classes.</p>
 * <p>
 * If you create your own Javadoc tasks remember to specify the 'source' property!
 * Without source the Javadoc task will not create any documentation. Example:
 * <pre autoTested=''>
 * apply plugin: 'java'
 *
 * task myJavadocs(type: Javadoc) {
 *   source = sourceSets.main.allJava
 * }
 * </pre>
 *
 * <p>
 * An example how to create a task that runs a custom doclet implementation:
 * <pre autoTested=''>
 * apply plugin: 'java'
 *
 * configurations {
 *   jaxDoclet
 * }
 *
 * dependencies {
 *   //jaxDoclet "some.interesting:Dependency:1.0"
 * }
 *
 * task generateRestApiDocs(type: Javadoc) {
 *   source = sourceSets.main.allJava
 *   destinationDir = reporting.file("rest-api-docs")
 *   options.docletpath = configurations.jaxDoclet.files.asType(List)
 *   options.doclet = "com.lunatech.doclets.jax.jaxrs.JAXRSDoclet"
 *   options.addStringOption("jaxrscontext", "http://localhost:8080/myapp")
 * }
 * </pre>
 */
public class Javadoc extends SourceTask {
    private JavadocExecHandleBuilder javadocExecHandleBuilder = new JavadocExecHandleBuilder(getServices().get(ExecActionFactory.class));

    private File destinationDir;

    private boolean failOnError = true;

    private String title;

    private String maxMemory;

    private MinimalJavadocOptions options = new StandardJavadocDocletOptions();

    private FileCollection classpath = getProject().files();

    private String executable;

    @TaskAction
    protected void generate() {
        final File destinationDir = getDestinationDir();

        if (options.getDestinationDirectory() == null) {
            options.destinationDirectory(destinationDir);
        }

        options.classpath(new ArrayList<File>(getClasspath().getFiles()));

        if (!GUtil.isTrue(options.getWindowTitle()) && GUtil.isTrue(getTitle())) {
            options.windowTitle(getTitle());
        }
        if (options instanceof StandardJavadocDocletOptions) {
            StandardJavadocDocletOptions docletOptions = (StandardJavadocDocletOptions) options;
            if (!GUtil.isTrue(docletOptions.getDocTitle()) && GUtil.isTrue(getTitle())) {
                docletOptions.setDocTitle(getTitle());
            }
        }

        if (maxMemory != null) {
            final List<String> jFlags = options.getJFlags();
            final Iterator<String> jFlagsIt = jFlags.iterator();
            boolean containsXmx = false;
            while (!containsXmx && jFlagsIt.hasNext()) {
                final String jFlag = jFlagsIt.next();
                if (jFlag.startsWith("-Xmx")) {
                    containsXmx = true;
                }
            }
            if (!containsXmx) {
                options.jFlags("-Xmx" + maxMemory);
            }
        }

        List<String> sourceNames = new ArrayList<String>();
        for (File sourceFile : getSource()) {
            sourceNames.add(sourceFile.getAbsolutePath());
        }
        options.setSourceNames(sourceNames);

        executeExternalJavadoc();
    }

    private void executeExternalJavadoc() {
        javadocExecHandleBuilder.setExecutable(executable);
        javadocExecHandleBuilder.execDirectory(getProject().getRootDir()).options(options).optionsFile(
                getOptionsFile());

        ExecAction execAction = javadocExecHandleBuilder.getExecHandle();
        if (!failOnError) {
            execAction.setIgnoreExitValue(true);
        }

        try {
            execAction.execute();
        } catch (ExecException e) {
            throw new GradleException("Javadoc generation failed.", e);
        }
    }

    void setJavadocExecHandleBuilder(JavadocExecHandleBuilder javadocExecHandleBuilder) {
        if (javadocExecHandleBuilder == null) {
            throw new IllegalArgumentException("javadocExecHandleBuilder == null!");
        }
        this.javadocExecHandleBuilder = javadocExecHandleBuilder;
    }

    /**
     * <p>Returns the directory to generate the documentation into.</p>
     *
     * @return The directory.
     */
    @OutputDirectory
    public File getDestinationDir() {
        return destinationDir;
    }

    /**
     * <p>Sets the directory to generate the documentation into.</p>
     */
    public void setDestinationDir(File destinationDir) {
        this.destinationDir = destinationDir;
    }

    /**
     * Returns the amount of memory allocated to this task.
     */
    public String getMaxMemory() {
        return maxMemory;
    }

    /**
     * Sets the amount of memory allocated to this task.
     *
     * @param maxMemory The amount of memory
     */
    public void setMaxMemory(String maxMemory) {
        this.maxMemory = maxMemory;
    }

    /**
     * <p>Returns the title for the generated documentation.</p>
     *
     * @return The title, possibly null.
     */
    @Input
    @Optional
    public String getTitle() {
        return title;
    }

    /**
     * <p>Sets the title for the generated documentation.</p>
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns whether Javadoc generation is accompanied by verbose output.
     *
     * @see #setVerbose(boolean)
     */
    public boolean isVerbose() {
        return options.isVerbose();
    }

    /**
     * Sets whether Javadoc generation is accompanied by verbose output or not. The verbose output is done via println
     * (by the underlying Ant task). Thus it is not handled by our logging.
     *
     * @param verbose Whether the output should be verbose.
     */
    public void setVerbose(boolean verbose) {
        if (verbose) {
            options.verbose();
        }
    }

    /**
     * Returns the classpath to use to resolve type references in the source code.
     *
     * @return The classpath.
     */
    @InputFiles
    public FileCollection getClasspath() {
        return classpath;
    }

    /**
     * Sets the classpath to use to resolve type references in this source code.
     *
     * @param classpath The classpath. Must not be null.
     */
    public void setClasspath(FileCollection classpath) {
        this.classpath = classpath;
    }

    /**
     * Returns the Javadoc generation options.
     *
     * @return The options. Never returns null.
     */
    @Nested
    public MinimalJavadocOptions getOptions() {
        return options;
    }

    /**
     * Sets the Javadoc generation options.
     *
     * @param options The options. Must not be null.
     */
    public void setOptions(MinimalJavadocOptions options) {
        this.options = options;
    }

    /**
     * Convenience method for configuring Javadoc generation options.
     *
     * @param block The configuration block for Javadoc generation options.
     */
    public void options(Closure block) {
        getProject().configure(getOptions(), block);
    }

    /**
     * Specifies whether this task should fail when errors are encountered during Javadoc generation. When {@code true},
     * this task will fail on Javadoc error. When {@code false}, this task will ignore Javadoc errors.
     */
    @Input
    public boolean isFailOnError() {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public File getOptionsFile() {
        return new File(getTemporaryDir(), "javadoc.options");
    }

    /**
     * Returns the Javadoc executable to use to generate the Javadoc. When {@code null}, the Javadoc executable for
     * the current JVM is used.
     *
     * @return The executable. May be null.
     */
    @Input @Optional
    public String getExecutable() {
        return executable;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }
}
