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

import com.github.kilel.jotter.common.DaoResultCode;
import com.github.kilel.jotter.common.EncryptedNote;
import com.github.kilel.jotter.common.EncryptedNoteList;
import com.github.kilel.jotter.msg.LoadResponse;
import com.github.kilel.jotter.msg.UpdateRequest;
import com.github.kilel.jotter.msg.UpdateResponse;

import java.math.BigInteger;
import java.util.List;

/**
 * DAO Response factory.
 */
public class ResponseFactory {
    public static final LoadResponse createLoadResponse(List<EncryptedNote> notes, BigInteger newLastSynchId) {
        final EncryptedNoteList result = new EncryptedNoteList();
        result.getEncryptedNote().addAll(notes);

        final LoadResponse response = new LoadResponse();
        response.setEncryptedNoteList(result);
        response.setLastSynchId(newLastSynchId);

        response.setResult(DaoResultCode.OK);
        return response;
    }

    public static final LoadResponse createLoadErrorResponse(DaoResultCode code) {
        final LoadResponse response = new LoadResponse();
        response.setResult(code);
        return response;
    }

    public static final UpdateResponse createUpdateResponse() {
        final UpdateResponse response = new UpdateResponse();
        response.setResult(DaoResultCode.OK);
        return response;
    }

    public static final UpdateResponse createUpdateErrorResponse(DaoResultCode code) {
        final UpdateResponse response = new UpdateResponse();
        response.setResult(code);
        return response;
    }
}
