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
import javax.validation.constraints.NotNull

/**
 * Represents user note, which contains some data.
 * @constructor By default crete with no arguments.
 */
@Entity
@Table(name = "SN_NOTES")
class Note constructor() : AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    override var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hi")
    var parent: Note? = null

    @Column(name = "code", unique = true, nullable = false)
    @NotNull
    var code = "unknown"

    @Column(name = "dscr")
    var description = ""

    @Column(name = "data", columnDefinition = "longvarbinary")
    var data: ByteArray = ByteArray(0)

    @Column(name = "schema")
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