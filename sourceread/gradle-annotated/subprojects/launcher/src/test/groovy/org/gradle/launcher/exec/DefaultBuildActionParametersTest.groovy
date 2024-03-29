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

package org.gradle.launcher.exec

import org.gradle.api.logging.LogLevel
import org.gradle.configuration.GradleLauncherMetaData
import spock.lang.Specification

public class DefaultBuildActionParametersTest extends Specification {

    def "is serializable"() {
        given:
        def params = new DefaultBuildActionParameters(new GradleLauncherMetaData(), System.currentTimeMillis(), System.properties, System.getenv(), new File("."), LogLevel.ERROR)
        ObjectOutputStream out = new ObjectOutputStream(new ByteArrayOutputStream());

        when:
        out.writeObject(params);

        then:
        noExceptionThrown()
    }
}
