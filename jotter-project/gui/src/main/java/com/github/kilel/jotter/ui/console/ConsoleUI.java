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

package com.github.kilel.jotter.ui.console;

import com.beust.jcommander.JCommander;
import com.github.kilel.jotter.command.impl.UpdateCommand;
import com.github.kilel.jotter.common.Note;
import com.github.kilel.jotter.log.LogManager;
import com.github.kilel.jotter.ui.JotterUI;
import com.github.kilel.jotter.ui.console.args.AddConsoleArgsLoader;
import com.github.kilel.jotter.ui.console.args.RemoveConsoleArgsLoader;
import com.github.kilel.jotter.ui.console.args.UpdateConsoleArgsLoader;
import com.github.kilel.jotter.ui.console.util.ArgsParsingUtil;
import com.github.kilel.jotter.ui.console.util.ConsoleCommandParams;
import com.github.kilel.jotter.util.NoteUtils;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Console UI.
 */
public class ConsoleUI extends JotterUI {
    private final Logger log = LogManager.commonLog();
    private final Map<String, Function<Object, Boolean>> actions;

    public ConsoleUI() {
        actions = new HashMap<>();
        actions.put("stop", (x) -> {
            log.info("Stopping Jotter console UI");
            return false;
        });
        actions.put("list", (x) -> {
            StringBuilder builder = new StringBuilder("Notes:\n");
            for (String category : getContext().getHolder().getCategories()) {
                builder.append(category).append("\n");
                for (String name : getContext().getHolder().getNames(category)) {
                    builder.append("\t").append(name).append("\n");
                }
            }
            log.info(builder.toString());
            return true;
        });
        actions.put("get", (x) -> {
            if (!(x instanceof ConsoleCommandParams.Get)) {
                throw new IllegalArgumentException("Calling 'get' command with illegal parameres");
            }
            ConsoleCommandParams.Get params = (ConsoleCommandParams.Get) x;
            Note note = getContext().getHolder().get(params.category, params.name);
            if (note == null) {
                log.info("No such note");
            } else {
                Toolkit.getDefaultToolkit().getSystemClipboard().//
                        setContents(new StringSelection(note.getValue()), null);
                log.info(NoteUtils.toString(note));
            }

            return true;
        });
        actions.put("add", (x) -> {
            if (!(x instanceof ConsoleCommandParams.Add)) {
                throw new IllegalArgumentException("Calling 'add' command with illegal parameres");
            }
            ConsoleCommandParams.Add params = (ConsoleCommandParams.Add) x;
            new UpdateCommand(getContext(), new AddConsoleArgsLoader(params)).run();
            return true;
        });
        actions.put("remove", (x) -> {
            if (!(x instanceof ConsoleCommandParams.Remove)) {
                throw new IllegalArgumentException("Calling 'remove' command with illegal parameres");
            }
            ConsoleCommandParams.Remove params = (ConsoleCommandParams.Remove) x;
            new UpdateCommand(getContext(), new RemoveConsoleArgsLoader(params)).run();
            return true;
        });
        actions.put("update", (x) -> {
            if (!(x instanceof ConsoleCommandParams.Update)) {
                throw new IllegalArgumentException("Calling 'get' command with illegal parameres");
            }
            ConsoleCommandParams.Update params = (ConsoleCommandParams.Update) x;
            new UpdateCommand(getContext(), new UpdateConsoleArgsLoader(params)).run();
            return true;
        });
        actions.put("help", (x) -> {
            //log.info("Command list: add, update, remove, list, view, stop");
            return true;
        });
    }

    public static void main(String[] args) {
        JotterUI ui = new ConsoleUI();
        ui.start();
    }

    @Override
    public void startInternal() {
        log.info("Starting Jotter console UI");
        Scanner scanner = new Scanner(System.in);
        log.info("Waiting for command");
        while (scanner.hasNextLine()) {
            if (!execute(scanner.nextLine())) {
                break;
            }
            log.info("Waiting for command");
        }

        scanner.close();
        stop();
    }

    @Override
    public void stopInternal() {
    }

    boolean execute(String command) {
        try {
            final JCommander parser = ArgsParsingUtil.parseCommand(command.trim());
            command = parser.getParsedCommand();
            final Object params = parser.getCommands().get(command).getObjects().get(0);

            log.info(String.format("Executing console UI command %s", command));

            if (actions.containsKey(command)) {
                return actions.get(command).apply(params);
            } else {
                log.info(String.format("Unknown command %s", command));
            }

        } catch (Exception e) {
            log.debug("Error executing command", e);
            log.error(String.format("Failed to execute command: %s", e.getMessage()));
        }
        return true;
    }
}
