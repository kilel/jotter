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

import org.apache.commons.codec.binary.Base32
import org.apache.commons.lang.StringUtils
import org.bouncycastle.util.BigIntegers
import org.bouncycastle.util.encoders.Base64
import java.math.BigInteger

enum class BinaryFormat(val code: String) {

    BASE_2("bin", 2),
    BASE_8("oct", 8),
    BASE_10("dec", 10),
    BASE_16("hex", 16),
    BASE_32_RFC("base32rfc"),
    BASE_32_HEX("base32hex", 32),
    BASE_32("base32"),
    BASE_64("base64");

    var formatLength = 0

    constructor(code: String, formatLength: Int = 0) : this(code) {
        this.formatLength = formatLength
    }

    fun format(data: ByteArray): String {
        return when (this) {
            BASE_2, BASE_8, BASE_10,
            BASE_16, BASE_32, BASE_32_HEX ->
                BigInteger(1, data).toString(formatLength)
            BASE_32_RFC ->
                StringUtils.strip(Base32().encodeToString(data), "=")
            BASE_64 ->
                Base64.toBase64String(data)
        }
    }

    fun parse(data: String): ByteArray {
        var source = data
        return when (this) {
            BASE_2, BASE_8, BASE_10,
            BASE_16, BASE_32, BASE_32_HEX ->
                return BigIntegers.asUnsignedByteArray(BigInteger(source, formatLength))
            BASE_32_RFC -> {
                source = source.toUpperCase()
                // add padding if none set up
                val padding = 8
                val countToPad = source.length % padding
                if (countToPad != 0) {
                    source = StringUtils.rightPad(source, source.length + countToPad, '=')
                }
                Base32().decode(source)
            }
            BASE_64 -> Base64.decode(source)
        }
    }

    companion object {
        fun getByCode(code: String): BinaryFormat {
            return BinaryFormat.values().find { it.code == code }
                    ?: throw  IllegalArgumentException("Unknown hash type [$code]")
        }

        fun format(format: String, source: ByteArray): String {
            return getByCode(format).format(source)
        }

        fun parse(format: String, source: String): ByteArray {
            return getByCode(format).parse(source)
        }
    }
}