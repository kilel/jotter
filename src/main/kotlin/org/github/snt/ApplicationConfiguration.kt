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

package org.github.snt

import org.github.snt.ui.LoginController
import org.github.snt.ui.MainController
import org.github.snt.ui.StatefulScene
import org.github.snt.ui.WindowTypes
import org.jasypt.salt.RandomSaltGenerator
import org.jasypt.salt.SaltGenerator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfiguration {

    @Bean
    @Qualifier(value = "loginScene")
    fun getLoginScene(): StatefulScene {
        return WindowTypes.login.buildScene()
    }

    @Bean
    fun getLoginController(): LoginController {
        return getLoginScene().controller as LoginController
    }

    @Bean
    @Qualifier(value = "mainScene")
    fun getMainScene(): StatefulScene {
        return WindowTypes.main.buildScene()
    }

    @Bean
    fun getMainController(): MainController {
        return getMainScene().controller as MainController
    }

    @Bean
    fun getSaltGenerator(): SaltGenerator {
        return RandomSaltGenerator()
    }
}
