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
import org.github.snt.dao.api.entity.NoteSource
import org.github.snt.dao.api.entity.User
import org.github.snt.dao.api.filter.NoteSourceFilter
import org.github.snt.dao.api.repo.NoteRepo
import org.github.snt.dao.api.repo.NoteSourceRepo
import org.github.snt.dao.api.repo.spring.NoteSourceSpringDataRepo
import org.github.snt.dao.impl.AbstractDaoRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.transaction.Transactional

@Component
class NoteSourceRepoImpl : AbstractDaoRepo<NoteSource, NoteSourceFilter>(), NoteSourceRepo {
    @Autowired
    lateinit var internalSpringDataRepo: NoteSourceSpringDataRepo

    @Autowired
    lateinit var noteRepo: NoteRepo

    override fun loadList(filter: NoteSourceFilter): List<NoteSource> {
        val id = filter.id
        if (filter.id != null) {
            return listOfNotNull(getSpringDataRepo().findOne(id))
        }

        val userId = filter.userId
        if (userId != null) {
            return getSpringDataRepo().findByUserId(userId)
        }

        return getSpringDataRepo().findAll().toList()
    }

    @Transactional
    override fun saveUserRoot(user: User) {
        // create new note (root)
        var note = Note("root")
        note.description = "Notes root for user"
        note = noteRepo.save(note)

        // attach to the user and save here
        val noteSource = NoteSource(user, note)
        save(noteSource)
    }

    override fun getSpringDataRepo(): NoteSourceSpringDataRepo {
        return internalSpringDataRepo
    }

    override fun getEntityName(): String {
        return "note source"
    }
}