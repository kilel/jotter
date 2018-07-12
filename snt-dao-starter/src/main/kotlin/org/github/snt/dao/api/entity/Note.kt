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

import java.util.*
import javax.persistence.*

/**
 * Represents user note, which contains some data.
 * @constructor By default crete with no arguments.
 */
@Entity
@Table(name = "SN_NOTES")
class Note constructor() : AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    override var id: Long? = null

    @Column(name = "id_hi")
    var parentId: Long? = null

    @Column(name = "code")
    var code = "unknown"

    @Column(name = "dscr")
    var description = ""

    @Column(name = "data")
    var data: ByteArray = ByteArray(0)

    @Column(name = "schema_id")
    var schemaId: Long = DataSchemaType.DEFAULT.id

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_dt")
    var updateDate: Date = Date()

    /**
     * Constructor to create note with name but without data
     * @param code Note name
     */
    constructor(code: String) : this() {
        this.code = code
    }
}