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

package org.github.snt.dao.impl.repo

import org.github.snt.dao.api.entity.Note
import org.github.snt.dao.api.filter.NoteFilter
import org.github.snt.dao.api.repo.NoteRepo
import org.github.snt.dao.api.repo.spring.NoteSpringDataRepo
import org.github.snt.dao.impl.AbstractDaoRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class NoteRepoImpl : AbstractDaoRepo<Note, NoteFilter>(), NoteRepo {
    @Autowired
    lateinit var internalSpringDataRepo: NoteSpringDataRepo

    override fun loadList(filter: NoteFilter): List<Note> {
        val id = filter.id
        if (id != null) {
            return listOfNotNull(getSpringDataRepo().findById(id).get())
        }

        val parentId = filter.parentId
        if (parentId != null) {
            return getSpringDataRepo().findByParentId(parentId)
        }

        return getSpringDataRepo().findAll().toList()
    }

    override fun getSpringDataRepo(): NoteSpringDataRepo {
        return internalSpringDataRepo
    }

    override fun getEntityName(): String {
        return "note"
    }
}