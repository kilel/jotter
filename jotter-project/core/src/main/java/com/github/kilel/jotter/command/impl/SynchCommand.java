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

package com.github.kilel.jotter.command.impl;

import com.github.kilel.jotter.command.Command;
import com.github.kilel.jotter.dao.DaoBridge;
import com.github.kilel.jotter.encryptor.EncryptionContext;
import com.github.kilel.jotter.encryptor.Encryptor;
import com.github.kilel.jotter.log.LogManager;
import com.github.kilel.jotter.msg.LoadResponse;
import com.github.kilel.jotter.util.NotesHolder;
import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Synchronization command.
 */
public class SynchCommand extends Command {
    private final Logger log = LogManager.commonLog();
    private final NotesHolder holder;
    private final EncryptionContext encryptionContext;
    private final Lock lock;

    private BigInteger lastSynchId = null;


    public SynchCommand(DaoBridge daoBridge, NotesHolder holder, EncryptionContext encryptionContext) {
        super(daoBridge);
        this.holder = holder;
        this.encryptionContext = encryptionContext;
        this.lock = new ReentrantLock();
    }

    @Override
    public void run() {
        // Try to lock synchronization.
        // If it is in progress, no need to start new.
        if(!lock.tryLock()) {
            return;
        }

        try {
            log.debug("Starting notes synchronization");

            final LoadResponse response = getDaoBridge().load(getRequestFactory().createLoad(lastSynchId));
            if (isSuccessful(response) && response.getLastSynchId() != null) {
                lastSynchId = response.getLastSynchId();
            }

            response.getEncryptedNoteList().getEncryptedNote().stream() //
                    .forEach((note) -> {
                        final String uniqueId = note.getEncryptorId();
                        final Encryptor encryptor = encryptionContext.get(uniqueId);
                        if (encryptor != null) {
                            holder.create(encryptor.decrypt(note));
                        }
                    });

            log.debug("Notes synchronization finished");
        } finally {
            lock.unlock();
        }
    }
}
