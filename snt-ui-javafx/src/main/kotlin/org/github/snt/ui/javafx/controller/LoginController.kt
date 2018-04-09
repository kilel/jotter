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

package org.github.snt.ui.javafx.controller

import javafx.fxml.FXML
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import org.github.snt.dao.api.entity.User
import org.github.snt.dao.api.repo.UserRepo
import org.github.snt.ui.javafx.lib.ApplicationState
import org.github.snt.ui.javafx.lib.StatefulScene
import org.github.snt.ui.javafx.lib.changeScene
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

class LoginController : AbstractController() {
    @FXML
    lateinit var loginField: TextField

    @FXML
    lateinit var passwordField: PasswordField

    @Autowired
    lateinit var state: ApplicationState

    @Autowired
    lateinit var mainController: MainController

    private val userRepo: UserRepo get() = dao.daoStore.userRepo
    private val authResourceRepo get() = dao.daoStore.authResourceRepo

    @Autowired
    @Qualifier(value = "mainScene")
    lateinit var mainScene: StatefulScene

    @FXML
    fun onLogin() {
        val user: User
        val masterKey = try {
            user = userRepo.loadByCode(loginField.text)
            authResourceRepo.checkPassword(user, passwordField.text)
        } catch (cause: Exception) {
            onError(cause)
            return
        }

        // open main scene
        changeScene(loginField.scene, mainScene)
        state.user = user
        state.masterKey = masterKey
        mainController.refresh()
    }

    @FXML
    fun onRegister() {
        val user = User()
        user.code = loginField.text
        user.description = "New user Registered from UI"

        try {
            userRepo.createNewUser(user, passwordField.text)
        } catch (cause: Exception) {
            onError(cause)
            return
        }

        // try login after successful registration
        onLogin()
    }
}