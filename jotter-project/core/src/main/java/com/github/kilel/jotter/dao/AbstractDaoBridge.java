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

package com.github.kilel.jotter.dao;

import com.github.kilel.jotter.common.DaoResultCode;
import com.github.kilel.jotter.common.EncryptedNote;
import com.github.kilel.jotter.msg.LoadRequest;
import com.github.kilel.jotter.msg.LoadResponse;
import com.github.kilel.jotter.msg.UpdateRequest;
import com.github.kilel.jotter.msg.UpdateResponse;
import com.github.kilel.jotter.util.NoteUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.github.kilel.jotter.dao.factory.ResponseFactory.createErrorResponse;
import static com.github.kilel.jotter.dao.factory.ResponseFactory.createLoadResponse;

/**
 * Abstract implementation of dao bridge.
 */
public abstract class AbstractDaoBridge implements DaoBridge {

    @Override
    public final LoadResponse load(LoadRequest request) {
        try {
            final List<EncryptedNote> resultList;
            BigInteger resultSynchId = request.getLastSynchId();

            if (request.getLastSynchId() != null) {
                resultList = loadBySynchId(request);
                resultSynchId = getMaxSynchId(resultList);
            } else if (request.getNoteIdList() != null) {
                resultList = loadByNoteIdList(request);
            } else {
                resultList = Collections.emptyList();
            }

            return createLoadResponse(resultList, resultSynchId);
        } catch (Exception e) {
            return createErrorResponse(DaoResultCode.UNKNOWN_ERROR, e.getMessage(), LoadResponse.class);
        }
    }

    @Override
    public UpdateResponse update(UpdateRequest request) {
        final List<EncryptedNote> result = new ArrayList<>();
        try {
            final EncryptedNote note = request.getEncryptedNote();

            if (note.isIsArchived()) {
                return removeNote(note);
            } else if (note.getId() == ID_TO_CREATE_NEW_NOTE) {
                return createNewNote(note);
            } else {
                return updateNote(note, false);
            }
        } catch (Exception e) {
            return createErrorResponse(DaoResultCode.UNKNOWN_ERROR, e.getMessage(), UpdateResponse.class);
        }
    }

    protected final BigInteger createSynchId() {
        return new BigInteger(Long.toString(System.nanoTime()));
    }

    protected final EncryptedNote createArchiveNote(Integer id) {
        final EncryptedNote note = new EncryptedNote();
        {
            note.setId(id);
            note.setSynchId(createSynchId());
            note.setEncryptorId("");
            note.setValue(new byte[0]);
        }
        return note;
    }

    protected final BigInteger getMaxSynchId(Collection<EncryptedNote> resultList) {
        if (resultList.isEmpty()) {
            return BigInteger.ZERO;
        }
        return resultList.parallelStream().map((note) -> note.getSynchId()) //
                .max(new NoteUtils.BigIntegerComparator()).get();
    }

    /**
     * Loads notes by synch ID.
     *
     * @param request Request.
     * @return Encrypted notes.
     */
    protected abstract List<EncryptedNote> loadBySynchId(LoadRequest request);

    /**
     * Loads notes by ID list.
     * If ID was not found, fills with empty archived note.
     *
     * @param request Request.
     * @return Encrypted notes.
     */
    protected abstract List<EncryptedNote> loadByNoteIdList(LoadRequest request);

    protected abstract UpdateResponse createNewNote(EncryptedNote source);

    protected abstract UpdateResponse updateNote(EncryptedNote source, boolean ensureNew);

    protected abstract UpdateResponse removeNote(EncryptedNote source);
}
