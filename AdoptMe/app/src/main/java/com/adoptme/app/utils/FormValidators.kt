package com.adoptme.app.utils

object FormValidators {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validateEmail(email: String): String? {
        val value = email.trim()
        if (value.isEmpty()) return "Введите email."
        if (!emailRegex.matches(value)) return "Введите корректный email."
        return null
    }

    fun validatePassword(password: String): String? {
        if (password.isEmpty()) return "Введите пароль."
        if (password.length < 6) return "Пароль должен быть не короче 6 символов."
        return null
    }

    fun validateLogin(email: String, password: String): String? {
        return validateEmail(email) ?: validatePassword(password)
    }

    fun validateRegister(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): String? {
        if (name.trim().length < 2) return "Имя пользователя должно быть не короче 2 символов."
        validateEmail(email)?.let { return it }
        validatePassword(password)?.let { return it }
        if (password != confirmPassword) return "Пароли не совпадают."
        return null
    }

    fun validateApplication(
        name: String,
        phone: String,
        email: String,
        address: String,
        experience: String,
        reason: String
    ): String? {
        if (name.trim().length < 2) return "Укажите имя заявителя."
        val digits = phone.filter { it.isDigit() }
        if (digits.length < 10) return "Укажите телефон не короче 10 цифр."
        validateEmail(email)?.let { return it }
        if (address.trim().length < 5) return "Укажите адрес проживания."
        if (experience.trim().isEmpty()) return "Расскажите об опыте содержания животных."
        if (reason.trim().length < 10) return "Опишите причину усыновления подробнее."
        return null
    }
}
