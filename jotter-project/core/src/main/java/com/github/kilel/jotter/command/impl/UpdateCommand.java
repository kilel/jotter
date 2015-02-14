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
import com.github.kilel.jotter.OrdinaryJotterException;
import com.github.kilel.jotter.command.Command;
import com.github.kilel.jotter.common.Note;
import com.github.kilel.jotter.dao.DaoBridge;
import com.github.kilel.jotter.encryptor.Encryptor;
import com.github.kilel.jotter.log.LogManager;
import com.github.kilel.jotter.msg.UpdateRequest;
import com.github.kilel.jotter.msg.UpdateResponse;
import com.github.kilel.jotter.util.NoteUtils;
import org.apache.log4j.Logger;

/**
 * Command to update notes.
 */
public class UpdateCommand extends Command {
    private final Logger log = LogManager.commonLog();
    private final ArgsLoader loader;

    public UpdateCommand(JotterContext context, ArgsLoader loader) {
        super(context);
        this.loader = loader;
    }

    @Override
    public void run() {
        final Note source = loader.getNoteToUpdate();
        final Note target = loader.getResultNote();
        final boolean createNew = source == null;
        final boolean remove = target == null;

        if (createNew && remove) {
            log.warn("Trying to create empty note. Rejected.");
            return;
        }

        final Note dest = doGetDest(source, target);
        final Encryptor encryptor = getContext().getEncryptionContext().get(dest.getEncryptorId());

        final UpdateRequest request = getRequestFactory().createUpdate(encryptor.encrypt(dest));
        final UpdateResponse response = getContext().getDaoBridge().update(request);

        doProcessResponse(response, encryptor, source, target);
    }

    private Note doGetDest(Note source, Note target) {
        final boolean createNew = source == null;
        final boolean remove = target == null;
        final Note dest;
        //Create note behavior
        if (createNew) {
            log.debug(String.format("Trying to create note [%s]", NoteUtils.getNotePath(target)));
            dest = target;
            dest.setId(DaoBridge.ID_TO_CREATE_NEW_NOTE);
        } else if (remove) {
            log.debug(String.format("Trying to remove note [%s]", NoteUtils.getNotePath(source)));
            dest = source;
        } else {
            log.debug(String.format("Trying to update note [%s] to [%s]", NoteUtils.getNotePath(source),
                                    NoteUtils.getNotePath(target)));
            dest = target;
        }
        return dest;
    }

    private void doProcessResponse(
            UpdateResponse response, Encryptor encryptor, Note source, Note target) {
        final boolean createNew = source == null;
        final boolean remove = target == null;

        if (!isSuccessful(response)) {
            throw new OrdinaryJotterException(response);
        }

        final Note result = encryptor.decrypt(response.getEncryptedNote());
        if (createNew) {
            getContext().getHolder().create(result);
        } else if (remove) {
            getContext().getHolder().remove(result);
        } else {
            getContext().getHolder().update(source, result);
        }
    }

    public static interface ArgsLoader {
        /**
         * Note, containing source ID.
         * Null to create new note.
         *
         * @return Note.
         */
        Note getNoteToUpdate();

        /**
         * Result note.
         * Null to remove selected note.
         *
         * @return
         */
        Note getResultNote();
    }
}
