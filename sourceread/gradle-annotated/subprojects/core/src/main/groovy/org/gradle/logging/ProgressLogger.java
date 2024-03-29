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

package org.gradle.logging;

/**
 * Used to log the progress of a potentially long running operation.
 *
 * <p>When running in the command-line UI, the properties of an operation are treated as follows:
 *
 * <ul>
 *
 * <li>When an operation starts, and the operation has a logging header defined, a LIFECYCLE log message is generated containing the logging header.
 * If running under a terminal, and the logging header == the short description or the status, this log message is deferred until either some other log
 * message is generated or the operation completes.</li>
 *
 * <li>If running under a terminal, and the operation has a status defined, that status is shown in the 'status bar' at the bottom of the screen.</li>
 *
 * <li>If running under a terminal, and the operation has a short description and no status defined, the short description is shown in the 'status bar' at the bottom of the screen.</li>
 *
 * </ul>
 *
 * </p>
 */
public interface ProgressLogger {
    /**
     * Returns the description of the operation.
     *
     * @return the description, must not be empty.
     */
    String getDescription();

    /**
     * <p>Sets the description of the operation. This should be a full, stand-alone description of the operation.
     *
     * <p>This must be called before {@link #started()}.
     *
     * @param description The description.
     */
    void setDescription(String description);

    /**
     * Returns the short description of the operation. This is used in place of the full description when display space is limited.
     *
     * @return The short description, must not be empty.
     */
    String getShortDescription();

    /**
     * <p>Sets the short description of the operation. This is used in place of the full description when display space is limited.
     *
     * <p>This must be called before {@link #started()}
     *
     * @param description The short description.
     */
    void setShortDescription(String description);

    /**
     * <p>Returns the logging header for the operation. This is logged before any other log messages for this operation are logged. It is usually
     * also logged at the end of the operation, along with the final status message. Defaults to null.
     *
     * <p>If not specified, no logging header is logged.
     *
     * @return The logging header, possibly empty.
     */
    String getLoggingHeader();

    /**
     * <p>Sets the logging header for the operation. This is logged before any other log messages for this operation are logged. It is usually
     * also logged at the end of the operation, along with the final status message. Defaults to null.
     *
     * @param header The header. May be empty or null.
     */
    void setLoggingHeader(String header);

    /**
     * Logs the start of the operation, with no initial status.
     */
    void started();

    /**
     * Logs the start of the operation, with the given status.
     *
     * @param status The initial status message. Can be null or empty.
     */
    void started(String status);

    /**
     * Logs some progress, indicated by a new status.
     *
     * @param status The new status message. Can be null or empty.
     */
    void progress(String status);

    /**
     * Logs the completion of the operation, with no final status
     */
    void completed();

    /**
     * Logs the completion of the operation, with a final status. This is generally logged along with the description.
     *
     * @param status The final status message. Can be null or empty.
     */
    void completed(String status);
}
