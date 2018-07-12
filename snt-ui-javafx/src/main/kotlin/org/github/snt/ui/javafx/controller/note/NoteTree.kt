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

import javafx.beans.property.BooleanProperty
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.AnchorPane
import javafx.util.Callback
import org.github.snt.dao.api.entity.DataSchemaType
import org.github.snt.dao.api.entity.Note
import org.github.snt.dao.api.filter.NoteFilter
import org.github.snt.dao.api.filter.NoteSourceFilter
import org.github.snt.ui.javafx.controller.MainController
import org.github.snt.ui.javafx.lib.ApplicationState
import org.github.snt.ui.javafx.lib.requestConfirm
import java.util.*

class NoteTree constructor(private val mainController: MainController) {

    private val notesTree: TreeView<NoteTreeItem> get() = mainController.notesTree
    private val notesContent: AnchorPane get() = mainController.notesContent
    private val state: ApplicationState get() = mainController.state

    private val dao get() = mainController.dao
    private val noteSourceRepo get() = dao.daoStore.noteSourceRepo
    private val noteRepo get() = dao.daoStore.noteRepo

    fun refreshTree() {
        notesTree.root = null

        // nothing to render if user is not selected
        val user = state.user ?: return

        val sources = noteSourceRepo.loadList(NoteSourceFilter(user))

        val treeRoot = createTreeItem(NoteTreeItem("All user note sources"))
        treeRoot.isExpanded = true

        addChildNotes(treeRoot, sources.map { NoteTreeItem(treeRoot, it) })

        notesTree.root = treeRoot

        // support renaming behavior
        notesTree.isEditable = true
        notesTree.cellFactory = Callback { NoteTreeCell(noteRepo) }
    }

    private fun addChildNotes(parent: TreeItem<NoteTreeItem>, notes: List<NoteTreeItem>) {
        notes.sortedWith(Comparator.comparing<NoteTreeItem, Long>({ x -> x.order }))//
                .forEach {
                    val currentNote = createTreeItem(it)
                    val noteId = it.note?.id

                    currentNote.isExpanded = state.expandedNotes.contains(noteId)
                    parent.children.add(currentNote)

                    if (noteId != null) {
                        val childNotes = noteRepo.loadList(NoteFilter(parentId = noteId))
                        addChildNotes(currentNote, childNotes.map { NoteTreeItem(currentNote, it) })
                    }
                }
    }

    fun startRenaming(item: TreeItem<NoteTreeItem>?) {
        item?.run {
            notesTree.edit(this)
        }
    }

    fun remove(item: TreeItem<NoteTreeItem>?) {
        val currentNote = item?.value?.note
        if (item == null || currentNote == null) {
            return
        }


        if (!item.value.allowsEdit()) {
            return
        }

        if (!requestConfirm("Confirm remove for the note '${currentNote.code}'")) {
            return
        }

        noteRepo.remove(NoteFilter(currentNote))
        item.parent.children.remove(item)
    }

    fun addChildNote(item: TreeItem<NoteTreeItem>?) {
        val currentNote = item?.value?.note
        if (item == null || currentNote == null) {
            return
        }

        // save new note with std name
        val newNote = doAddChildNote(currentNote)
        val childTreeItem = createTreeItem(NoteTreeItem(item, newNote))

        // add to UI tree childs
        item.children.add(childTreeItem)
        item.isExpanded = true

        // start renaming instantly
        notesTree.selectionModel.select(childTreeItem)
        notesTree.requestFocus()

        // prepare tree layout for new item to be able to edit
        notesTree.layout()

        startRenaming(childTreeItem)
    }

    private fun doAddChildNote(parent: Note): Note {
        val note = Note("New note")
        note.parentId = parent.id
        note.schemaId = DataSchemaType.DEFAULT.id

        noteRepo.save(note)
        return note
    }

    private fun createTreeItem(item: NoteTreeItem): TreeItem<NoteTreeItem> {
        val result = TreeItem(item)
        result.expandedProperty().addListener({ observable, oldValue, newValue ->
            val booleanProperty = observable as BooleanProperty
            val treeItem = booleanProperty.bean as TreeItem<*>
            val note = (treeItem.value as NoteTreeItem).note
            if (note != null) {
                if (newValue) {
                    state.expandedNotes.add(note.id!!)
                } else {
                    state.expandedNotes.remove(note.id)
                }
            }
        })
        return result
    }
}