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

package org.gradle.api.internal.artifacts.ivyservice;

import org.gradle.api.artifacts.ResolveException;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.internal.Transformers;
import org.gradle.api.internal.artifacts.ArtifactDependencyResolver;
import org.gradle.api.internal.artifacts.ConfigurationResolver;
import org.gradle.api.internal.artifacts.ResolverResults;
import org.gradle.api.internal.artifacts.configurations.ConfigurationInternal;
import org.gradle.api.internal.artifacts.repositories.ResolutionAwareRepository;
import org.gradle.util.CollectionUtils;

import java.util.List;

public class DefaultConfigurationResolver implements ConfigurationResolver {
    private final ArtifactDependencyResolver resolver;
    private final RepositoryHandler repositories;

    public DefaultConfigurationResolver(ArtifactDependencyResolver resolver, RepositoryHandler repositories) {
        this.resolver = resolver;
        this.repositories = repositories;
    }

    public ResolverResults resolve(ConfigurationInternal configuration) throws ResolveException {
        List<ResolutionAwareRepository> resolutionAwareRepositories = CollectionUtils.collect(repositories, Transformers.cast(ResolutionAwareRepository.class));
        return resolver.resolve(configuration, resolutionAwareRepositories);
    }
}
