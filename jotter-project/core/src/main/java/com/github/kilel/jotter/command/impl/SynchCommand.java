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

import com.github.kilel.jotter.JotterContext;
import com.github.kilel.jotter.command.Command;
import com.github.kilel.jotter.dao.factory.RequestFactory;
import com.github.kilel.jotter.encryptor.Encryptor;
import com.github.kilel.jotter.log.LogManager;
import com.github.kilel.jotter.msg.LoadResponse;
import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Synchronization command.
 */
public class SynchCommand extends Command {
    private final Logger log = LogManager.commonLog();
    private final Lock lock;
    private BigInteger lastSynchId = BigInteger.ZERO;

    public SynchCommand(JotterContext context) {
        super(context);
        this.lock = new ReentrantLock();
    }

    @Override
    public void run() {
        if(Thread.interrupted()) {
            return;
        }

        // Try to lock synchronization.
        // If it is in progress, no need to start new.
        if (!lock.tryLock()) {
            return;
        }

        try {
            log.debug("Starting notes synchronization");

            final LoadResponse response = getContext().getDaoBridge().load(RequestFactory.createLoad(lastSynchId));
            if (isSuccessful(response)  //
                    && response.getLastSynchId() != null //
                    && lastSynchId.compareTo(response.getLastSynchId()) < 0) {
                lastSynchId = response.getLastSynchId();
            }

            response.getEncryptedNoteList().getEncryptedNote().stream() //
                    .forEach((note) -> {
                        final String uniqueId = note.getEncryptorId();
                        final Encryptor encryptor = getContext().getEncryptionContext().get(uniqueId);

                        if (encryptor != null) {
                            getContext().getHolder().create(encryptor.decrypt(note));
                        } else {
                            log.error("Can't find encryptor with ID = " + uniqueId);
                        }
                    });

            log.debug("Notes synchronization finished");
        } finally {
            lock.unlock();
        }
    }
}
