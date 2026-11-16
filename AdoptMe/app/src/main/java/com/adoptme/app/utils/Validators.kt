package com.adoptme.app.utils

object ValidationMessages {
    const val NAME_REQUIRED = "Укажите имя."
    const val EMAIL_REQUIRED = "Укажите электронную почту."
    const val EMAIL_INVALID = "Введите корректную электронную почту."
    const val PASSWORD_REQUIRED = "Введите пароль."
    const val PASSWORD_SHORT = "Пароль должен содержать не менее 6 символов."
    const val PASSWORD_MISMATCH = "Пароли не совпадают."
    const val PHONE_REQUIRED = "Укажите номер телефона."
    const val PHONE_INVALID = "Введите корректный номер телефона."
    const val ADDRESS_REQUIRED = "Укажите адрес проживания."
    const val EXPERIENCE_REQUIRED = "Расскажите об опыте содержания животных."
    const val REASON_REQUIRED = "Укажите причину усыновления."
    const val LOGIN_FAILED = "Неверная электронная почта или пароль."
    const val EMAIL_TAKEN = "Пользователь с такой почтой уже зарегистрирован."
    const val FIELDS_REQUIRED = "Заполните все обязательные поля."
}

object AuthValidator {
    private val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validateRegistration(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): String? {
        if (name.trim().length < 2) return ValidationMessages.NAME_REQUIRED
        validateEmail(email)?.let { return it }
        if (password.isEmpty()) return ValidationMessages.PASSWORD_REQUIRED
        if (password.length < 6) return ValidationMessages.PASSWORD_SHORT
        if (password != confirmPassword) return ValidationMessages.PASSWORD_MISMATCH
        return null
    }

    fun validateLogin(email: String, password: String): String? {
        validateEmail(email)?.let { return it }
        if (password.isEmpty()) return ValidationMessages.PASSWORD_REQUIRED
        return null
    }

    fun validateEmail(email: String): String? {
        val value = email.trim()
        if (value.isEmpty()) return ValidationMessages.EMAIL_REQUIRED
        if (!emailPattern.matches(value)) return ValidationMessages.EMAIL_INVALID
        return null
    }
}

object AdoptionFormValidator {
    fun validate(
        fullName: String,
        phone: String,
        email: String,
        address: String,
        experience: String,
        reason: String
    ): String? {
        if (fullName.trim().length < 2) return ValidationMessages.NAME_REQUIRED
        validatePhone(phone)?.let { return it }
        AuthValidator.validateEmail(email)?.let { return it }
        if (address.trim().length < 5) return ValidationMessages.ADDRESS_REQUIRED
        if (experience.trim().isEmpty()) return ValidationMessages.EXPERIENCE_REQUIRED
        if (reason.trim().isEmpty()) return ValidationMessages.REASON_REQUIRED
        return null
    }

    fun validatePhone(phone: String): String? {
        val value = phone.trim()
        if (value.isEmpty()) return ValidationMessages.PHONE_REQUIRED
        val digits = value.count { it.isDigit() }
        val allowed = value.all { it.isDigit() || it == '+' || it == '-' || it == ' ' || it == '(' || it == ')' }
        if (!allowed || digits !in 10..15) return ValidationMessages.PHONE_INVALID
        return null
    }
}
