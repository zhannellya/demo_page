package com.adoptme.app

import com.adoptme.app.utils.AdoptionFormValidator
import com.adoptme.app.utils.AuthValidator
import com.adoptme.app.utils.ValidationMessages
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FormValidatorTest {

    @Test
    fun registrationAcceptsCompleteForm() {
        assertNull(
            AuthValidator.validateRegistration(
                name = "Анна",
                email = "anna@example.com",
                password = "secret1",
                confirmPassword = "secret1"
            )
        )
    }

    @Test
    fun registrationRequiresNameEmailPasswordAndMatch() {
        assertEquals(
            ValidationMessages.NAME_REQUIRED,
            AuthValidator.validateRegistration(" ", "anna@example.com", "secret1", "secret1")
        )
        assertEquals(
            ValidationMessages.EMAIL_REQUIRED,
            AuthValidator.validateRegistration("Анна", " ", "secret1", "secret1")
        )
        assertEquals(
            ValidationMessages.EMAIL_INVALID,
            AuthValidator.validateRegistration("Анна", "anna", "secret1", "secret1")
        )
        assertEquals(
            ValidationMessages.PASSWORD_SHORT,
            AuthValidator.validateRegistration("Анна", "anna@example.com", "123", "123")
        )
        assertEquals(
            ValidationMessages.PASSWORD_MISMATCH,
            AuthValidator.validateRegistration("Анна", "anna@example.com", "secret1", "secret2")
        )
    }

    @Test
    fun loginRequiresEmailAndPassword() {
        assertEquals(ValidationMessages.EMAIL_INVALID, AuthValidator.validateLogin("not-an-email", "secret1"))
        assertEquals(ValidationMessages.PASSWORD_REQUIRED, AuthValidator.validateLogin("anna@example.com", ""))
        assertNull(AuthValidator.validateLogin("anna@example.com", "secret1"))
    }

    @Test
    fun adoptionFormRequiresEveryField() {
        assertNull(
            AdoptionFormValidator.validate(
                fullName = "Иван Петров",
                phone = "+7 900 000 00 00",
                email = "ivan@example.com",
                address = "Москва, дом 1",
                experience = "Жила кошка",
                reason = "Хочу заботу"
            )
        )
        assertEquals(
            ValidationMessages.PHONE_INVALID,
            AdoptionFormValidator.validate("Иван Петров", "123", "ivan@example.com", "Москва, дом 1", "Опыт", "Причина")
        )
        assertEquals(
            ValidationMessages.ADDRESS_REQUIRED,
            AdoptionFormValidator.validate("Иван Петров", "+7 900 000 00 00", "ivan@example.com", "дом", "Опыт", "Причина")
        )
        assertEquals(
            ValidationMessages.EXPERIENCE_REQUIRED,
            AdoptionFormValidator.validate("Иван Петров", "+7 900 000 00 00", "ivan@example.com", "Москва, дом 1", " ", "Причина")
        )
        assertEquals(
            ValidationMessages.REASON_REQUIRED,
            AdoptionFormValidator.validate("Иван Петров", "+7 900 000 00 00", "ivan@example.com", "Москва, дом 1", "Опыт", " ")
        )
    }
}
