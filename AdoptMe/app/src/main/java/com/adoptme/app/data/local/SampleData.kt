package com.adoptme.app.data.local

import com.adoptme.app.R
import com.adoptme.app.data.local.db.AnimalEntity
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.AppNotification
import com.adoptme.app.data.model.Clinic
import com.adoptme.app.data.model.DonationOption
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.HealthStatus
import com.adoptme.app.data.model.LostFoundPost
import com.adoptme.app.data.model.LostFoundType
import com.adoptme.app.data.model.OnboardingPage
import com.adoptme.app.data.model.Shelter

object SampleData {

    val onboardingPages = listOf(
        OnboardingPage(
            title = "Найдите друга",
            description = "Кошки и собаки из приютов ждут спокойный и заботливый дом.",
            imageRes = R.drawable.onboarding_cat
        ),
        OnboardingPage(
            title = "Усыновите с любовью",
            description = "Заполните заявку и познакомьтесь с питомцем ближе.",
            imageRes = R.drawable.onboarding_dog
        ),
        OnboardingPage(
            title = "Любовь начинается здесь",
            description = "Сохраняйте избранное и следите за историей заявок.",
            imageRes = R.drawable.onboarding_friends
        )
    )

    val animalEntities: List<AnimalEntity> = listOf(
        animal(
            id = "1",
            name = "Луна",
            type = AnimalType.CAT,
            breed = "Британская короткошёрстная",
            ageMonths = 24,
            ageLabel = "2 года",
            gender = Gender.FEMALE,
            health = HealthStatus.HEALTHY,
            description = "Луна спокойная и ласковая. Любит солнечные подоконники, мягкие пледы и тихие вечера рядом с человеком.",
            imageUrl = "https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&w=900&q=80",
            location = "Приют «Тёплый дом», Москва",
            size = "Средний"
        ),
        animal(
            id = "2",
            name = "Барни",
            type = AnimalType.DOG,
            breed = "Золотистый ретривер",
            ageMonths = 36,
            ageLabel = "3 года",
            gender = Gender.MALE,
            health = HealthStatus.HEALTHY,
            description = "Барни дружелюбный, обучен базовым командам и отлично ладит с детьми. Обожает долгие прогулки и воду.",
            imageUrl = "https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=900&q=80",
            location = "Приют «Лапка», Санкт-Петербург",
            size = "Крупный"
        ),
        animal(
            id = "3",
            name = "Мило",
            type = AnimalType.CAT,
            breed = "Полосатый",
            ageMonths = 12,
            ageLabel = "1 год",
            gender = Gender.MALE,
            health = HealthStatus.HEALTHY,
            description = "Любопытный рыжий исследователь. Играет с удочками, мурчит на коленях и быстро привыкает к новому дому.",
            imageUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=900&q=80",
            location = "Приют «Тёплый дом», Москва",
            size = "Маленький"
        ),
        animal(
            id = "4",
            name = "Белла",
            type = AnimalType.DOG,
            breed = "Бигль",
            ageMonths = 48,
            ageLabel = "4 года",
            gender = Gender.FEMALE,
            health = HealthStatus.RECOVERING,
            description = "Белла восстанавливается после лечения и ищет спокойную семью. Любит нюхательные прогулки и мягкий голос.",
            imageUrl = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=900&q=80",
            location = "Приют «Доброе сердце», Казань",
            size = "Средний",
            goodWithKids = true
        ),
        animal(
            id = "5",
            name = "Нала",
            type = AnimalType.CAT,
            breed = "Сиамская",
            ageMonths = 8,
            ageLabel = "8 месяцев",
            gender = Gender.FEMALE,
            health = HealthStatus.HEALTHY,
            description = "Разговорчивая и изящная. Нала отвечает на своё имя и любит засыпать рядом с хозяином.",
            imageUrl = "https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?auto=format&fit=crop&w=900&q=80",
            location = "Приют «Лапка», Санкт-Петербург",
            size = "Маленький"
        ),
        animal(
            id = "6",
            name = "Макс",
            type = AnimalType.DOG,
            breed = "Хаски",
            ageMonths = 30,
            ageLabel = "2,5 года",
            gender = Gender.MALE,
            health = HealthStatus.HEALTHY,
            description = "Энергичный компаньон для активных людей. Макс дружит с другими собаками и нуждается в ежедневной нагрузке.",
            imageUrl = "https://images.unsplash.com/photo-1605568427561-40dd23c2acea?auto=format&fit=crop&w=900&q=80",
            location = "Приют «Хвостатые», Новосибирск",
            size = "Крупный",
            goodWithKids = true
        ),
        animal(
            id = "7",
            name = "Клео",
            type = AnimalType.CAT,
            breed = "Персидская",
            ageMonths = 60,
            ageLabel = "5 лет",
            gender = Gender.FEMALE,
            health = HealthStatus.NEEDS_CARE,
            description = "Пушистая и нежная Клео нуждается в регулярном груминге. За терпение отвечает долгим мурчанием.",
            imageUrl = "https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&w=900&q=80",
            location = "Приют «Доброе сердце», Казань",
            size = "Средний",
            goodWithKids = false
        ),
        animal(
            id = "8",
            name = "Чарли",
            type = AnimalType.DOG,
            breed = "Корги",
            ageMonths = 18,
            ageLabel = "1,5 года",
            gender = Gender.MALE,
            health = HealthStatus.HEALTHY,
            description = "Короткие лапы и огромный характер. Чарли обожает парк, игрушки и быть в центре внимания.",
            imageUrl = "https://images.unsplash.com/photo-1612536057832-2ff7ebd6c4f6?auto=format&fit=crop&w=900&q=80",
            location = "Приют «Лапка», Санкт-Петербург",
            size = "Маленький"
        ),
        animal(
            id = "9",
            name = "Мурка",
            type = AnimalType.CAT,
            breed = "Дворовая",
            ageMonths = 6,
            ageLabel = "6 месяцев",
            gender = Gender.FEMALE,
            health = HealthStatus.HEALTHY,
            description = "Маленькая трёхцветная кошечка. Быстро социализируется, ест самостоятельно и любит тёплые руки.",
            imageUrl = "https://images.unsplash.com/photo-1592194996308-7b43878e84a6?auto=format&fit=crop&w=900&q=80",
            location = "Приют «Доброе сердце», Казань",
            size = "Маленький",
            sterilized = false
        ),
        animal(
            id = "10",
            name = "Рекс",
            type = AnimalType.DOG,
            breed = "Лабрадор",
            ageMonths = 20,
            ageLabel = "1 год 8 месяцев",
            gender = Gender.MALE,
            health = HealthStatus.HEALTHY,
            description = "Умный и уравновешенный лабрадор. Рекс знает «сидеть» и «ко мне», подойдёт семье с детьми.",
            imageUrl = "https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&w=900&q=80",
            location = "Приют «Тёплый дом», Москва",
            size = "Крупный"
        ),
        animal(
            id = "11",
            name = "Сёма",
            type = AnimalType.CAT,
            breed = "Мейн-кун",
            ageMonths = 42,
            ageLabel = "3,5 года",
            gender = Gender.MALE,
            health = HealthStatus.HEALTHY,
            description = "Крупный и добродушный. Сёма ходит на шлейке, любит расчёсывание и наблюдать за птицами из окна.",
            imageUrl = "https://images.unsplash.com/photo-1495360010541-f48722b34f7d?auto=format&fit=crop&w=900&q=80",
            location = "Приют «Хвостатые», Новосибирск",
            size = "Крупный"
        ),
        animal(
            id = "12",
            name = "Джек",
            type = AnimalType.DOG,
            breed = "Такса",
            ageMonths = 9,
            ageLabel = "9 месяцев",
            gender = Gender.MALE,
            health = HealthStatus.HEALTHY,
            description = "Весёлый щенок таксы. Джек учится ходить на поводке и ищет дом, где с ним будут играть каждый день.",
            imageUrl = "https://images.unsplash.com/photo-1517849845537-4d257902454a?auto=format&fit=crop&w=900&q=80",
            location = "Приют «Тёплый дом», Москва",
            size = "Маленький",
            sterilized = false
        )
    )

