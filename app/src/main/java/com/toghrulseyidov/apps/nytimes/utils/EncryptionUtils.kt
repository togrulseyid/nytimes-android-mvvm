package com.toghrulseyidov.apps.nytimes.utils

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


open class EncryptionUtils {
    @Throws(Exception::class)
    open fun encrypt(raw: ByteArray, clear: ByteArray): ByteArray? {
        val secretKeySpec = SecretKeySpec(raw, "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        return cipher.doFinal(clear)
    }

    @Throws(Exception::class)
    open fun decrypt(raw: ByteArray, encrypted: ByteArray): ByteArray? {
        val secretKeySpec = SecretKeySpec(raw, "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        return cipher.doFinal(encrypted)
    }
}
