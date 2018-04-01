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

import org.bouncycastle.crypto.engines.AESEngine
import org.bouncycastle.crypto.modes.CBCBlockCipher
import org.bouncycastle.crypto.paddings.PKCS7Padding
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Encryptor for BC AES engine.
 * //TODO maybe implement RijndaelEngine
 */
class AesEncryptor(val password: String) {
    companion object {
        const val ENCRYPT = true
        const val DECRYPT = false
        const val BIT_TO_BYTE = 8
    }

    private val keyType = "AES"

    private val engine get() = AESEngine()
    private val ivSize = AESEngine().blockSize
    private val algo get() = "PBEWithSHA256And${keySize}BitAES-CBC-BC"

    var random: (size: Int) -> ByteArray = randomDataBuilder()

    var keyIterations = 1000
    var keySize = 256
    var saltSize = 256

    val saltSizeBytes get() = saltSize / BIT_TO_BYTE

    /**
     * Encrypt data.
     * @param data Source data.
     * @return Encrypted data.
     */
    fun encrypt(data: ByteArray): ByteArray {
        val salt = random(saltSizeBytes)
        val iv = random(ivSize)

        val cipher = buildCipher(ENCRYPT, salt, iv)
        val bufferWithPadding = doProcessEnc(cipher, data)

        return EncryptedData(salt, iv, bufferWithPadding).full
    }

    /**
     * Decrypt data.
     * @param data Source (encrypted) data.
     * @return Clear data (or exception)
     */
    fun decrypt(data: ByteArray): ByteArray {
        val encryptedData = EncryptedData(this, data)

        val cipher = buildCipher(DECRYPT, encryptedData.salt, encryptedData.iv)
        return doProcessEnc(cipher, encryptedData.data)
    }

    private fun getKey(salt: ByteArray): ByteArray {
        val pbeKeySpec = PBEKeySpec(password.toCharArray(), salt, keyIterations, keySize)
        val keyFactory = SecretKeyFactory.getInstance(algo)
        val secretKeySpec = SecretKeySpec(keyFactory.generateSecret(pbeKeySpec).encoded, keyType)
        return secretKeySpec.encoded
    }

    private fun buildCipher(type: Boolean, salt: ByteArray, iv: ByteArray): PaddedBufferedBlockCipher {
        val params = ParametersWithIV(KeyParameter(getKey(salt)), iv)
        val cipher = PaddedBufferedBlockCipher(CBCBlockCipher(engine), PKCS7Padding())

        cipher.reset()
        cipher.init(type, params)
        return cipher
    }

    private fun doProcessEnc(cipher: PaddedBufferedBlockCipher, data: ByteArray): ByteArray {
        val bufferWithPadding = ByteArray(cipher.getOutputSize(data.size))
        var length = cipher.processBytes(data, 0, data.size, bufferWithPadding, 0)
        length += cipher.doFinal(bufferWithPadding, length)

        if (length == bufferWithPadding.size) {
            return bufferWithPadding
        }

        val resultWithoutPadding = ByteArray(length)
        System.arraycopy(bufferWithPadding, 0, resultWithoutPadding, 0, length)
        return resultWithoutPadding
    }

    class EncryptedData(val salt: ByteArray,
                        val iv: ByteArray,
                        val data: ByteArray) {

        val full get() = aggregate(salt, iv, data)

        constructor(enc: AesEncryptor, encryptedData: ByteArray) : this(
                encryptedData.copyOfRange(0, enc.saltSizeBytes),
                encryptedData.copyOfRange(enc.saltSizeBytes, enc.saltSizeBytes + enc.ivSize),
                encryptedData.copyOfRange(enc.saltSizeBytes + enc.ivSize, encryptedData.size)
        )
    }
}
