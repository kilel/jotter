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

import org.github.snt.lib.util.BinaryFormat.*
import org.github.snt.lib.util.HashType.*
import java.security.MessageDigest
import java.util.*

fun ByteArray?.equalsNullableBA(data: ByteArray?): Boolean {
    return (this == null && data == null //
            || this != null && data != null && MessageDigest.isEqual(this, data))
}

fun Any?.equalsNullable(other: Any?): Boolean {
    return Objects.equals(this, other)
}

fun md5(vararg sources: ByteArray): ByteArray {
    return MD5.hash(*sources)
}

fun sha1(vararg sources: ByteArray): ByteArray {
    return SHA_1.hash(*sources)
}

fun sha256(vararg sources: ByteArray): ByteArray {
    return SHA_256.hash(*sources)
}

fun sha512(vararg sources: ByteArray): ByteArray {
    return SHA_512.hash(*sources)
}

fun sha3(vararg sources: ByteArray): ByteArray {
    return SHA_3.hash(*sources)
}

fun ByteArray.md5(): ByteArray {
    return md5(this)
}

fun ByteArray.sha1(): ByteArray {
    return sha1(this)
}

fun ByteArray.sha256(): ByteArray {
    return sha256(this)
}

fun ByteArray.sha512(): ByteArray {
    return sha512(this)
}

fun ByteArray.sha3(): ByteArray {
    return sha3(this)
}

fun ByteArray.bin(): String {
    return BASE_2.format(this)
}

fun String.fromBin(): ByteArray {
    return BASE_2.parse(this)
}

fun ByteArray.oct(): String {
    return BASE_8.format(this)
}

fun String.fromOct(): ByteArray {
    return BASE_8.parse(this)
}

fun ByteArray.dec(): String {
    return BASE_10.format(this)
}

fun String.fromDec(): ByteArray {
    return BASE_10.parse(this)
}

fun ByteArray.hex(): String {
    return BASE_16.format(this)
}

fun String.fromHex(): ByteArray {
    return BASE_16.parse(this)
}

fun ByteArray.base32(): String {
    return BASE_32.format(this)
}

fun String.fromBase32(): ByteArray {
    return BASE_32.parse(this)
}

fun ByteArray.base32Rfc(): String {
    return BASE_32_RFC.format(this)
}

fun String.fromBase32Rfc(): ByteArray {
    return BASE_32_RFC.parse(this)
}

fun ByteArray.base64(): String {
    return BASE_64.format(this)
}

fun String.fromBase64(): ByteArray {
    return BASE_64.parse(this)
}


