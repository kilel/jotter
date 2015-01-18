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

package com.github.kilel.jotter.gui;

import com.github.kilel.jotter.dao.DaoBridge;
import com.github.kilel.jotter.dao.factory.DaoBridgeFactory;
import com.github.kilel.jotter.log.LogManager;

/**
 * Common GUI facade.
 */
public abstract class JotterGui {
    private final DaoBridge daoBridge;

    public JotterGui() {
        LogManager.init();
        daoBridge = new DaoBridgeFactory().create("config"); // TODO implement configuration
    }

    public abstract void start();

    public abstract void stop();
}
