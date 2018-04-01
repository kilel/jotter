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

/**
 * Root note for user.
 */
@Entity
@Table(name = "SN_AUTH_RES")
class AuthResource() : AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    override var id: Long? = null

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    lateinit var user: User

    @Column(name = "code", nullable = false, unique = true)
    lateinit var code: String

    @Column(name = "dscr", nullable = true)
    var description = "No description"

    @Column(name = "typeId", nullable = false)
    var typeId: Long = AuthResourceType.PASSWORD.id

    @Column(name = "data", nullable = false, columnDefinition = "longvarbinary")
    lateinit var data: ByteArray

    @Column(name = "data_ck", nullable = false, columnDefinition = "longvarbinary")
    lateinit var check: ByteArray

    constructor(user: User, type: AuthResourceType = AuthResourceType.PASSWORD) : this() {
        this.user = user
        this.typeId = type.id
    }
}