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

package org.github.snt.ui.javafx.controller

import javafx.fxml.FXML
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import org.github.snt.dao.api.entity.Note
import org.github.snt.dao.api.filter.NoteFilter
import org.github.snt.dao.api.filter.NoteSourceFilter
import org.github.snt.ui.javafx.lib.ApplicationState
import org.springframework.beans.factory.annotation.Autowired
import java.util.stream.Stream
import javax.annotation.PostConstruct

class MainController : AbstractController() {

    @FXML
    lateinit var notesTree: TreeView<NoteItem>

    @Autowired
    lateinit var state: ApplicationState

    private val noteSourceRepo get() = dao.daoStore.noteSourceRepo
    private val noteRepo get() = dao.daoStore.noteRepo

    @PostConstruct
    fun init() {
        refresh()
    }

    fun refresh() {
        notesTree.root = null

        // nothing to render if user is not selected
        val user = state.user ?: return

        val sources = noteSourceRepo.loadList(NoteSourceFilter(user))

        val treeRoot = TreeItem<NoteItem>()
        treeRoot.isExpanded = true

        sources.stream()//
                .map { it.note }
                .addtoParentNode(treeRoot)

        notesTree.root = treeRoot
    }

    private fun Stream<Note>.addtoParentNode(parent: TreeItem<NoteItem>) {
        this.sorted { a, b -> a.id!!.compareTo(b.id!!) }
                .forEach {
                    val child = TreeItem(NoteItem(it))
                    child.isExpanded = state.expandedNotes.contains(it.id)
                    parent.children.add(child)
                    fillChilds(child)
                }
    }

    private fun fillChilds(parent: TreeItem<NoteItem>) {
        noteRepo.loadList(NoteFilter(parent.value.note)) //
                .stream()//
                .addtoParentNode(parent)
    }

    class NoteItem(val note: Note) {
        override fun toString(): String {
            return note.code
        }
    }
}