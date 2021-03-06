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

package org.github.snt.ui.javafx.lib

import org.github.snt.dao.api.entity.User
import org.github.snt.dao.api.repo.AuthResourceRepo
import org.github.snt.dao.api.repo.UserRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Application state.
 * Useful for storing information actual for user and his actions.
 */
@Component
class ApplicationState {
    var user: User? = null
    var masterKey: ByteArray? = null
    val expandedNotes = HashSet<Long>()

    @Autowired(required = false)
    private lateinit var userRepo: UserRepo

    @Autowired(required = false)
    private lateinit var authResourceRepo: AuthResourceRepo

    fun login(userCode: String, password: String) {
        val currentUser = userRepo.loadByCode(userCode)
        val currentKey = authResourceRepo.checkPassword(currentUser, password)

        // if everything is OK - init state
        this.user = currentUser
        this.masterKey = currentKey

    }

    fun logout() {
        this.user = null
        this.masterKey = null
    }

    fun isLoggedIn(): Boolean {
        return user != null && masterKey != null
    }
}