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
package org.gradle.logging.internal

import org.gradle.internal.TimeProvider
import static org.gradle.logging.StyledTextOutput.Style.*
import org.gradle.api.logging.LogLevel

class LoggingBackedStyledTextOutputTest extends OutputSpecification {
    private final OutputEventListener listener = Mock()
    private final TimeProvider timeProvider = { 1200L } as TimeProvider
    private final LoggingBackedStyledTextOutput output = new LoggingBackedStyledTextOutput(listener, 'category', LogLevel.INFO, timeProvider)

    def forwardsLineOfTextToListener() {
        when:
        output.println('message')

        then:
        1 * listener.onOutput({it.spans[0].text == toNative('message\n')})
        0 * listener._
    }

    def fillsInDetailsOfEvent() {
        when:
        output.text('message').println()

        then:
        1 * listener.onOutput(!null) >> { args ->
            def event = args[0]
            assert event.spans[0].style == Normal
            assert event.category == 'category'
            assert event.logLevel == LogLevel.INFO
            assert event.timestamp == 1200
            assert event.spans[0].text == toNative('message\n')
        }
        0 * listener._
    }

    def buffersTextUntilEndOfLineReached() {
        when:
        output.text('message ').text(toNative('with more\nanother ')).text('line').println()

        then:
        1 * listener.onOutput({it.spans[0].text == toNative('message with more\n')})
        1 * listener.onOutput({it.spans[0].text == toNative('another line\n')})
        0 * listener._
    }
    
    def forwardsEachLineOfTextToListener() {
        when:
        output.text(toNative('message1\nmessage2')).println()

        then:
        1 * listener.onOutput({it.spans[0].text == toNative('message1\n')})
        1 * listener.onOutput({it.spans[0].text == toNative('message2\n')})
        0 * listener._
    }

    def forwardsEmptyLinesToListener() {
        when:
        output.text(toNative('\n\n'))

        then:
        2 * listener.onOutput({it.spans[0].text == toNative('\n')})
        0 * listener._
    }

    def canChangeTheStyle() {
        when:
        output.style(Header)
        output.println('message')

        then:
        1 * listener.onOutput(!null) >> { args ->
            def event = args[0]
            assert event.spans.size() == 1
            assert event.spans[0].style == Header
            assert event.spans[0].text == toNative('message\n')
        }
        0 * listener._
    }
    
    def canChangeTheStyleInsideALine() {
        when:
        output.style(Header)
        output.text('header')
        output.style(Normal)
        output.text('normal')
        output.println()

        then:
        1 * listener.onOutput(!null) >> { args ->
            def event = args[0]
            assert event.spans.size() == 2
            assert event.spans[0].style == Header
            assert event.spans[0].text == 'header'
            assert event.spans[1].style == Normal
            assert event.spans[1].text == toNative('normal\n')
        }
        0 * listener._
    }

    def ignoresEmptySpans() {
        when:
        output.style(Header)
        output.text('')
        output.style(Normal)
        output.style(UserInput)
        output.println()

        then:
        1 * listener.onOutput(!null) >> { args ->
            def event = args[0]
            assert event.spans.size() == 1
            assert event.spans[0].style == UserInput
            assert event.spans[0].text == toNative('\n')
        }
        0 * listener._
    }
}
