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

package org.github.snt.gui

import javafx.fxml.FXML
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.stage.Stage
import org.github.snt.api.User
import org.github.snt.api.dao.Dao
import org.github.snt.api.filter.BaseFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import javax.annotation.PostConstruct

class LoginController {
    @FXML
    lateinit var loginField: TextField

    @FXML
    lateinit var passwordField: PasswordField

    @Autowired
    lateinit var dao: Dao

    @Autowired
    @Qualifier(value = "mainScene")
    lateinit var mainScene: StatefulScene

    @FXML
    fun initialize() {
    }

    @PostConstruct
    fun init() {
        Thread.sleep(1)
    }

    @FXML
    fun onLogin() {
        val login = loginField.text

        // check user exists
        dao.daoStore.userRepo.loadOne(BaseFilter(login))

        // open main scene
        val stage = loginField.scene.window as Stage

        stage.scene = mainScene.scene
        stage.sizeToScene()
    }

    @FXML
    fun onRegister() {
        val user = User()
        user.code = loginField.text
        user.description = "New user Registered from UI"
        dao.daoStore.userRepo.save(user)

        // try login after registration
        onLogin()
    }
}