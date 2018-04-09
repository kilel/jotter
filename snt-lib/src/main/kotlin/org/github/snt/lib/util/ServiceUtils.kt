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
import org.github.snt.lib.util.BinaryFormat.*
import org.github.snt.lib.util.HashType.*
import org.jasypt.digest.StandardByteDigester
import org.jasypt.digest.StandardStringDigester
import org.jasypt.salt.SaltGenerator
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import java.util.stream.Collectors

fun buildAesEncryptor(data: String, config: SntCoreConfig, saltGenerator: SaltGenerator): AesEncryptor {
    val encryptor = AesEncryptor(data.trim())
    encryptor.random = fun(size: Int) = saltGenerator.generateSalt(size)
    encryptor.keySize = config.encryption.keySize
    encryptor.keyIterations = config.encryption.iterations
    encryptor.saltSize = config.encryption.saltSize
    return encryptor
}

fun buildJasyptPwdDigister(config: SntCoreConfig, saltGenerator: SaltGenerator): StandardStringDigester {
    val result = StandardStringDigester()
    result.setAlgorithm(config.digest.hashAlgo)
    result.setIterations(config.digest.iterations)
    result.setSaltSizeBytes(config.digest.saltSize)
    result.setSaltGenerator(saltGenerator)
    result.setProviderName(BouncyCastleProvider.PROVIDER_NAME)
    result.initialize()
    return result
}

fun buildJasyptBinDigister(config: SntCoreConfig, saltGenerator: SaltGenerator): StandardByteDigester {
    val result = StandardByteDigester()
    result.setAlgorithm(config.digest.hashAlgo)
    result.setIterations(config.digest.iterations)
    result.setSaltSizeBytes(config.digest.saltSize)
    result.setSaltGenerator(saltGenerator)
    result.setProviderName(BouncyCastleProvider.PROVIDER_NAME)
    result.initialize()
    return result
}

fun aggregate(vararg sources: ByteArray): ByteArray {
    val sourcesList = Arrays.stream(sources)//
            .filter { it != null && it.isNotEmpty() }//
            .collect(Collectors.toList())

    val result = ByteArray(sourcesList.stream().collect(Collectors.summingInt { it.size }))
    var shift = 0
    sourcesList.forEach {
        System.arraycopy(it, 0, result, shift, it.size)
        shift += it.size
    }

    return result
}

fun randomDataBuilder(random: Random = SecureRandom()): (size: Int) -> ByteArray {
    return fun(size: Int): ByteArray {
        val result = ByteArray(size)
        random.nextBytes(result)
        return result
    }
}

fun ByteArray?.equalsNullableBA(data: ByteArray?): Boolean {
    return (this == null && data == null //
            || this != null && data != null && MessageDigest.isEqual(this, data))
}

fun String.getBytes(): ByteArray {
    return this.toByteArray(charset = Charset.forName("UTF-8"))
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

fun Throwable.extractStackTrace(): String {
    val writer = StringWriter()
    this.printStackTrace(PrintWriter(writer))
    return writer.toString()
}


