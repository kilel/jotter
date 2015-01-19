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

import com.github.kilel.jotter.gui.JotterGui;
import com.github.kilel.jotter.log.LogManager;
import org.apache.log4j.Logger;

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
            // TODO
            return true;
        });
        actions.put("add", (x) -> {
            // TODO
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

        while (scanner.hasNext()) {
            log.info("Waiting for command");
            if (!execute(scanner, scanner.next())) {
                break;
            }
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
            return actions.get(command).apply(scanner);
        } else {
            log.info(String.format("Unknown command %s", command));
            return true;
        }
    }
}
