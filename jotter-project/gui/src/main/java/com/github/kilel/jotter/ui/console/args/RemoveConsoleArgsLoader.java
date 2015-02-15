/*
 * Copyright 2015 Kislitsyn Ilya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.kilel.jotter.ui.console.args;

import com.github.kilel.jotter.command.impl.UpdateCommand;
import com.github.kilel.jotter.common.Note;
import com.github.kilel.jotter.ui.console.util.ConsoleCommandParams;
import com.github.kilel.jotter.util.NoteUtils;

/**
 * Console args loader.
 */
public class RemoveConsoleArgsLoader implements UpdateCommand.ArgsLoader {
    private final ConsoleCommandParams.Remove params;

    public RemoveConsoleArgsLoader(ConsoleCommandParams.Remove params) {
        this.params = params;
    }

    @Override
    public Note getNoteToUpdate() {
        return NoteUtils.createNote(params.category, params.name);
    }

    @Override
    public Note getResultNote() {
        return null;
    }
}
