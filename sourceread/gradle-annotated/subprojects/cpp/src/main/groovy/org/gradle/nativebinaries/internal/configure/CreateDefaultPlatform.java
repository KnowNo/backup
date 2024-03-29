/*
 * Copyright 2013 the original author or authors.
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

package org.gradle.nativebinaries.internal.configure;

import org.gradle.api.Action;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.nativebinaries.PlatformContainer;
import org.gradle.nativebinaries.internal.DefaultPlatform;

public class CreateDefaultPlatform implements Action<ProjectInternal> {
    public void execute(ProjectInternal project) {
        PlatformContainer platforms = project.getExtensions().getByType(PlatformContainer.class);
        if (platforms.isEmpty()) {
            platforms.add(new DefaultPlatform("current"));
        }
    }
}
