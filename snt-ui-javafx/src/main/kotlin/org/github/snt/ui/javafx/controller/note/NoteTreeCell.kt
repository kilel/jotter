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

import javafx.scene.control.cell.TextFieldTreeCell
import org.github.snt.dao.api.repo.NoteRepo

class NoteTreeCell constructor(val noteRepo: NoteRepo)
    : TextFieldTreeCell<NoteTreeItem>(NoteTreeItemConverter()) {

    override fun startEdit() {
        if (this.treeItem.value.allowsEdit()) {
            super.startEdit()
        }
    }

    override fun commitEdit(newValue: NoteTreeItem?) {
        if (!isEditing) {
            return
        }

        val treeNote = this.treeItem.value
        super.commitEdit(newValue)

        treeNote?.note?.let {
            val currentItem = noteRepo.loadById(it.id!!)
            currentItem.code = newValue.toString()
            noteRepo.save(currentItem)
            this.treeItem.value = NoteTreeItem(treeNote.parent, currentItem)
        }
    }
}