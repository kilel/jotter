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

import com.github.kilel.jotter.common.AbstractResponse;
import com.github.kilel.jotter.common.DaoResultCode;
import com.github.kilel.jotter.common.EncryptedNote;
import com.github.kilel.jotter.common.EncryptedNoteList;
import com.github.kilel.jotter.log.LogManager;
import com.github.kilel.jotter.msg.LoadResponse;
import com.github.kilel.jotter.msg.UpdateResponse;

import java.math.BigInteger;
import java.util.List;

/**
 * DAO Response factory.
 */
public class ResponseFactory {
    public static final LoadResponse createLoadResponse(List<EncryptedNote> notes, BigInteger newLastSynchId) {
        final LoadResponse response = new LoadResponse();
        {
            final EncryptedNoteList result = new EncryptedNoteList();
            result.getEncryptedNote().addAll(notes);

            response.setEncryptedNoteList(result);
            response.setLastSynchId(newLastSynchId);
            response.setResult(DaoResultCode.OK);
        }
        return response;
    }

    public static final UpdateResponse createUpdateResponse(EncryptedNote note) {
        final UpdateResponse response = new UpdateResponse();
        {
            response.setEncryptedNote(note);
            response.setResult(DaoResultCode.OK);
        }
        return response;
    }

    /**
     * Creates error response of any type.
     *
     * @param code        Result code.
     * @param description Error description.
     * @param type        Response type.
     * @param <T>         Class of response element.
     * @return Response with error result.
     */
    public static <T extends AbstractResponse> T createErrorResponse(
            DaoResultCode code, String description, Class<T> type) {
        try {
            final T response = type.newInstance();
            {
                response.setResult(code);
                response.setDescription(description);
            }
            return response;
        } catch (Exception e) {
            LogManager.commonLog().error(String.format("Failed to create response of type %s", type), e);
            return null;
        }
    }
}
