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

package org.github.snt.ui.javafx.controller.note

import javafx.scene.control.TreeItem
import org.github.snt.dao.api.entity.Note
import org.github.snt.dao.api.entity.NoteSource

class NoteTreeItem
private constructor(val parent: TreeItem<NoteTreeItem>? = null,
                    val noteSource: NoteSource? = null,
                    val note: Note? = null,
                    val name: String? = null) {

    val order: Long get() = note?.id ?: 0

    constructor(parent: TreeItem<NoteTreeItem>?, noteSource: NoteSource) : this(parent, noteSource, noteSource.note, noteSource.description)
    constructor(parent: TreeItem<NoteTreeItem>?, note: Note) : this(parent, note = note, name = note.code)
    constructor(name: String?) : this(parent = null, name = name)

    override fun toString(): String {
        return name ?: "Empty note"
    }

    fun allowsEdit(): Boolean {
        //can't update root node
        return parent != null
                // can't update note source
                && noteSource == null
                // note should be defined to change
                && note != null

    }
}