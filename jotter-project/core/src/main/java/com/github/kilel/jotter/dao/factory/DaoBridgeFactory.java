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

import com.github.kilel.jotter.dao.DaoBridge;
import com.github.kilel.jotter.dao.impl.FileDaoBridge;
import com.github.kilel.jotter.dao.impl.RAMDaoBridge;

/**
 * Creates DAO bridge.
 */
public class DaoBridgeFactory {

    public DaoBridge create(String type) {
        switch(type){
            case "mem":
                return new RAMDaoBridge();
            case "file":
                return new FileDaoBridge();
        }

        throw new IllegalArgumentException(String.format("No DAO bridge with type %s supported", type));
    }
}
