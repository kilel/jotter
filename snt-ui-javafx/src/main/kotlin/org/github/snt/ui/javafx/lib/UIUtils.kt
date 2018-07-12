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

package org.github.snt.ui.javafx.lib

import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.Stage
import org.github.snt.lib.err.SntException
import org.github.snt.lib.err.SntResult.GENERAL_ERROR
import org.github.snt.lib.util.extractStackTrace

fun initWindow(stage: Stage, call: Stage.() -> Unit) {
    stage.call()
}

fun changeScene(scene: Scene, targetScene: StatefulScene) {
    val stage = scene.window as Stage

    stage.scene = targetScene.scene
    stage.sizeToScene()
}

fun closeScene(scene: Scene) {
    val stage = scene.window as Stage
    stage.close()
}

fun showError(error: Throwable) {
    val cause = error as? SntException ?: SntException(GENERAL_ERROR, error)

    val alert = Alert(Alert.AlertType.ERROR)
    alert.title = "Application error"
    alert.headerText = "Error - ${cause.result}"
    alert.contentText = cause.message

    alert.dialogPane.expandableContent = buildDialogInfoArea("Stacktrace:", cause.extractStackTrace())

    alert.showAndWait()
}

fun requestConfirm(text: String): Boolean {
    val alert = Alert(Alert.AlertType.CONFIRMATION)
    alert.title = "Confirmation required"
    alert.headerText = text
    return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK
}

fun buildDialogInfoArea(header: String, data: String): GridPane {
    val label = Label(header)

    val textArea = TextArea(data)
    textArea.isEditable = false
    textArea.isWrapText = true

    textArea.maxWidth = java.lang.Double.MAX_VALUE
    textArea.maxHeight = java.lang.Double.MAX_VALUE


    GridPane.setVgrow(textArea, Priority.ALWAYS)
    GridPane.setHgrow(textArea, Priority.ALWAYS)

    val content = GridPane()
    content.maxWidth = java.lang.Double.MAX_VALUE
    content.add(label, 0, 0)
    content.add(textArea, 0, 1)

    return content
}

