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

package org.gradle.logging.internal;

import org.gradle.api.Action;
import org.gradle.api.logging.StandardOutputListener;
import org.gradle.logging.StandardOutputCapture;
import org.gradle.logging.StandardOutputRedirector;
import org.gradle.util.LinePerThreadBufferingOutputStream;

import java.io.PrintStream;

public class DefaultStandardOutputRedirector implements StandardOutputRedirector {
    private PrintStream originalStdOut;
    private PrintStream originalStdErr;
    private final WriteAction stdOut = new WriteAction();
    private final WriteAction stdErr = new WriteAction();
    private final PrintStream redirectedStdOut = new LinePerThreadBufferingOutputStream(stdOut);
    private final PrintStream redirectedStdErr = new LinePerThreadBufferingOutputStream(stdErr);

    public void redirectStandardOutputTo(StandardOutputListener stdOutDestination) {
        stdOut.setDestination(stdOutDestination);
    }

    public void redirectStandardErrorTo(StandardOutputListener stdErrDestination) {
        stdErr.setDestination(stdErrDestination);
    }

    public StandardOutputCapture start() {
        if (stdOut.destination != null) {
            originalStdOut = System.out;
            System.setOut(redirectedStdOut);
        }
        if (stdErr.destination != null) {
            originalStdErr = System.err;
            System.setErr(redirectedStdErr);
        }
        return this;
    }

    public StandardOutputCapture stop() {
        try {
            if (originalStdOut != null) {
                System.setOut(originalStdOut);
            }
            if (originalStdErr != null) {
                System.setErr(originalStdErr);
            }
            redirectedStdOut.flush();
            redirectedStdErr.flush();
        } finally {
            originalStdOut = null;
            originalStdErr = null;
            stdOut.setDestination(new DiscardAction());
            stdErr.setDestination(new DiscardAction());
        }
        return this;
    }

    private static class DiscardAction implements StandardOutputListener {
        public void onOutput(CharSequence output) {
        }
    }

    private static class WriteAction implements Action<String> {
        private StandardOutputListener destination;

        public void execute(String message) {
            destination.onOutput(message);
        }

        public void setDestination(StandardOutputListener destination) {
            this.destination = destination;
        }
    }
}
