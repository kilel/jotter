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
import org.github.snt.api.AuthResourceType.PASSWORD
import org.github.snt.api.User
import org.github.snt.api.dao.filter.AuthResourceFilter
import org.github.snt.api.dao.impl.AbstractDaoRepo
import org.github.snt.api.dao.repo.AuthResourceRepo
import org.github.snt.api.dao.repo.crud.AuthResourceCrudRepo
import org.github.snt.api.err.OperationResult
import org.github.snt.api.err.ServiceException
import org.github.snt.lib.util.equalsNullableBA
import org.github.snt.lib.util.hex
import org.github.snt.lib.util.sha1
import org.jasypt.encryption.pbe.PBEByteEncryptor
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor
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

    override fun checkPassword(user: User, password: String): ByteArray {
        val userPwdResources = loadList(AuthResourceFilter(user, PASSWORD))

        if (userPwdResources.isEmpty()) {
            throw ServiceException(OperationResult.GENERAL_ERROR, "No password is defined for user, can't authenticate")
        }

        val encryptor = buildEncryptor(password)

        val masterKey = userPwdResources.stream()//
                .map { Pair(it, encryptor.decrypt(it.data)) }//
                .filter { checkMasterKey(it.first, it.second) }//
                .map { it.second }//
                .findAny()

        return masterKey.orElseThrow { throw ServiceException(OperationResult.WRONG_PASSWORD) }
    }

    private fun checkMasterKey(resource: AuthResource, masterKey: ByteArray): Boolean {
        val decryptedCheck = buildEncryptor(masterKey).decrypt(resource.check)
        return SntConfig.AUTH_RES_CHECK.equalsNullableBA(decryptedCheck)
    }

    override fun buildEncryptor(password: String): PBEByteEncryptor {
        return doBuildEncryptor(password.trim().toByteArray().sha1().hex())
    }

    override fun buildEncryptor(masterKey: ByteArray): PBEByteEncryptor {
        return doBuildEncryptor(masterKey.sha1().hex())
    }

    private fun doBuildEncryptor(data: String): PBEByteEncryptor {
        // todo fixme: encryption does not works now with long passwords - need to fix
        val encryptor = StandardPBEByteEncryptor()
        encryptor.setPassword(data.trim().substring(0, 4))
        encryptor.setSaltGenerator(saltGenerator)
        encryptor.setAlgorithm(config.security.encryptionAlgo.trim())
        return encryptor
    }

    override fun getCrudRepo(): AuthResourceCrudRepo {
        return internalCrudRepo
    }

    override fun getEntityName(): String {
        return "authResource"
    }
}