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

package com.github.kilel.jotter.log;


import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Creates logs for Jotter application.
 */
public class LogManager {
    private static final String COMMON_LOG = "common";
    private static AtomicBoolean isInitialized = new AtomicBoolean(false);

    public static Logger commonLog() {
        assert isInitialized.get();
        return Logger.getLogger(COMMON_LOG);
    }

    public static void init() {
        if (isInitialized.compareAndSet(false, true)) {
            DOMConfigurator.configure(LogManager.class.getClassLoader().getResource("log4j/log4j.xml"));
        }
    }
}
