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
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.util.Callback
import org.github.snt.dao.api.entity.DataSchemaType
import org.github.snt.dao.api.entity.Note
import org.github.snt.dao.api.filter.NoteFilter
import org.github.snt.dao.api.filter.NoteSourceFilter
import org.github.snt.ui.javafx.controller.MainController
import org.github.snt.ui.javafx.lib.*
import java.util.*

class NoteTree constructor(private val mainController: MainController) {

    private val notesTree: TreeView<NoteTreeItem> get() = mainController.notesTree
    private val contentGrid: GridPane get() = mainController.contentGrid
    private val contentRootGrid: GridPane get() = mainController.contentRootGrid
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
        notesTree.selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue ->
            if (newValue != null) {
                onSelected(newValue as TreeItem<NoteTreeItem>, isEdit = false)
            } else {
                cleanContent()
            }
        }
    }

    fun onSelected(selectedItem: TreeItem<NoteTreeItem>, isEdit: Boolean) {
        val noteItem = selectedItem.value
        val note = noteItem?.note

        val needClean = note == null || noteItem.noteSource != null

        // some notes can't be displayed
        if (needClean) {
            cleanContent()
            return
        } else {
            fillContent(note, isEdit)
        }
    }

    private fun getNoteNameNode(note: Note?, isEdit: Boolean): Node {
        if (note == null) {
            return Label()
        }

        return newTextField(note.code, isEditable = isEdit)
    }

    private fun fillContent(note: Note?, isEdit: Boolean) {
        updateNoteNameLabel(note, isEdit)
        cleanContextGrid()

        contentGrid.addRow(0, newLabel("", isUnderlined = true))
        contentGrid.addRow(1, newLabel("Description", isUnderlined = true))
        contentGrid.addRow(2, newTextArea(note?.description ?: "",
                id = "ext-descr",
                isEditable = isEdit,
                background = contentGrid.background))

        // TODO fill content pane from schema

    }

    private fun cleanContent() {
        updateNoteNameLabel(null, isEdit = false)
        cleanContextGrid()
    }

    private fun cleanContextGrid() {
        contentGrid.children.clear()

        contentGrid.columnConstraints.clear()
        contentGrid.columnConstraints.add(ColumnConstraints())

        contentGrid.rowConstraints.clear()
    }

    private fun addChildNotes(parent: TreeItem<NoteTreeItem>, notes: List<NoteTreeItem>) {
        notes.sortedWith(Comparator.comparing<NoteTreeItem, Long> { x -> x.order })//
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
        result.expandedProperty().addListener { observable, oldValue, newValue ->
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
        }
        return result
    }

    private fun updateNoteNameLabel(note: Note?, isEdit: Boolean) {
        // remove elements from first column
        contentRootGrid.children.removeAll(getNoteNameLabelNodes())

        // need to add as first child to the grid for correct focus transfer
        val newNameNode = getNoteNameNode(note, isEdit)
        GridPane.setConstraints(newNameNode, 0, 0)
        contentRootGrid.children.add(0, newNameNode)

        if (isEdit) {
            newNameNode.requestFocus()
        }
    }

    private fun getNoteNameLabelNodes(): List<Node> {
        return contentRootGrid.children.filter { GridPane.getRowIndex(it) == 0 }
    }

    fun updateNoteDetails(selectedItem: TreeItem<NoteTreeItem>?) {
        val note = selectedItem?.value?.note ?: return

        val noteNameLabelNodes = getNoteNameLabelNodes()
        assert(noteNameLabelNodes.size == 1)

        val nameNode = noteNameLabelNodes[0] as? TextField ?: return

        val currentItem = noteRepo.loadById(note.id!!)
        currentItem.code = nameNode.text

        //TODO - fill description and details from format
        val descrArea = contentGrid.children.find { it.id == "ext-descr" } as? TextArea

        descrArea?.let { currentItem.description = it.text }

        noteRepo.save(currentItem)
        selectedItem.value = NoteTreeItem(selectedItem.value.parent, currentItem)
    }
}