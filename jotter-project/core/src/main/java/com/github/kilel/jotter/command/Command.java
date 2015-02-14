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

package com.github.kilel.jotter.command;

import com.github.kilel.jotter.JotterContext;
import com.github.kilel.jotter.common.AbstractResponse;
import com.github.kilel.jotter.common.DaoResultCode;
import com.github.kilel.jotter.dao.factory.RequestFactory;

/**
 * Describes DAO command.
 */
public abstract class Command implements Runnable {
    private final RequestFactory requestFactory = new RequestFactory();
    private final JotterContext context;

    public Command(final JotterContext context) {
        this.context= context;
    }

    public RequestFactory getRequestFactory() {
        return requestFactory;
    }

    public JotterContext getContext() {
        return context;
    }

    protected final boolean isSuccessful(AbstractResponse response) {
        return response != null
                && response.getResult() != null
                && response.getResult() == DaoResultCode.OK;
    }
}
