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

package org.gradle.api.internal.tasks.testing.junit.result;

import org.gradle.api.internal.tasks.testing.TestDescriptorInternal;
import org.gradle.api.tasks.testing.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Collects the test results into memory and spools the test output to file during execution (to avoid holding it all in memory).
 */
public class TestReportDataCollector implements TestListener, TestOutputListener {

    private final Map<String, TestClassResult> results;
    private final TestOutputStore.Writer outputWriter;

    private final Map<Object, Long> idMappings = new HashMap<Object, Long>();
    private long internalIdCounter = 1;

    public TestReportDataCollector(Map<String, TestClassResult> results, TestOutputStore.Writer outputWriter) {
        this.results = results;
        this.outputWriter = outputWriter;
    }

    public void beforeSuite(TestDescriptor suite) {
    }

    public void afterSuite(TestDescriptor suite, TestResult result) {
    }

    public void beforeTest(TestDescriptor testDescriptor) {
    }

    public void afterTest(TestDescriptor testDescriptor, TestResult result) {
        if (!testDescriptor.isComposite()) {
            String className = testDescriptor.getClassName();
            TestMethodResult methodResult = new TestMethodResult(getInternalId((TestDescriptorInternal) testDescriptor), testDescriptor.getName(), result);
            TestClassResult classResult = results.get(className);
            if (classResult == null) {
                classResult = new TestClassResult(internalIdCounter++, className, result.getStartTime());
                results.put(className, classResult);
            }
            classResult.add(methodResult);
        }
    }

    public void onOutput(TestDescriptor testDescriptor, TestOutputEvent outputEvent) {
        String className = testDescriptor.getClassName();
        if (className == null) {
            //this means that we receive an output before even starting any class (or too late).
            //we don't have a place for such output in any of the reports so skipping.
            return;
        }
        TestDescriptorInternal testDescriptorInternal = (TestDescriptorInternal) testDescriptor;
        TestClassResult classResult = results.get(className);
        if (classResult == null) {
            classResult = new TestClassResult(internalIdCounter++, className, 0);
            results.put(className, classResult);
        }

        String name = testDescriptor.getName();

        // This is a rather weak contract, but given the current inputs is the best we can do
        boolean isClassLevelOutput = name.equals(className);

        if (isClassLevelOutput) {
            outputWriter.onOutput(classResult.getId(), outputEvent);
        } else {
            outputWriter.onOutput(classResult.getId(), getInternalId(testDescriptorInternal), outputEvent);
        }
    }

    private long getInternalId(TestDescriptorInternal testDescriptor) {
        Object id = testDescriptor.getId();
        Long internalId = idMappings.get(id);
        if (internalId == null) {
            internalId = internalIdCounter++;
            idMappings.put(id, internalId);
        }
        return internalId;
    }
}