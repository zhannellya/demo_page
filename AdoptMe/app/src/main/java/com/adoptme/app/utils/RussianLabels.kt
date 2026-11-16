package com.adoptme.app.utils

import com.adoptme.app.data.model.AgeGroup
import com.adoptme.app.data.model.AnimalSize
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.model.Availability
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.HealthStatus
import com.adoptme.app.data.model.NewsCategory
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun AnimalType.label(): String = when (this) {
    AnimalType.CAT -> "Кошка"
    AnimalType.DOG -> "Собака"
    AnimalType.OTHER -> "Другое"
}

fun Gender.label(): String = when (this) {
    Gender.MALE -> "Мальчик"
    Gender.FEMALE -> "Девочка"
}

fun AnimalSize.label(): String = when (this) {
    AnimalSize.SMALL -> "Маленький"
    AnimalSize.MEDIUM -> "Средний"
    AnimalSize.LARGE -> "Крупный"
}

fun AgeGroup.label(): String = when (this) {
    AgeGroup.UNDER_ONE -> "До 1 года"
    AgeGroup.ONE_TO_THREE -> "1–3 года"
    AgeGroup.OVER_THREE -> "Старше 3 лет"
}

fun HealthStatus.label(): String = when (this) {
    HealthStatus.HEALTHY -> "Здоров"
    HealthStatus.RECOVERING -> "Восстанавливается"
    HealthStatus.NEEDS_CARE -> "Нужен уход"
}

fun Availability.label(): String = when (this) {
    Availability.AVAILABLE -> "Ищет дом"
    Availability.RESERVED -> "Зарезервирован"
    Availability.ADOPTED -> "Уже дома"
}

fun ApplicationStatus.label(): String = when (this) {
    ApplicationStatus.PENDING -> "На рассмотрении"
    ApplicationStatus.APPROVED -> "Одобрено"
    ApplicationStatus.REJECTED -> "Отклонено"
    ApplicationStatus.CANCELLED -> "Отменено"
}

fun NewsCategory.label(): String = when (this) {
    NewsCategory.SHELTER -> "Новости приютов"
    NewsCategory.CARE -> "Советы по уходу"
    NewsCategory.STORY -> "Истории усыновления"
}

private val russianDateTime: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale("ru"))

fun Long.toRussianDateTime(): String =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).format(russianDateTime)
