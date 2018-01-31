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

package org.github.snt.api.dao.filter

import org.github.snt.api.AuthResourceType
import org.github.snt.api.User

class AuthResourceFilter() : Filter() {
    var user: User? = null
    var type: AuthResourceType? = null

    constructor(user: User, type: AuthResourceType) : this() {
        this.user = user
        this.type = type
    }
}