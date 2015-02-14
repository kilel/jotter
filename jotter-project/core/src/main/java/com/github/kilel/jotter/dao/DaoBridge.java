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

package com.github.kilel.jotter.dao;


import com.github.kilel.jotter.msg.LoadRequest;
import com.github.kilel.jotter.msg.LoadResponse;
import com.github.kilel.jotter.msg.UpdateRequest;
import com.github.kilel.jotter.msg.UpdateResponse;

/**
 * Provides access to notes.
 */
public interface DaoBridge {
    Integer ID_TO_CREATE_NEW_NOTE = 0;

    /**
     * Loads notes.
     *
     * @param request Request to load notes.
     * @return Load notes response.
     */
    LoadResponse load(LoadRequest request);

    /**
     * Creates or updates note(s).
     *
     * @param request Request to update note(s).
     * @return Update response.
     */
    UpdateResponse update(UpdateRequest request);

}
