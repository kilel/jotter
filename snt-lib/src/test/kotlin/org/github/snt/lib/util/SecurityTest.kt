/*
 * Copyright 2018 Kislitsyn Ilya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.github.snt.lib.util

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.github.snt.lib.config.SntCoreConfig
import org.jasypt.salt.RandomSaltGenerator
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.security.Security

class SecurityTest {

    private val saltGenerator = RandomSaltGenerator()
    private val config = SntCoreConfig()

    @BeforeEach
    fun init() {
        println("Running encryption initialization")
        Security.addProvider(BouncyCastleProvider())
    }

    @Test
    fun testSimpleEncryption() {
        println("Running encryption test for short password and simple data")
        val sourceData = "Some source data array".getBytes()
        val encryptor = buildEncryptor("password")

        Assert.assertArrayEquals(sourceData, encryptor.decrypt(encryptor.encrypt(sourceData)))
    }

    @Test
    fun testLongPwdEncryption() {
        println("Running encryption test for long password and simple data")
        val sourceData = "Some source data array".getBytes()
        val encryptor = buildEncryptor("password".getBytes().sha3().hex())

        Assert.assertArrayEquals(sourceData, encryptor.decrypt(encryptor.encrypt(sourceData)))
    }

    @Test
    fun testLongDataEncryption() {
        println("Running encryption test for long password and data")
        val sourceData = saltGenerator.generateSalt(4096)
        val encryptor = buildEncryptor("password".getBytes().sha3().hex())

        Assert.assertArrayEquals(sourceData, encryptor.decrypt(encryptor.encrypt(sourceData)))
    }

    @Test
    fun testLongDataEncryptionStrong() {
        println("Running encryption test for long password and data")
        config.encryption.keySize = 256
        val sourceData = saltGenerator.generateSalt(4096)
        val encryptor = buildEncryptor("password".getBytes().sha3().hex())

        Assert.assertArrayEquals(sourceData, encryptor.decrypt(encryptor.encrypt(sourceData)))
    }

    private fun buildEncryptor(data: String): AesEncryptor {
        return buildAesEncryptor(data, config, saltGenerator)
    }

}