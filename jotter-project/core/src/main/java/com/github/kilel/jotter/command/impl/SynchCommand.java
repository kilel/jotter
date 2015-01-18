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

import com.github.kilel.jotter.dao.DaoBridge;
import com.github.kilel.jotter.command.Command;
import com.github.kilel.jotter.msg.LoadResponse;

import java.math.BigInteger;
import java.util.function.Consumer;

/**
 * Synchronization command.
 */
public class SynchCommand extends Command {
    private final Consumer<LoadResponse> consumer;

    private BigInteger lastSynchId = null;

    public SynchCommand(DaoBridge daoBridge, Consumer<LoadResponse> consumer) {
        super(daoBridge);
        this.consumer = consumer;
    }

    @Override
    public void run() {
        final LoadResponse response = getDaoBridge().load(getRequestFactory().createLoad(lastSynchId));
        if (isSuccessful(response) && response.getLastSynchId() != null) {
            lastSynchId = response.getLastSynchId();
        }

        consumer.accept(response);
    }
}
