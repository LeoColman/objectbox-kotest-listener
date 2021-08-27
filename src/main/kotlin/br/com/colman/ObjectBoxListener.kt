/*
 * Copyright 2020 Leonardo Colman Lopes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either press or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.colman

import io.kotest.core.listeners.Listener
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.objectbox.BoxStore
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * @param createBoxstore Suggestion: MyObjectBox.builder().build()
 */
public class ObjectBoxListener(
    private val deleteOnExit: Boolean,
    private val createBoxstore: () -> BoxStore
) : TestListener {

    public lateinit var boxStore: BoxStore

    override suspend fun beforeTest(testCase: TestCase) {
        boxStore = createBoxstore()
    }

    override suspend fun afterTest(testCase: TestCase, result: TestResult) {
        if(deleteOnExit) {
            boxStore.removeAllObjects()
        }
        boxStore.close()
    }
}
