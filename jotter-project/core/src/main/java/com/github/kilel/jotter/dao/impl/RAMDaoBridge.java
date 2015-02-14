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

package com.github.kilel.jotter.dao.impl;

import static com.github.kilel.jotter.common.DaoResultCode.*;
import com.github.kilel.jotter.common.EncryptedNote;
import com.github.kilel.jotter.dao.AbstractDaoBridge;
import com.github.kilel.jotter.msg.LoadRequest;
import com.github.kilel.jotter.msg.UpdateResponse;
import com.github.kilel.jotter.util.NoteUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.kilel.jotter.dao.factory.ResponseFactory.createErrorResponse;
import static com.github.kilel.jotter.dao.factory.ResponseFactory.createUpdateResponse;

/**
 * Stores notes in RAM.
 */
public final class RAMDaoBridge extends AbstractDaoBridge {
    private final Map<Integer, EncryptedNote> notesMap;
    private final Lock updateLock = new ReentrantLock();

    public RAMDaoBridge() {
        this.notesMap = new ConcurrentHashMap<>();
    }

    @Override
    protected List<EncryptedNote> loadBySynchId(LoadRequest request) {
        final List<EncryptedNote> result = new ArrayList<>();
        notesMap.values().stream().forEach((note) -> {
            if (note.getSynchId().compareTo(request.getLastSynchId()) > 0) {
                result.add(note);
            }
        });
        return result;
    }

    @Override
    protected List<EncryptedNote> loadByNoteIdList(LoadRequest request) {
        final List<EncryptedNote> result = new ArrayList<>();
        request.getNoteIdList().getId().stream().forEach((noteId) -> {
            if (notesMap.containsKey(noteId)) {
                result.add(notesMap.get(noteId));
            } else {
                result.add(createArchiveNote(noteId));
            }
        });
        return result;
    }

    @Override
    protected UpdateResponse createNewNote(EncryptedNote source) {
        final EncryptedNote dest = NoteUtils.copy(source);

        while (true) {
            dest.setId(createNewId());
            UpdateResponse response = updateNote(dest, true);
            if (response.getResult() != RACE_CONDITIONS) {
                return response;
            }
        }
    }

    @Override
    protected UpdateResponse updateNote(EncryptedNote source, boolean ensureNew) {
        updateLock.lock();
        try {
            final EncryptedNote dest = NoteUtils.copy(source);
            {
                dest.setIsArchived(false);
                dest.setSynchId(createSynchId());
            }
            if (ensureNew && notesMap.containsKey(dest.getId())) {
                return createErrorResponse(RACE_CONDITIONS, "RC", UpdateResponse.class);
            }
            notesMap.put(source.getId(), source);
            return createUpdateResponse(dest);
        } finally {
            updateLock.unlock();
        }
    }

    @Override
    protected UpdateResponse removeNote(EncryptedNote source) {
        return updateNote(createArchiveNote(source.getId()), false);
    }

    private Integer createNewId() {
        if (notesMap.isEmpty()) {
            return 1;
        }
        return notesMap.values().parallelStream().//
                map((x) -> x.getId()).//
                max((a, b) -> a.compareTo(b)).//
                get() + 1;
    }

}
