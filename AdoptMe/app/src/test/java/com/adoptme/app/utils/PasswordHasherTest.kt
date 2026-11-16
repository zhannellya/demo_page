package com.adoptme.app.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {
    @Test
    fun hashDoesNotKeepThePasswordAndVerifiesIt() {
        val salt = PasswordHasher.newSalt()
        val hash = PasswordHasher.hash("Admin12345", salt)
        assertNotEquals("Admin12345", hash)
        assertFalse(hash.contains("Admin12345"))
        assertTrue(PasswordHasher.verify("Admin12345", salt, hash))
        assertFalse(PasswordHasher.verify("Admin12346", salt, hash))
    }

    @Test
    fun differentSaltsProduceDifferentHashes() {
        val first = PasswordHasher.newSalt()
        val second = PasswordHasher.newSalt()
        assertNotEquals(
            PasswordHasher.hash("secret-pass", first),
            PasswordHasher.hash("secret-pass", second)
        )
    }
}
