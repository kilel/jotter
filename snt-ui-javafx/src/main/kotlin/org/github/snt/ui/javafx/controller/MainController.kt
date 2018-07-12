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

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.TreeView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.AnchorPane
import org.github.snt.ui.javafx.controller.note.NoteTree
import org.github.snt.ui.javafx.controller.note.NoteTreeItem
import org.github.snt.ui.javafx.lib.ApplicationState
import org.github.snt.ui.javafx.lib.StatefulScene
import org.github.snt.ui.javafx.lib.changeScene
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import javax.annotation.PostConstruct

class MainController : AbstractController() {

    @FXML
    lateinit var notesTree: TreeView<NoteTreeItem>

    @FXML
    lateinit var notesContent: AnchorPane

    @Autowired
    lateinit var state: ApplicationState

    private val tree = NoteTree(this)

    @Autowired
    @Qualifier(value = "loginScene")
    lateinit var loginScene: StatefulScene

    private val selectedItem get() = notesTree.selectionModel.selectedItem

    @PostConstruct
    fun init() {
        tree.refreshTree()
    }

    @FXML
    fun onKeyTyped(event: KeyEvent) {
        when {
            event.code == KeyCode.DELETE -> onRemove()
            event.code == KeyCode.F5 -> onTreeRefresh()
        }

        if (event.isControlDown) {
            when {
                event.code == KeyCode.N -> onAddChild()
                event.code == KeyCode.R -> onTreeRefresh()
            }
        }
    }

    @FXML
    fun onAddChild() {
        try {
            tree.addChildNote(selectedItem)
        } catch (cause: Exception) {
            onError(cause)
        }
    }

    @FXML
    fun onRemove() {
        try {
            tree.remove(selectedItem)
        } catch (cause: Exception) {
            onError(cause)
        }
    }

    @FXML
    fun onRename() {
        try {
            tree.startRenaming(selectedItem)
        } catch (cause: Exception) {
            onError(cause)
        }
    }

    @FXML
    fun onTreeRefresh() {
        try {
            tree.refreshTree()
        } catch (cause: Exception) {
            onError(cause)
        }
    }

    @FXML
    fun onLogout() {
        try {
            state.logout()
            changeScene(notesTree.scene, loginScene)
        } catch (cause: Exception) {
            onError(cause)
        }
    }

    @FXML
    fun onExit() {
        try {
            Platform.exit()
            System.exit(0)
        } catch (cause: Exception) {
            onError(cause)
        }
    }
}