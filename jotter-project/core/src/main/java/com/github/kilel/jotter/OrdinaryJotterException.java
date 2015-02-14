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

package com.github.kilel.jotter;

import com.github.kilel.jotter.common.AbstractResponse;
import com.github.kilel.jotter.common.DaoResultCode;

/**
 * Ordinary Jotter exception.
 */
public class OrdinaryJotterException extends RuntimeException {
    private final DaoResultCode resultCode;
    private final String description;

    public  OrdinaryJotterException(AbstractResponse response) {
        this(response.getResult(), response.getDescription());
    }

    public  OrdinaryJotterException(DaoResultCode code, String message) {
        this(code, message, null);
    }

    public  OrdinaryJotterException(DaoResultCode code, String message, Throwable e) {
        super(String.format("%s: %s", code, message), e);
        this.resultCode = code;
        this.description = message;
    }

    public DaoResultCode getResultCode() {
        return resultCode;
    }

    public String getDescription() {
        return description;
    }

}
