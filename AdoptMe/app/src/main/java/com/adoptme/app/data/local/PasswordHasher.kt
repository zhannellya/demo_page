package com.adoptme.app.data.local

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Local demo hashing only. A production app should send the password over HTTPS
 * and hash it on the server (Argon2id or bcrypt). The device should keep a
 * short-lived token, never the password and never this hash as the source of truth.
 */
object PasswordHasher {
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val ITERATIONS = 20_000
    private const val KEY_BITS = 256
    private const val SALT_BYTES = 16

    fun newSalt(): String {
        val salt = ByteArray(SALT_BYTES)
        SecureRandom().nextBytes(salt)
        return Base64.getEncoder().withoutPadding().encodeToString(salt)
    }

    fun hash(password: String, salt: String): String {
        val spec = PBEKeySpec(
            password.toCharArray(),
            Base64.getDecoder().decode(salt),
            ITERATIONS,
            KEY_BITS
        )
        val key = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).encoded
        spec.clearPassword()
        return Base64.getEncoder().withoutPadding().encodeToString(key)
    }

    fun verify(password: String, salt: String, expectedHash: String): Boolean {
        val actual = hash(password, salt)
        return actual.length == expectedHash.length && slowlyEqual(actual, expectedHash)
    }

    private fun slowlyEqual(left: String, right: String): Boolean {
        var diff = 0
        for (index in left.indices) {
            diff = diff or (left[index].code xor right[index].code)
        }
        return diff == 0
    }
}
