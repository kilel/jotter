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

import org.bouncycastle.crypto.Digest
import org.bouncycastle.crypto.digests.*
import java.util.*

enum class HashType(val code: String, val digestF: () -> Digest) {
    MD5("MD5", { MD5Digest() }),
    SHA_1("SHA-1", { SHA1Digest() }),
    SHA_256("SHA-256", { SHA256Digest() }),
    SHA_512("SHA-512", { SHA512Digest() }),
    SHA_3("SHA-3", { SHA3Digest() }),
    GOST_3411("GOST3411", { GOST3411Digest() }),
    GOST_3411_12_256("GOST3411-2012-256", { GOST3411_2012_256Digest() }),
    GOST_3411_12_512("GOST3411-2012-512", { GOST3411_2012_512Digest() });

    val digest: Digest
        get() = digestF.invoke()

    fun hash(vararg data: ByteArray?): ByteArray {
        val currentDigest = digest

        Arrays.stream(data)//
                .filter { it != null && it.isNotEmpty() }//
                .forEach { currentDigest.update(it, 0, it!!.size) }

        val result = ByteArray(currentDigest.digestSize)
        currentDigest.doFinal(result, 0)
        return result
    }

    companion object {
        fun getByCode(code: String): HashType {
            return values().find { it.code == code } ?: throw  IllegalArgumentException("Unknown hash type [$code]")
        }

        fun hash(type: String, vararg sources: ByteArray): ByteArray {
            return getByCode(type).hash(*sources)
        }
    }
}