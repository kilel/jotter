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

import com.github.kilel.jotter.common.DaoResultCode;
import com.github.kilel.jotter.common.EncryptedNote;
import com.github.kilel.jotter.dao.DaoBridge;
import com.github.kilel.jotter.dao.factory.ResponseFactory;
import com.github.kilel.jotter.msg.LoadRequest;
import com.github.kilel.jotter.msg.LoadResponse;
import com.github.kilel.jotter.msg.UpdateRequest;
import com.github.kilel.jotter.msg.UpdateResponse;
import com.github.kilel.jotter.util.NoteUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores notes in RAM.
 */
public class RAMDaoBridge implements DaoBridge {
    private final Map<Integer, EncryptedNote> notesMap;

    public RAMDaoBridge() {
        this.notesMap = new ConcurrentHashMap<>();
    }

    @Override
    public LoadResponse load(LoadRequest request) {
        final List<EncryptedNote> resultList = new ArrayList<>();
        BigInteger resultSynchId = request.getLastSynchId();

        if (request.getLastSynchId() != null) {
            loadBySynchId(request, resultList);
            resultSynchId = getMaxSynchId(resultList);
        } else if (request.getNoteIdList() != null) {
            loadByNoteIdList(request, resultList);
        }

        return ResponseFactory.createLoadResponse(resultList, resultSynchId);
    }

    private void loadBySynchId(LoadRequest request, List<EncryptedNote> resultList) {
        notesMap.values().stream().forEach((note) -> {
            if (note.getSynchId().compareTo(request.getLastSynchId()) > 0) {
                resultList.add(note);
            }
        });
    }

    private void loadByNoteIdList(LoadRequest request, List<EncryptedNote> resultList) {
        request.getNoteIdList().getId().stream().forEach((noteId) -> {
            if (notesMap.containsKey(noteId)) {
                resultList.add(notesMap.get(noteId));
            }
        });
    }

    @Override
    public UpdateResponse update(UpdateRequest request) {
        final List<EncryptedNote> result = new ArrayList<>();
        try {
            request.getEncryptedNoteList().getEncryptedNote().stream().//
                    forEach((note) -> {
                note.setSynchId(createSynchId());
                if (note.getId() == 0) {
                    note.setId(createNewId());
                }
                notesMap.put(note.getId(), note);
            });
            return ResponseFactory.createUpdateResponse();
        } catch (Exception e) {
            return ResponseFactory.createUpdateErrorResponse(DaoResultCode.UNKNOWN_ERROR);
        }
    }

    private BigInteger createSynchId() {
        return new BigInteger(Long.toString(System.nanoTime()));
    }

    private BigInteger getMaxSynchId(Collection<EncryptedNote> resultList) {
        if(resultList.size() > 0) {
            return resultList.stream().map((note) -> note.getSynchId())//
                    .max(new NoteUtils.BigIntegerComparator()).get();
        } else {
            return BigInteger.ZERO;
        }
    }

    private Integer createNewId() {
        if(notesMap.size() > 0) {
            return notesMap.values().stream().map((x) -> x.getId()).max((a, b) -> a.compareTo(b)).get() + 1;
        } else {
            return 1;
        }
    }

}
