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
package org.gradle.launcher.bootstrap;

import org.gradle.api.internal.ClassPathRegistry;
import org.gradle.api.internal.DefaultClassPathProvider;
import org.gradle.api.internal.DefaultClassPathRegistry;
import org.gradle.api.internal.classpath.DefaultModuleRegistry;
import org.gradle.internal.classpath.ClassPath;
import org.gradle.internal.classloader.ClassLoaderFactory;
import org.gradle.internal.classloader.DefaultClassLoaderFactory;
import org.gradle.internal.classloader.MutableURLClassLoader;

import java.lang.reflect.Method;

public class ProcessBootstrap {
    public void run(String mainClassName, String[] args) {
        try {
            runNoExit(mainClassName, args);
            System.exit(0);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            System.exit(1);
        }
    }

    private void runNoExit(String mainClassName, String[] args) throws Exception {
        ClassPathRegistry classPathRegistry = new DefaultClassPathRegistry(new DefaultClassPathProvider(new DefaultModuleRegistry()));
        ClassLoaderFactory classLoaderFactory = new DefaultClassLoaderFactory();
        ClassPath antClasspath = classPathRegistry.getClassPath("ANT");
        ClassPath runtimeClasspath = classPathRegistry.getClassPath("GRADLE_RUNTIME");
        ClassLoader antClassLoader = classLoaderFactory.createIsolatedClassLoader(antClasspath);
        ClassLoader runtimeClassLoader = new MutableURLClassLoader(antClassLoader, runtimeClasspath);
        Thread.currentThread().setContextClassLoader(runtimeClassLoader);
        Class<?> mainClass = runtimeClassLoader.loadClass(mainClassName);
        Method mainMethod = mainClass.getMethod("main", String[].class);
        mainMethod.invoke(null, new Object[]{args});
    }
}
