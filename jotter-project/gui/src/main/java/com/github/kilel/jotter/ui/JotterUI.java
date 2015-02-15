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

package com.github.kilel.jotter.ui;

import com.github.kilel.jotter.JotterContext;
import com.github.kilel.jotter.log.LogManager;
import com.github.kilel.jotter.util.NoteUtils;
import com.github.kilel.jotter.util.NotesSynchronizer;

/**
 * Common UI facade.
 */
public abstract class JotterUI {
    private final NotesSynchronizer notesSynchronizer;
    private final JotterContext context;

    public JotterUI() {
        LogManager.init();
        this.context = new JotterContext("mem");
        notesSynchronizer = new NotesSynchronizer(context);

        // add log listener
        context.getHolder().//
                addListener((x) -> LogManager//
                .commonLog().debug(//
                                   String.format("Executed event %s for note [%s]",//
                                                 x.getType(),//
                                                 NoteUtils.toString(x.getSource())//
                                                )//
                                  )//
                           );
    }

    public JotterContext getContext() {
        return context;
    }

    public final void start() {
        notesSynchronizer.start();
        startInternal();
    }

    public final void stop() {
        notesSynchronizer.stop();
        stopInternal();
    }

    public abstract void startInternal();

    public abstract void stopInternal();

}
