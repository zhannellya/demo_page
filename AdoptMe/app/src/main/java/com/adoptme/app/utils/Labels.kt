package com.adoptme.app.utils

import com.adoptme.app.data.model.AgeGroup
import com.adoptme.app.data.model.AnimalStatus
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.PetSize
import com.adoptme.app.data.model.Species
import com.adoptme.app.data.model.UserRole
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Species.label(): String = when (this) {
    Species.CAT -> "Кошка"
    Species.DOG -> "Собака"
    Species.OTHER -> "Другое"
}

fun Gender.label(): String = when (this) {
    Gender.MALE -> "Самец"
    Gender.FEMALE -> "Самка"
}

fun PetSize.label(): String = when (this) {
    PetSize.SMALL -> "Маленький"
    PetSize.MEDIUM -> "Средний"
    PetSize.LARGE -> "Крупный"
}

fun AgeGroup.label(): String = when (this) {
    AgeGroup.BABY -> "До года"
    AgeGroup.YOUNG -> "1–3 года"
    AgeGroup.ADULT -> "3–7 лет"
    AgeGroup.SENIOR -> "Старше 7"
}

fun AnimalStatus.label(): String = when (this) {
    AnimalStatus.AVAILABLE -> "Ищет дом"
    AnimalStatus.RESERVED -> "Забронирован"
    AnimalStatus.ADOPTED -> "Уже дома"
}

fun ApplicationStatus.label(): String = when (this) {
    ApplicationStatus.PENDING -> "На рассмотрении"
    ApplicationStatus.APPROVED -> "Одобрено"
    ApplicationStatus.REJECTED -> "Отклонено"
    ApplicationStatus.CANCELLED -> "Отменена"
}

fun UserRole.label(): String = when (this) {
    UserRole.USER -> "Пользователь"
    UserRole.LOCAL_ADMIN -> "Локальный администратор"
}

fun formatDateTime(epochMillis: Long): String {
    val format = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru"))
    return format.format(Date(epochMillis))
}
