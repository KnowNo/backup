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

package org.gradle.launcher.daemon.diagnostics;

public class DaemonStartupInfo {

    private String uid;
    private DaemonDiagnostics diagnostics;

    public DaemonStartupInfo(String uid, DaemonDiagnostics diagnostics) {
        this.uid = uid;
        this.diagnostics = diagnostics;
    }

    public String getUid() {
        return uid;
    }

    /**
     * @return the diagnostics. Can be null, this means the existing daemon hasn't yet provided the diagnostics.
     */
    public DaemonDiagnostics getDiagnostics() {
        return diagnostics;
    }

    @Override
    public String toString() {
        return "{"
                + "uid='" + uid + '\''
                + ", diagnostics=" + diagnostics
                + '}';
    }

    public String describe() {
        if (diagnostics == null) {
            return "Daemon uid: " + uid + " without diagnostics.";
        } else {
            return "Daemon uid: " + uid + " with diagnostics:\n"
                    + diagnostics.describe();
        }
    }
}