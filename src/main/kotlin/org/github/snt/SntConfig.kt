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
    var ui = UI()
    var security = Security()
    var encryption = Encryption()
    var digest = Digest()

    class UI {
        lateinit var name: String
    }

    class Security {
        var masterKeySize = 256

    }

    class Encryption {
        // supported 128, 196, 256
        var keySize = 128
        var saltSize = 256
        var iterations = 1000
    }

    class Digest {
        var hashAlgo = "SHA-256"
        var saltSize = 16
        var iterations = 10_000
    }
}