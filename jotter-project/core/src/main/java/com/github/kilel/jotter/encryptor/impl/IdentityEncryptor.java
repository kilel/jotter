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

package com.github.kilel.jotter.encryptor.impl;

import com.github.kilel.jotter.encryptor.AbstractEncryptor;

import java.io.UnsupportedEncodingException;

/**
 * Do not encrypts notes.
 */
public class IdentityEncryptor extends AbstractEncryptor {

    public IdentityEncryptor() {
        super("");
    }

    @Override
    protected byte[] doEncrypt(String source) {
        try {
            return source.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unexpected exception");
        }
    }


    @Override
    protected String doDecrypt(byte[] source) {
        try {
            return new String(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unexpected exception");
        }
    }
}
