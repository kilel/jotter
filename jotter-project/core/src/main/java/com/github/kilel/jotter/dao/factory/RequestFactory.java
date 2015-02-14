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

package com.github.kilel.jotter.dao.factory;

import com.github.kilel.jotter.common.*;
import com.github.kilel.jotter.msg.LoadRequest;
import com.github.kilel.jotter.msg.UpdateRequest;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

/**
 * Creates requests.
 */
public final class RequestFactory {

    public static LoadRequest createLoad(final BigInteger lastSynchId) {
        final LoadRequest request = createBaseRequest(LoadRequest.class);
        request.setLastSynchId(lastSynchId);
        return request;
    }

    public static LoadRequest createLoad(final List<Integer> noteIds) {
        final LoadRequest request = createBaseRequest(LoadRequest.class);
        final NoteIdList noteIdList = new NoteIdList();
        noteIdList.getId().addAll(noteIds);
        request.setNoteIdList(noteIdList);
        return request;
    }

    public static UpdateRequest createUpdate(final EncryptedNote note) {
        final UpdateRequest request = createBaseRequest(UpdateRequest.class);
        request.setEncryptedNote(note);
        return request;
    }

    private static <T extends AbstractRequest> T createBaseRequest(final Class<T> type) {
        try {
            T request = type.getConstructor().newInstance();
            request.setHead(new RequestHead());
            request.getHead().setRequestId(UUID.randomUUID().toString());
            return request;
        } catch (Exception e) {
            // critical exception, application must shut down.
            throw new RuntimeException("Failed to create DAO request of class " + type, e);
        }
    }
}