    val shelters = listOf(
        Shelter("s1", "Приют «Тёплый дом»", "ул. Лесная, 12, Москва", "+7 495 000 11 11", 55.751244, 37.618423, "09:00–18:00"),
        Shelter("s2", "Приют «Лапка»", "Невский пр., 42, Санкт-Петербург", "+7 812 000 22 22", 59.934280, 30.335099, "10:00–19:00"),
        Shelter("s3", "Приют «Доброе сердце»", "ул. Баумана, 18, Казань", "+7 843 000 33 33", 55.788740, 49.122140, "09:30–17:30"),
        Shelter("s4", "Приют «Хвостатые»", "Красный пр., 7, Новосибирск", "+7 383 000 44 44", 55.030199, 82.920430, "08:00–16:00")
    )

    val clinics = listOf(
        Clinic("c1", "Ветклиника «Забота»", "ул. Тверская, 8, Москва", "+7 495 111 10 01", "Терапия · хирургия · вакцинация", 4.8f),
        Clinic("c2", "Клиника «Хвост и ус»", "Литейный пр., 21, Санкт-Петербург", "+7 812 111 10 02", "Экстренная помощь · стоматология", 4.6f),
        Clinic("c3", "Ветцентр «Мягкие лапы»", "ул. Пушкина, 5, Казань", "+7 843 111 10 03", "Кошки и собаки · диагностика", 4.9f)
    )

