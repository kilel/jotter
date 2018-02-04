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
import org.github.snt.api.AuthResourceType.PASSWORD
import org.github.snt.api.User
import org.github.snt.api.dao.filter.AuthResourceFilter
import org.github.snt.api.dao.impl.AbstractDaoRepo
import org.github.snt.api.dao.repo.AuthResourceRepo
import org.github.snt.api.dao.repo.crud.AuthResourceCrudRepo
import org.github.snt.api.err.OperationResult
import org.github.snt.api.err.ServiceException
import org.github.snt.lib.util.*
import org.jasypt.digest.StandardByteDigester
import org.jasypt.digest.StandardStringDigester
import org.jasypt.salt.SaltGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AuthResourceRepoImpl : AbstractDaoRepo<AuthResource, AuthResourceFilter>(), AuthResourceRepo {

    @Autowired
    lateinit var internalCrudRepo: AuthResourceCrudRepo

    @Autowired
    lateinit var saltGenerator: SaltGenerator

    @Autowired
    lateinit var config: SntConfig

    override fun loadList(filter: AuthResourceFilter): List<AuthResource> {
        val id = filter.id
        if (filter.id != null) {
            return listOfNotNull(getCrudRepo().findOne(id))
        }

        val user = filter.user
        val type = filter.type
        if (user != null && type != null) {
            return listOfNotNull(getCrudRepo().findByUserIdAndTypeId(user.id, type.id))
        } else if (user != null) {
            return listOfNotNull(getCrudRepo().findByUserId(user.id))
        }

        return getCrudRepo().findAll().toList()
    }

    override fun saveUserPassword(user: User, password: String) {
        val resCheckData = password.buildPasswordBytes()
        val masterKey = saltGenerator.generateSalt(config.security.masterKeySize)

        val encryptor = buildEncryptor(password)
        val digister = buildBinDigister()

        val result = AuthResource(user, AuthResourceType.PASSWORD)
        result.code = AuthResourceType.PASSWORD.toString()
        result.description = "Main password for user"
        result.data = encryptor.encrypt(masterKey)
        result.check = digister.digest(resCheckData)

        save(result)
    }

    private fun String.buildPasswordBytes(): ByteArray = this.trim().getBytes()

    override fun checkPassword(user: User, password: String): ByteArray {
        val userPwdResources = loadList(AuthResourceFilter(user, PASSWORD))

        if (userPwdResources.isEmpty()) {
            throw ServiceException(OperationResult.GENERAL_ERROR, "No password is defined for user, can't authenticate")
        }

        val resCheckData = password.buildPasswordBytes()
        val encryptor = buildEncryptor(password)
        val digester = buildBinDigister()

        val masterKey = userPwdResources.stream()//
                .filter { digester.matches(resCheckData, it.check) }
                .findAny() //
                .map { encryptor.decrypt(it.data) }

        return masterKey.orElseThrow { throw ServiceException(OperationResult.WRONG_PASSWORD) }
    }

    override fun buildEncryptor(password: String): AesEncryptor {
        return doBuildEncryptor(password.trim().toByteArray().sha512().hex())
    }

    override fun buildEncryptor(masterKey: ByteArray): AesEncryptor {
        return doBuildEncryptor(masterKey.sha512().hex())
    }

    private fun doBuildEncryptor(data: String): AesEncryptor {
        return buildAesEncryptor(data, config, saltGenerator)
    }

    private fun buildPwdDigister(): StandardStringDigester {
        return buildJasyptPwdDigister(config, saltGenerator)
    }

    private fun buildBinDigister(): StandardByteDigester {
        return buildJasyptBinDigister(config, saltGenerator)
    }

    override fun getCrudRepo(): AuthResourceCrudRepo {
        return internalCrudRepo
    }

    override fun getEntityName(): String {
        return "authResource"
    }
}