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

package org.github.snt.ui.javafx

import javafx.application.Application
import javafx.stage.Stage
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.github.snt.ui.javafx.config.SntUiConfig
import org.github.snt.ui.javafx.lib.StatefulScene
import org.github.snt.ui.javafx.lib.initWindow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Lazy
import java.security.Security

@Lazy
@SpringBootApplication
@EnableConfigurationProperties(value = [SntUiConfig::class])
class SntApplication : Application() {
    lateinit var springContext: ConfigurableApplicationContext

    @Autowired
    lateinit var config: SntUiConfig

    @Autowired
    @Qualifier(value = "loginScene")
    lateinit var loginScene: StatefulScene

    override fun init() {
        super.init()
        springContext = org.springframework.boot.SpringApplication.run(SntApplication::class.java, *Main.INIT_ARGS)
        springContext.autowireCapableBeanFactory.autowireBean(this)
    }

    override fun stop() {
        super.stop()
        springContext.close()
    }

    override fun start(rootWindow: Stage?) {
        // add bouncy castle support
        Security.addProvider(BouncyCastleProvider())

        if (rootWindow == null) {
            throw IllegalArgumentException("Scene is empty!")
        }

        initWindow(rootWindow) {
            title = config.name
            scene = loginScene.scene
        }

        rootWindow.show()
    }
}
