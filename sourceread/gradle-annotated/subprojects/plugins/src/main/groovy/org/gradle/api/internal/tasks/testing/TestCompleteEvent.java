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

package org.gradle.api.internal.tasks.testing;

import org.gradle.api.tasks.testing.TestResult;

import java.io.Serializable;

public class TestCompleteEvent implements Serializable {
    private final long endTime;
    private final TestResult.ResultType resultType;

    public TestCompleteEvent(long endTime) {
        this(endTime, null);
    }

    public TestCompleteEvent(long endTime, TestResult.ResultType resultType) {
        this.endTime = endTime;
        this.resultType = resultType;
    }

    public long getEndTime() {
        return endTime;
    }

    public TestResult.ResultType getResultType() {
        return resultType;
    }
}
