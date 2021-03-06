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

package org.github.snt.dao.api.repo

import org.github.snt.dao.api.DaoRepo
import org.github.snt.dao.api.entity.Note
import org.github.snt.dao.api.filter.NoteFilter
import org.github.snt.dao.api.repo.spring.NoteSpringDataRepo

interface NoteRepo : DaoRepo<Note, NoteFilter> {
    override fun getSpringDataRepo(): NoteSpringDataRepo
}