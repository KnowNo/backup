/*
 * Copyright 2011 the original author or authors.
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

package org.gradle.launcher.daemon.client

import org.gradle.internal.id.IdGenerator
import org.gradle.messaging.remote.internal.Connection
import spock.lang.Specification

public class StopDispatcherTest extends Specification {

    def dispatcher = new StopDispatcher({12} as IdGenerator)
    def connection = Mock(Connection)

    def "ignores failed dispatch and does not receive"() {
        given:
        connection.dispatch(_) >> { throw new RuntimeException("Cannot dispatch") }

        when:
        dispatcher.dispatch(connection)

        then:
        0 * connection.receive()
        noExceptionThrown()
    }

    def "ignores failed receive"() {
        given:
        connection.receive() >> { throw new RuntimeException("Cannot dispatch") }

        when:
        dispatcher.dispatch(connection)

        then:
        1 * connection.dispatch(_)
        noExceptionThrown()
    }
}
