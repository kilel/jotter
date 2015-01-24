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

package com.github.kilel.jotter.gui.console;

import com.github.kilel.jotter.common.EncryptedNote;
import com.github.kilel.jotter.common.Note;
import com.github.kilel.jotter.dao.factory.RequestFactory;
import com.github.kilel.jotter.gui.JotterGui;
import com.github.kilel.jotter.log.LogManager;
import com.github.kilel.jotter.util.NoteUtils;
import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Console GUI.
 */
public class ConsoleGui extends JotterGui {
    private final Logger log = LogManager.commonLog();
    private final Map<String, Function<Scanner, Boolean>> actions;

    public ConsoleGui() {
        actions = new HashMap<>();
        actions.put("stop", (x) -> {
            return false;
        });
        actions.put("list", (x) -> {
            StringBuilder builder = new StringBuilder("Notes:\n");
            for (String category : getNotesHolder().getCategories()) {
                builder.append(category).append("\n");
                for (String name : getNotesHolder().getNames(category)) {
                    builder.append("\t").append(name).append("\n");
                }
            }
            log.info(builder.toString());
            return true;
        });
        actions.put("add", (x) -> {
            Note note = new Note();
            log.info("Category:");
            note.setCategory(x.nextLine().trim());
            log.info("Name:");
            note.setName(x.nextLine().trim());
            log.info("Value:");
            note.setValue(x.nextLine().trim());
            note.setId(0);
            note.setSynchId(BigInteger.ZERO);

            EncryptedNote result = getEncryptionContext().get("").encrypt(note);
            getDaoBridge().update(RequestFactory.createUpdate(Collections.singletonList(result)));
            getNotesSynchronizer().immediate();
            return true;
        });
        actions.put("view", (x) -> {
            log.info("Category:");
            String category = x.nextLine().trim();
            log.info("Name:");
            String name = x.nextLine().trim();
            Note note = getNotesHolder().get(category, name);
            if (note == null) {
                log.info("No such note");
            } else {
                log.info(NoteUtils.toString(note));
            }
            return true;
        });
        actions.put("remove", (x) -> {
            // TODO
            return true;
        });
        actions.put("update", (x) -> {
            // TODO
            return true;
        });
        actions.put("help", (x) -> {
            // TODO
            return true;
        });
    }

    public static void main(String[] args) {
        JotterGui gui = new ConsoleGui();
        gui.start();
    }

    @Override
    public void startInternal() {
        log.info("Starting Jotter console GUI");
        final Scanner scanner = new Scanner(System.in);
        log.info("Waiting for command");
        while (scanner.hasNextLine()) {
            if (!execute(scanner, scanner.nextLine())) {
                break;
            }
            log.info("Waiting for command");
        }

        log.info("Stopping Jotter console GUI");
        scanner.close();
        stop();
    }

    @Override
    public void stopInternal() {

    }

    boolean execute(Scanner scanner, String command) {
        command = command.trim();
        log.info(String.format("Executing console gui command %s", command));

        if (actions.containsKey(command)) {
            try {
                return actions.get(command).apply(scanner);
            } catch (Exception e) {
                log.error("Error executing command", e);
                return true;
            }
        } else {
            log.info(String.format("Unknown command %s", command));
            return true;
        }
    }
}
