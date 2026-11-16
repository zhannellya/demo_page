package com.adoptme.app

import com.adoptme.app.data.local.PasswordHasher
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {
    @Test
    fun verifiesMatchingPasswordAndRejectsAnother() {
        val salt = PasswordHasher.newSalt()
        val hash = PasswordHasher.hash("secret1", salt)
        assertTrue(PasswordHasher.verify("secret1", salt, hash))
        assertFalse(PasswordHasher.verify("secret2", salt, hash))
        assertFalse(hash.contains("secret1"))
    }

    @Test
    fun samePasswordWithDifferentSaltsProducesDifferentHashes() {
        val first = PasswordHasher.hash("secret1", PasswordHasher.newSalt())
        val second = PasswordHasher.hash("secret1", PasswordHasher.newSalt())
        assertNotEquals(first, second)
    }
}
