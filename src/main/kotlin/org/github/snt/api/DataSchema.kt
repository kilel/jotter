/*
 * Copyright 2018 Kislitsyn Ilya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.github.snt.api

/**
 * Data stored in note could be represented in several formats.
 * It could contain simple unstructured data, or some set of fields.
 * All available sets of data formats are collected in this enum.
 */
enum class DataSchema {

    /**
     * Empty note, no data is stored.
     * Mainly used for directories of notes.
     */
    NONE,

    /**
     * Default format: all data is stored in single multiline field.
     */
    DEFAULT,

    /**
     * Password for some account.
     * Contains fields: url, login, password.
     * Password is hidden by default and could be copied without opening.
     */
    PASSWORD
}