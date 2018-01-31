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

package org.github.snt.api

import javax.persistence.*

/**
 * Root note for user.
 */
@Entity
@Table(name = "SN_NOTE_SRC")
class NoteSource() : AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    override var id: Long? = null

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    lateinit var user: User

    @OneToOne
    @JoinColumn(name = "noteId", nullable = false, unique = true)
    lateinit var note: Note

    @Column(name = "dscr", nullable = false)
    var description = "Auto-generated note's source"

    constructor(user: User, note: Note) : this() {
        this.user = user
        this.note = note
    }

}