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

package com.github.kilel.jotter.gui.console.util;

import com.github.kilel.jotter.command.impl.UpdateCommand;
import com.github.kilel.jotter.common.Note;
import com.github.kilel.jotter.log.LogManager;
import org.apache.log4j.Logger;

import java.util.Scanner;

/**
 * Console args loader.
 */
public class UpdateConsoleArgsLoader implements UpdateCommand.ArgsLoader {
    private final Logger log = LogManager.commonLog();
    private final Scanner scanner;
    private final  String command;

    public UpdateConsoleArgsLoader(Scanner scanner, String command) {
        this.scanner = scanner;
        this.command = command;
    }

    // add remove update
    @Override
    public Note getNoteToUpdate() {
        // for update and remove read node path
        if(!command.equalsIgnoreCase("add")) {
            log.info("Select node");
            return readNotePath();
        }

        // for create nothing to select
        return null;
    }

    @Override
    public Note getResultNote() {
        if(command.equalsIgnoreCase("add")) {
            return readNote();
        }

        // for remove nothing to select
        return null;
    }

    private Note readNotePath() {
        final Note note = new Note();
        {
            log.info("Category:");
            final String category = scanner.nextLine().trim();
            log.info("Name:");
            final String name = scanner.nextLine().trim();

            note.setCategory(category);
            note.setName(name);
        }
        return note;
    }

    private Note readNote() {
        final Note note = readNotePath();
        {
            log.info("Value:");
            final String value = scanner.nextLine().trim();
            log.info("Encryptor:");
            final String encryptor = scanner.nextLine().trim();

            note.setValue(value);
            note.setEncryptorId(encryptor);
        }
        return note;
    }
}
