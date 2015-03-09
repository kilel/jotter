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
import com.github.kilel.jotter.log.LogManager;
import com.github.kilel.jotter.ui.JotterUI;
import com.github.kilel.jotter.ui.UICommandParams;
import com.github.kilel.jotter.ui.console.command.ConsoleCommand;
import com.github.kilel.jotter.ui.console.util.ArgsParsingUtil;
import org.apache.log4j.Logger;

import java.util.Scanner;

/**
 * Console UI.
 */
public class ConsoleUI extends JotterUI {
    private final Logger log = LogManager.commonLog();

    public static void main(String[] args) {
        JotterUI ui = new ConsoleUI();
        ui.start();
    }

    @Override
    public void startInternal() {
        log.info("Starting Jotter console UI");
        log.info("Waiting for command");
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                execute(scanner.nextLine());

                if (isActive()) {
                    log.info("Waiting for command");
                } else {
                    break;
                }
            }
        }
        log.info("Finished main sequence of Jotter Console UI");
    }

    @Override
    public void stopInternal() {
    }

    private void execute(String command) {
        try {
            final JCommander parser = ArgsParsingUtil.parseCommand(command.trim());
            command = parser.getParsedCommand();
            final Object params = parser.getCommands().get(command).getObjects().get(0);
            log.info(String.format("Executing console UI command %s", command));
            ConsoleCommand.build(command, this, (UICommandParams) params).run();
        } catch (Exception e) {
            log.debug("Error executing command", e);
            log.error(String.format("Failed to execute command: %s", e.getMessage()));
        }
    }
}
