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

package org.github.snt.dao.impl

import org.github.snt.dao.api.DaoRepoStore
import org.github.snt.dao.api.repo.AuthResourceRepo
import org.github.snt.dao.api.repo.NoteRepo
import org.github.snt.dao.api.repo.NoteSourceRepo
import org.github.snt.dao.api.repo.UserRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DaoRepoStoreImpl : DaoRepoStore {

    @Autowired
    override lateinit var userRepo: UserRepo

    @Autowired
    override lateinit var noteRepo: NoteRepo

    @Autowired
    override lateinit var authResourceRepo: AuthResourceRepo

    @Autowired
    override lateinit var noteSourceRepo: NoteSourceRepo
}