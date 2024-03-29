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

package org.gradle.test.fixtures.archive

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import org.hamcrest.Matcher

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasItem
import static org.junit.Assert.assertEquals

class ArchiveTestFixture {
    private final ListMultimap<String, String> filesByRelativePath = ArrayListMultimap.create()

    protected void add(String relativePath, String content) {
        filesByRelativePath.put(relativePath, content)
    }

    def assertContainsFile(String relativePath, int occurrences = 1) {
        assertEquals(occurrences, filesByRelativePath.get(relativePath).size())
        this
    }

    String content(String relativePath) {
        List<String> files = filesByRelativePath.get(relativePath)
        assertEquals(1, files.size())
        files.get(0)
    }

    Integer countFiles(String relativePath) {
        filesByRelativePath.get(relativePath).size()
    }

    def hasDescendants(String... relativePaths) {
        assertThat(relativePaths as Set, equalTo(filesByRelativePath.keySet()))
        def expectedCounts = ArrayListMultimap.create()
        for (String fileName : relativePaths) {
            expectedCounts.put(fileName, fileName)
        }
        for (String fileName : relativePaths) {
            assertEquals(expectedCounts.get(fileName).size(), filesByRelativePath.get(fileName).size())
        }
        this
    }

    /**
     * Asserts that there is exactly one file present with the given path, and that this file has the given content.
     */
    def assertFileContent(String relativePath, String fileContent) {
        assertFileContent(relativePath, equalTo(fileContent))
    }

    def assertFileContent(String relativePath, Matcher contentMatcher) {
        assertThat(content(relativePath), contentMatcher)
        this
    }

    /**
     * Asserts that there is a file present with the given path and content.
     */
    def assertFilePresent(String relativePath, String fileContent) {
        assertThat(filesByRelativePath.get(relativePath), hasItem(fileContent))
        this
    }
}
