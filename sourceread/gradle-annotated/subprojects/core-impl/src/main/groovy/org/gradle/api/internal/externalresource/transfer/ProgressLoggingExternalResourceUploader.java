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

package org.gradle.api.internal.externalresource.transfer;

import org.gradle.internal.Factory;
import org.gradle.logging.ProgressLoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class ProgressLoggingExternalResourceUploader extends AbstractProgressLoggingHandler implements ExternalResourceUploader {
    private final ExternalResourceUploader delegate;

    public ProgressLoggingExternalResourceUploader(ExternalResourceUploader delegate, ProgressLoggerFactory progressLoggerFactory) {
        super(progressLoggerFactory);
        this.delegate = delegate;
    }
    public void upload(final Factory<InputStream> source, final Long contentLength, String destination) throws IOException {
        final ResourceOperation uploadOperation = createResourceOperation(destination, ResourceOperation.Type.upload, getClass(), contentLength);

        try {
            delegate.upload(new Factory<InputStream>() {
                public InputStream create() {
                    return new ProgressLoggingInputStream(source.create(), uploadOperation);
                }
            }, contentLength, destination);
        } finally {
            uploadOperation.completed();
        }
    }

    private class ProgressLoggingInputStream extends InputStream {
        private InputStream inputStream;
        private final ResourceOperation resourceOperation;

        public ProgressLoggingInputStream(InputStream inputStream, ResourceOperation resourceOperation) {
            this.inputStream = inputStream;
            this.resourceOperation = resourceOperation;
        }

        @Override
        public void close() throws IOException {
            inputStream.close();
        }

        @Override
        public int read() throws IOException {
            int result = inputStream.read();
            if (result >= 0) {
                doLogProgress(1);
            }
            return result;
        }

        public int read(byte[] b, int off, int len) throws IOException {
            int read = inputStream.read(b, off, len);
            if (read > 0) {
                doLogProgress(read);
            }
            return read;
        }

        private void doLogProgress(long numberOfBytes) {
            resourceOperation.logProcessedBytes(numberOfBytes);
        }
    }
}
