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

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "snt")
class SntConfig {
    companion object {
        /**
         * Internal constant to check that master key vas successfully recovered.
         */
        val AUTH_RES_CHECK = "781877782b3518677fce3c28223b7da84cfeeb85a2c7b9739eb2cdbe6ab7d684aa2e375101ce035de51694b81262731eccb5d75dbdea5b4a1a2e626b128ef045".toByteArray()
    }

    var ui = UI()
    var security = SntConfig.Security()

    class UI {
        lateinit var name: String
    }

    class Security {
        var encryptionAlgo: String = "PBEWithSHA256And256BitAES-CBC-BC"
        var masterKeyLength = 256
    }
}