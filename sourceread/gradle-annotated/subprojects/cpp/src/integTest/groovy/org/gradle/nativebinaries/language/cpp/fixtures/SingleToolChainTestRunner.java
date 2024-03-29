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

package org.gradle.nativebinaries.language.cpp.fixtures;

import org.gradle.integtests.fixtures.AbstractMultiTestRunner;

import java.util.List;

public class SingleToolChainTestRunner extends AbstractMultiTestRunner {
    private static final String TOOLCHAINS_SYSPROP_NAME = "org.gradle.integtest.cpp.toolChains";

    public SingleToolChainTestRunner(Class<? extends AbstractInstalledToolChainIntegrationSpec> target) {
        super(target);
    }

    @Override
    protected void createExecutions() {
        boolean enableAllToolChains = "all".equals(System.getProperty(TOOLCHAINS_SYSPROP_NAME, "default"));
        List<AvailableToolChains.ToolChainCandidate> toolChains = AvailableToolChains.getToolChains();
        boolean someToolChainAvailable = false;
        for (AvailableToolChains.ToolChainCandidate toolChain : toolChains) {
            boolean enabled = enableAllToolChains || (toolChain.isAvailable() && !someToolChainAvailable);
            someToolChainAvailable |= enabled;
            add(new ToolChainExecution(toolChain, enabled));
        }
    }

    private static class ToolChainExecution extends Execution {
        private final AvailableToolChains.ToolChainCandidate toolChain;
        private final boolean enabled;

        public ToolChainExecution(AvailableToolChains.ToolChainCandidate toolChain, boolean enabled) {
            this.toolChain = toolChain;
            this.enabled = enabled;
        }

        @Override
        protected String getDisplayName() {
            return toolChain.getDisplayName();
        }

        @Override
        protected boolean isTestEnabled(TestDetails testDetails) {
            return enabled;
        }

        @Override
        protected void assertCanExecute() {
            assert toolChain.isAvailable() : String.format("Tool chain %s not available", toolChain.getDisplayName());
        }

        @Override
        protected void before() {
            System.out.println(String.format("Using tool chain %s", toolChain.getDisplayName()));
            AbstractInstalledToolChainIntegrationSpec.setToolChain((AvailableToolChains.InstalledToolChain) toolChain);
        }
    }
}
