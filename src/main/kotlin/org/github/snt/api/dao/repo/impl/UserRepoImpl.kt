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

import org.github.snt.SntConfig
import org.github.snt.api.AuthResource
import org.github.snt.api.AuthResourceType
import org.github.snt.api.User
import org.github.snt.api.dao.impl.AbstractDaoRepo
import org.github.snt.api.dao.repo.AuthResourceRepo
import org.github.snt.api.dao.repo.UserRepo
import org.github.snt.api.dao.repo.crud.UserCrudRepo
import org.github.snt.api.filter.BaseFilter
import org.jasypt.salt.SaltGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.transaction.Transactional

@Component
class UserRepoImpl : AbstractDaoRepo<User, BaseFilter>(), UserRepo {

    @Autowired
    lateinit var internalCrudRepo: UserCrudRepo

    @Autowired
    lateinit var authResourceRepo: AuthResourceRepo

    @Autowired
    lateinit var saltGenerator: SaltGenerator

    @Autowired
    lateinit var config: SntConfig

    override fun loadList(filter: BaseFilter): List<User> {
        val id = filter.id
        if (filter.id != null) {
            return listOfNotNull(getCrudRepo().findOne(id))
        }

        val code = filter.code
        if (code != null) {
            return listOfNotNull(getCrudRepo().findByCode(code))
        }

        return getCrudRepo().findAll().toList()
    }

    @Transactional
    override fun createNewUser(user: User, password: String) {
        save(user)
        saveNewUserMainPassword(loadByCode(user.code), password)
    }

    fun saveNewUserMainPassword(user: User, password: String) {
        val masterKey = saltGenerator.generateSalt(config.security.masterKeyLength)

        val passwordEncryptor = authResourceRepo.buildEncryptor(password)
        val masterKeyEncryptor = authResourceRepo.buildEncryptor(masterKey)

        val result = AuthResource(user, AuthResourceType.PASSWORD)
        result.code = AuthResourceType.PASSWORD.toString()
        result.description = "Main password for user"
        result.data = passwordEncryptor.encrypt(masterKey)
        result.check = masterKeyEncryptor.encrypt(SntConfig.AUTH_RES_CHECK)

        authResourceRepo.save(result)
    }

    override fun getCrudRepo(): UserCrudRepo {
        return internalCrudRepo
    }

    override fun getEntityName(): String {
        return "user"
    }
}