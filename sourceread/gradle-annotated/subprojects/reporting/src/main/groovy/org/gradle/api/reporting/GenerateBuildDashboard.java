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

package org.gradle.api.reporting;

import groovy.lang.Closure;
import org.gradle.api.DefaultTask;
import org.gradle.api.Incubating;
import org.gradle.api.NamedDomainObjectSet;
import org.gradle.api.Transformer;
import org.gradle.api.reporting.internal.BuildDashboardGenerator;
import org.gradle.api.reporting.internal.DefaultBuildDashboardReports;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.util.CollectionUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Generates build dashboard report.
 */
@Incubating
public class GenerateBuildDashboard extends DefaultTask implements Reporting<BuildDashboardReports> {
    private Set<Reporting<? extends ReportContainer<?>>> aggregated = new LinkedHashSet<Reporting<? extends ReportContainer<?>>>();

    @Nested
    private final DefaultBuildDashboardReports reports;

    @Inject
    public GenerateBuildDashboard(Instantiator instantiator) {
        reports = instantiator.newInstance(DefaultBuildDashboardReports.class, this);
        reports.getHtml().setEnabled(true);
    }

    @Input
    public Set<ReportState> getInputReports() {
        Set<ReportState> inputs = new HashSet<ReportState>();
        for (Report report : getEnabledInputReports()) {
            if (getReports().contains(report)) {
                // A report to be generated, ignore
                continue;
            }
            inputs.add(new ReportState(report.getDisplayName(), report.getDestination(), report.getDestination().exists()));
        }
        return inputs;
    }

    private Set<Report> getEnabledInputReports() {
        Set<NamedDomainObjectSet<? extends Report>> enabledReportSets = CollectionUtils.collect(aggregated, new Transformer<NamedDomainObjectSet<? extends Report>, Reporting<? extends ReportContainer<?>>>() {
            public NamedDomainObjectSet<? extends Report> transform(Reporting<? extends ReportContainer<?>> reporting) {
                return reporting.getReports().getEnabled();
            }
        });
        return new LinkedHashSet<Report>(CollectionUtils.flattenToList(Report.class, enabledReportSets));
    }

    /**
     * Configures which reports are to be aggregated in the build dashboard report generated by this task.
     *
     * <pre>
     * buildDashboard {
     *   aggregate codenarcMain, checkstyleMain
     * }
     * </pre>
     *
     * @param reportings an array of {@link Reporting} instances that are to be aggregated
     */
    public void aggregate(Reporting<? extends ReportContainer<?>>... reportings) {
        aggregated.addAll(Arrays.asList(reportings));
    }

    /**
     * The reports to be generated by this task.
     *
     * @return The reports container
     */
    public BuildDashboardReports getReports() {
        return reports;
    }

    /**
     * Configures the reports to be generated by this task.
     *
     * The contained reports can be configured by name and closures.
     *
     * <pre>
     * buildDashboard {
     *   reports {
     *     html {
     *       destination "build/dashboard.html"
     *     }
     *   }
     * }
     * </pre>
     *
     * @param closure The configuration
     * @return The reports container
     */
    public BuildDashboardReports reports(Closure closure) {
        return (BuildDashboardReports) reports.configure(closure);
    }

    @TaskAction
    void run() {
        if (getReports().getHtml().isEnabled()) {
            BuildDashboardGenerator generator = new BuildDashboardGenerator(getEnabledInputReports(), reports.getHtml().getEntryPoint());
            generator.generate();
        } else {
            setDidWork(false);
        }
    }

    private static class ReportState implements Serializable {
        private final String name;
        private final File destination;
        private final boolean available;

        private ReportState(String name, File destination, boolean available) {
            this.name = name;
            this.destination = destination;
            this.available = available;
        }

        @Override
        public boolean equals(Object obj) {
            ReportState other = (ReportState) obj;
            return name.equals(other.name) && destination.equals(other.destination) && available == other.available;
        }

        @Override
        public int hashCode() {
            return name.hashCode() ^ destination.hashCode();
        }
    }
}
