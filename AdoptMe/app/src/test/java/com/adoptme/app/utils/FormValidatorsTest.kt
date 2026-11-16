package com.adoptme.app.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FormValidatorsTest {
    @Test
    fun loginRejectsBlankAndBadEmail() {
        assertEquals("Введите email.", FormValidators.validateLogin("", "secret1"))
        assertEquals("Введите корректный email.", FormValidators.validateLogin("not-an-email", "secret1"))
        assertEquals("Пароль должен быть не короче 6 символов.", FormValidators.validateLogin("a@b.co", "123"))
        assertNull(FormValidators.validateLogin("a@b.co", "123456"))
    }

    @Test
    fun registerChecksNamePasswordAndConfirmation() {
        assertTrue(FormValidators.validateRegister("А", "a@b.co", "123456", "123456")!!.contains("Имя"))
        assertEquals(
            "Пароли не совпадают.",
            FormValidators.validateRegister("Анна", "anna@adoptme.app", "123456", "123457")
        )
        assertNull(FormValidators.validateRegister("Анна", "anna@adoptme.app", "123456", "123456"))
    }

    @Test
    fun applicationRequiresEveryField() {
        assertTrue(FormValidators.validateApplication("", "9000000000", "a@b.co", "улица 1", "есть", "хочу забрать домой")!!.contains("имя"))
        assertTrue(FormValidators.validateApplication("Анна", "123", "a@b.co", "улица 1", "есть", "хочу забрать домой")!!.contains("телефон"))
        assertTrue(FormValidators.validateApplication("Анна", "9000000000", "bad", "улица 1", "есть", "хочу забрать домой")!!.contains("email"))
        assertTrue(FormValidators.validateApplication("Анна", "+7 900 000-00-00", "a@b.co", "дом", "есть", "хочу забрать домой")!!.contains("адрес"))
        assertTrue(FormValidators.validateApplication("Анна", "+7 900 000-00-00", "a@b.co", "улица Ленина 1", "", "хочу забрать домой")!!.contains("опыт"))
        assertTrue(FormValidators.validateApplication("Анна", "+7 900 000-00-00", "a@b.co", "улица Ленина 1", "Кошка была", "коротко")!!.contains("причин"))
        assertNull(
            FormValidators.validateApplication(
                "Анна",
                "+7 900 000-00-00",
                "anna@adoptme.app",
                "улица Ленина 1",
                "Раньше жила кошка",
                "Хочу забрать Шарика и гулять с ним каждый день"
            )
        )
    }
}
