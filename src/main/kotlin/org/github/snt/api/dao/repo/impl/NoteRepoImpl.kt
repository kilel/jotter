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

package org.github.snt.api.dao.repo.impl

import org.github.snt.api.Note
import org.github.snt.api.dao.impl.AbstractDaoRepo
import org.github.snt.api.dao.repo.NoteRepo
import org.github.snt.api.dao.repo.crud.NoteCrudRepo
import org.github.snt.api.filter.NoteFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class NoteRepoImpl : AbstractDaoRepo<Note, NoteFilter>(), NoteRepo {
    @Autowired
    lateinit var internalCrudRepo: NoteCrudRepo

    override fun loadList(filter: NoteFilter): List<Note> {
        val id = filter.id
        if (filter.id != null) {
            return listOfNotNull(getCrudRepo().findOne(id))
        }

        val parentId = filter.parentId
        if (parentId != null) {
            return getCrudRepo().findByParentId(parentId)
        }

        return getCrudRepo().findAll().toList()
    }

    override fun getCrudRepo(): NoteCrudRepo {
        return internalCrudRepo
    }

    override fun getEntityName(): String {
        return "note"
    }
}