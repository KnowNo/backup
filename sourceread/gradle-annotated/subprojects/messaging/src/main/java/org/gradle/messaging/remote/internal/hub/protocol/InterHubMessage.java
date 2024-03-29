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

package org.gradle.messaging.remote.internal.hub.protocol;

public abstract class InterHubMessage {
    /**
     * Specifies the delivery constraints for this message.
     */
    public enum Delivery {
        /**
         * Message should be delivered to a single handler, and queued until a handler is available.
         */
        SingleHandler,
        /**
         * Message should be delivered to all current handlers. Discarded if no handler is available.
         */
        AllHandlers,
        /**
         * Message should be delivered to all handlers. Most recent stateful message is queued and also delivered to each new handler.
         */
        Stateful
    }

    public abstract Delivery getDelivery();
}
