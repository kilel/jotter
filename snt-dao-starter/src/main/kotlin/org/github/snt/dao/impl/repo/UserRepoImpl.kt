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

import org.github.snt.dao.api.entity.User
import org.github.snt.dao.api.filter.BaseFilter
import org.github.snt.dao.api.repo.AuthResourceRepo
import org.github.snt.dao.api.repo.NoteSourceRepo
import org.github.snt.dao.api.repo.UserRepo
import org.github.snt.dao.api.repo.spring.UserSpringDataRepo
import org.github.snt.dao.impl.AbstractDaoRepo
import org.github.snt.lib.config.SntCoreConfig
import org.jasypt.salt.SaltGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.transaction.Transactional

@Component
class UserRepoImpl : AbstractDaoRepo<User, BaseFilter>(), UserRepo {

    @Autowired
    lateinit var internalSpringDataRepo: UserSpringDataRepo

    @Autowired
    lateinit var authResourceRepo: AuthResourceRepo

    @Autowired
    lateinit var noteSourceRepo: NoteSourceRepo

    @Autowired
    lateinit var saltGenerator: SaltGenerator

    @Autowired
    lateinit var config: SntCoreConfig

    override fun loadList(filter: BaseFilter): List<User> {
        val id = filter.id
        if (filter.id != null) {
            return listOfNotNull(getSpringDataRepo().findOne(id))
        }

        val code = filter.code
        if (code != null) {
            return listOfNotNull(getSpringDataRepo().findByCode(code))
        }

        return getSpringDataRepo().findAll().toList()
    }

    @Transactional
    override fun createNewUser(user: User, password: String) {
        val savedUser = save(user)
        authResourceRepo.saveUserPassword(savedUser, password)
        noteSourceRepo.saveUserRoot(savedUser)
    }

    override fun getSpringDataRepo(): UserSpringDataRepo {
        return internalSpringDataRepo
    }

    override fun getEntityName(): String {
        return "user"
    }
}