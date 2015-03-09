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

package com.github.kilel.jotter.ui;

/**
 * Parameters for UI Commands
 */
public interface UICommandParams {
    /**
     * If required help for operation.
     *
     * @return true, if required help for operation.
     */
    boolean isHelp();

    public static interface Get extends UICommandParams {
        /**
         * Category to select note.
         *
         * @return Category to select note.
         */
        String getCategory();

        /**
         * Name to select note.
         *
         * @return Name to select note.
         */
        String getName();
    }

    public static interface Add extends Get {
        /**
         * Encryptor ID for this note.
         *
         * @return Encryptor ID for this note.
         */
        String getEncryptor();

        /**
         * Note raw value.
         *
         * @return Note raw value.
         */
        String getValue();

        /**
         * Note encrypted value.
         *
         * @return Note encrypted value.
         */
        String getEncValue();
    }

    public static interface Update extends Add {
        /**
         * Target category (if changed).
         *
         * @return Target category (if changed).
         */
        String getTgtCategory();

        /**
         * Target name (if changed).
         *
         * @return Target name (if changed).
         */
        String getTgtName();

    }

    public static interface Remove extends Get {
    }

    public static interface List extends UICommandParams {
    }

    public static interface Stop extends UICommandParams {
    }

}
