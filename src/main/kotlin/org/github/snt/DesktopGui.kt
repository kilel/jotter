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
import org.github.snt.gui.StatefulScene
import org.github.snt.lib.initWindow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Lazy

@Lazy
@SpringBootApplication
class DesktopGui : AbstractJavaFxSpringBootApplication() {

    @Value("ui.name")
    var appName: String = "SNT - Secure Notepad"

    @Autowired
    @Qualifier(value = "loginScene")
    lateinit var loginScene: StatefulScene

    override fun start(rootWindow: Stage?) {
        if (rootWindow == null) {
            throw IllegalArgumentException("Scene is empty!")
        }

        initWindow(rootWindow) {
            title = appName
            scene = loginScene.scene
        }

        rootWindow.show()
    }
}
