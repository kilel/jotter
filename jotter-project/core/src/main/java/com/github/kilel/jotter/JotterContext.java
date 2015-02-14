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

import com.github.kilel.jotter.dao.DaoBridge;
import com.github.kilel.jotter.dao.factory.DaoBridgeFactory;
import com.github.kilel.jotter.encryptor.EncryptionContext;
import com.github.kilel.jotter.encryptor.impl.EncryptionContextImpl;
import com.github.kilel.jotter.log.LogManager;
import com.github.kilel.jotter.util.NotesHolder;
import com.github.kilel.jotter.util.NotesHolderImpl;
import org.apache.log4j.Logger;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstract command factory.
 */
public class JotterContext {
    private final Logger log = LogManager.commonLog();
    private final DaoBridge daoBridge;
    private final NotesHolder holder;
    private final EncryptionContext encryptionContext;

    public JotterContext(String daoType) {
        this.daoBridge = new DaoBridgeFactory().create(daoType);
        this.holder = new NotesHolderImpl();
        this.encryptionContext = new EncryptionContextImpl();
    }

    public DaoBridge getDaoBridge() {
        return daoBridge;
    }

    public NotesHolder getHolder() {
        return holder;
    }

    public EncryptionContext getEncryptionContext() {
        return encryptionContext;
    }
}
