package com.adoptme.app.data.local

import androidx.room.TypeConverter
import com.adoptme.app.data.model.AnimalSize
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.model.Availability
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.HealthStatus
import com.adoptme.app.data.model.NewsCategory

class Converters {
    @TypeConverter fun fromAnimalType(value: AnimalType): String = value.name
    @TypeConverter fun toAnimalType(value: String): AnimalType = AnimalType.valueOf(value)

    @TypeConverter fun fromGender(value: Gender): String = value.name
    @TypeConverter fun toGender(value: String): Gender = Gender.valueOf(value)

    @TypeConverter fun fromSize(value: AnimalSize): String = value.name
    @TypeConverter fun toSize(value: String): AnimalSize = AnimalSize.valueOf(value)

    @TypeConverter fun fromHealth(value: HealthStatus): String = value.name
    @TypeConverter fun toHealth(value: String): HealthStatus = HealthStatus.valueOf(value)

    @TypeConverter fun fromAvailability(value: Availability): String = value.name
    @TypeConverter fun toAvailability(value: String): Availability = Availability.valueOf(value)

    @TypeConverter fun fromStatus(value: ApplicationStatus): String = value.name
    @TypeConverter fun toStatus(value: String): ApplicationStatus = ApplicationStatus.valueOf(value)

    @TypeConverter fun fromNews(value: NewsCategory): String = value.name
    @TypeConverter fun toNews(value: String): NewsCategory = NewsCategory.valueOf(value)
}
