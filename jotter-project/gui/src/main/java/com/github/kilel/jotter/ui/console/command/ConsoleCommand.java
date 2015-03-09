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

package com.github.kilel.jotter.ui.console.command;

import com.github.kilel.jotter.JotterContext;
import com.github.kilel.jotter.command.Command;
import com.github.kilel.jotter.command.impl.UpdateCommand;
import com.github.kilel.jotter.common.Note;
import com.github.kilel.jotter.log.LogManager;
import com.github.kilel.jotter.ui.ArgsLoaderFactory;
import com.github.kilel.jotter.ui.JotterUI;
import com.github.kilel.jotter.ui.UICommandParams;
import com.github.kilel.jotter.util.NoteUtils;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Console command
 */
public abstract class ConsoleCommand<Params extends UICommandParams> extends Command {
    private static final Map<String, Class<?>> commands;
    private final Logger log = LogManager.commonLog();
    private Params params;

    static {
        commands = new ConcurrentHashMap<>();
        commands.put("get", Get.class);
        commands.put("add", Add.class);
        commands.put("update", Update.class);
        commands.put("remove", Remove.class);
        commands.put("list", List.class);
        commands.put("stop", Stop.class);
    }

    public ConsoleCommand(JotterContext context) {
        super(context);
    }

    public static synchronized ConsoleCommand //
    build(String name, JotterUI ui, UICommandParams params)//
            throws Exception {
        final ConsoleCommand command;
        if (name != null && commands.containsKey(name)) {
            Class<?> cmdClass = commands.get(name);
            command = (ConsoleCommand) cmdClass.getConstructor(JotterUI.class).newInstance(ui);
            command.setParams(params);
        } else {
            command = null;
        }

        if (command == null) {
            throw new RuntimeException("Unsupported command " + name);
        }

        return command;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public Logger getLog() {
        return log;
    }

    private static class Get extends ConsoleCommand<UICommandParams.Get> {
        public Get(JotterUI ui) {
            super(ui.getContext());
        }

        @Override
        public void run() {
            UICommandParams.Get params = getParams();
            Note note = getContext().getHolder().get(params.getCategory(), params.getName());
            if (note == null) {
                getLog().info("No such note");
            } else {
                Toolkit.getDefaultToolkit().getSystemClipboard().//
                        setContents(new StringSelection(note.getValue()), null);
                getLog().info(NoteUtils.toString(note));
            }
        }
    }

    private static class Add extends ConsoleCommand<UICommandParams.Add> {
        public Add(JotterUI ui) {
            super(ui.getContext());
        }

        @Override
        public void run() {
            new UpdateCommand(getContext(), ArgsLoaderFactory.createUpdateArgsLoader(getParams())).run();
        }
    }

    private static class Update extends ConsoleCommand<UICommandParams.Update> {
        public Update(JotterUI ui) {
            super(ui.getContext());
        }

        @Override
        public void run() {
            new UpdateCommand(getContext(), ArgsLoaderFactory.createUpdateArgsLoader(getParams())).run();
        }
    }

    private static class Remove extends ConsoleCommand<UICommandParams.Remove> {
        public Remove(JotterUI ui) {
            super(ui.getContext());
        }

        @Override
        public void run() {
            new UpdateCommand(getContext(), ArgsLoaderFactory.createUpdateArgsLoader(getParams())).run();
        }
    }

    private static class List extends ConsoleCommand<UICommandParams.List> {
        public List(JotterUI ui) {
            super(ui.getContext());
        }

        @Override
        public void run() {
            StringBuilder builder = new StringBuilder("Notes:\n");
            for (String category : getContext().getHolder().getCategories()) {
                builder.append(category).append("\n");
                for (String name : getContext().getHolder().getNames(category)) {
                    builder.append("\t").append(name).append("\n");
                }
            }
            getLog().info(builder.toString());
        }
    }

    private static class Stop extends ConsoleCommand<UICommandParams.Stop> {
        private final JotterUI ui;

        public Stop(JotterUI ui) {
            super(ui.getContext());
            this.ui = ui;
        }

        @Override
        public void run() {
            ui.stop();
        }
    }


}
