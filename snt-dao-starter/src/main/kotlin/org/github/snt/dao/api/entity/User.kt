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

package org.github.snt.dao.api.entity

import javax.persistence.*
import javax.validation.constraints.NotNull

/**
 * Represents service user.
 * @constructor By default crete with no arguments.
 */
@Entity
@Table(name = "SN_USERS")
class User constructor() : AbstractEntity {
    @Id
    @GeneratedValue
    @Column
    override var id: Long? = null

    @Column(name = "code")
    @NotNull
    var code = "unknown"

    @Column(name = "dscr")
    var description = ""

    /**
     * Creates user by code with description
     * @param code User code
     * @param description Optional description.
     */
    constructor(code: String, description: String? = null) : this() {
        this.code = code
        this.description = description ?: "None description"
    }
}