    val lostFound = listOf(
        LostFoundPost(
            "lf1", LostFoundType.LOST, "Симба", "Кошка",
            "Рыжий кот в синем ошейнике, последний раз видели у парка.",
            "Парк Горького, Москва", "+7 900 111 22 33",
            "https://images.unsplash.com/photo-1495360010541-f48722b34f7d?auto=format&fit=crop&w=600&q=80",
            "2 дня назад"
        ),
        LostFoundPost(
            "lf2", LostFoundType.FOUND, "Щенок без клички", "Собака",
            "Небольшой коричневый щенок нашёлся у метро. Ищем хозяина.",
            "м. Невский проспект", "+7 900 222 33 44",
            "https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&w=600&q=80",
            "Вчера"
        ),
        LostFoundPost(
            "lf3", LostFoundType.LOST, "Маффин", "Кошка",
            "Белая пушистая кошка, откликается на имя Маффин, есть чип.",
            "Вахитовский район, Казань", "+7 900 333 44 55",
            "https://images.unsplash.com/photo-1511044568932-338cba0ad803?auto=format&fit=crop&w=600&q=80",
            "5 дней назад"
        )
    )

    val donations = listOf(
        DonationOption("d1", "Корм на неделю", "Качественный корм для одного питомца", "1 500 ₽", "food"),
        DonationOption("d2", "Лечение", "Вакцинация и осмотр врача", "4 000 ₽", "medical"),
        DonationOption("d3", "Поддержка приюта", "Лежанки, миски и игрушки", "2 500 ₽", "shelter"),
        DonationOption("d4", "Волонтёрство", "Прогулки и уход в выходные", "Бесплатно", "volunteer")
    )

    val notifications = listOf(
        AppNotification(
            "n1",
            "Добро пожаловать",
            "В ADOPTME уже ждут кошки и собаки из приютов.",
            "Сегодня",
            false
        ),
        AppNotification(
            "n2",
            "Новые анкеты",
            "Посмотрите животных рядом: Москва, Санкт-Петербург, Казань и Новосибирск.",
            "1 час назад",
            false
        ),
        AppNotification(
            "n3",
            "Спасибо",
            "Помощь приютам кормит питомцев, пока они ищут дом.",
            "Вчера",
            true
        )
    )

    private fun animal(
        id: String,
        name: String,
        type: AnimalType,
        breed: String,
        ageMonths: Int,
        ageLabel: String,
        gender: Gender,
        health: HealthStatus,
        description: String,
        imageUrl: String,
        location: String,
        size: String,
        vaccinated: Boolean = true,
        sterilized: Boolean = true,
        goodWithKids: Boolean = true
    ) = AnimalEntity(
        id = id,
        name = name,
        type = type.name,
        breed = breed,
        ageMonths = ageMonths,
        ageLabel = ageLabel,
        gender = gender.name,
        healthStatus = health.name,
        description = description,
        imageUrl = imageUrl,
        location = location,
        size = size,
        vaccinated = vaccinated,
        sterilized = sterilized,
        goodWithKids = goodWithKids
    )
}
