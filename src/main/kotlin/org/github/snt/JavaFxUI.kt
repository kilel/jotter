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

import javafx.stage.Stage
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.github.snt.ui.StatefulScene
import org.github.snt.ui.initWindow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Lazy
import java.security.Security

@Lazy
@SpringBootApplication
@EnableConfigurationProperties(value = [SntConfig::class])
class JavaFxUI : AbstractJavaFxSpringBootApplication() {

    @Autowired
    lateinit var config: SntConfig

    @Autowired
    @Qualifier(value = "loginScene")
    lateinit var loginScene: StatefulScene

    override fun start(rootWindow: Stage?) {
        // add bouncy castle support
        Security.addProvider(BouncyCastleProvider())

        if (rootWindow == null) {
            throw IllegalArgumentException("Scene is empty!")
        }

        initWindow(rootWindow) {
            title = config.ui.name
            scene = loginScene.scene
        }

        rootWindow.show()
    }
}
