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

package com.github.kilel.jotter.encryptor;

import com.github.kilel.jotter.common.EncryptedNote;
import com.github.kilel.jotter.common.Note;

/**
 * Responsible for note encryption.
 */
public interface Encryptor {

    /**
     * Encrypts note.
     * @param source Source note.
     * @return Encrypted note.
     */
    EncryptedNote encrypt(Note source);

    /**
     * Decrypts note.
     * @param source Source encrypted note.
     * @return Decrypted note.
     */
    Note decrypt(EncryptedNote source);

    /**
     * Unique encryptor identifier.
     * @return Identifier.
     */
    String getUniqueId();

}
