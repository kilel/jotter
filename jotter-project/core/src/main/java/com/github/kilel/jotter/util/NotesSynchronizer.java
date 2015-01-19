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

package com.github.kilel.jotter.util;

import com.github.kilel.jotter.command.Command;
import com.github.kilel.jotter.command.impl.SynchCommand;
import com.github.kilel.jotter.dao.DaoBridge;
import com.github.kilel.jotter.encryptor.EncryptionContext;
import com.github.kilel.jotter.log.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Synchronizes notes with DAO.
 */
public class NotesSynchronizer {
    private final Logger log = LogManager.commonLog();
    private final long delay = 5; // 5 seconds
    private final Command synchCommand;
    private ScheduledExecutorService schedule;
    private boolean isStarted = false;

    public NotesSynchronizer(DaoBridge daoBridge, NotesHolder holder, EncryptionContext encryptionContext) {
        this.synchCommand = new SynchCommand(daoBridge, holder, encryptionContext);
    }

    /**
     * Starts synchronization.
     */
    public void start() {
        if (isStarted) {
            return;
        }

        log.debug("Creating notes synchronization schedule");
        schedule = Executors.newScheduledThreadPool(1);
        schedule.scheduleAtFixedRate(synchCommand, 0, delay, TimeUnit.SECONDS);
        isStarted = true;
    }

    /**
     * Ends synchronization.
     */
    public void stop() {
        if (!isStarted) {
            return;
        }

        log.debug("Deleting synchronization schedule");
        schedule.shutdown();
        isStarted = false;
    }

    public void immediate() {
        synchCommand.run();
    }

}
