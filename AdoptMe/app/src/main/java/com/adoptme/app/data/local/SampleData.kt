package com.adoptme.app.data.local

import com.adoptme.app.R
import com.adoptme.app.data.local.db.AnimalEntity
import com.adoptme.app.data.local.db.ShelterEntity
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.AppNotification
import com.adoptme.app.data.model.Clinic
import com.adoptme.app.data.model.DonationOption
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.HealthStatus
import com.adoptme.app.data.model.LostFoundPost
import com.adoptme.app.data.model.LostFoundType
import com.adoptme.app.data.model.OnboardingPage
import com.adoptme.app.data.model.OwnerType
import com.adoptme.app.data.model.Shelter
import com.adoptme.app.data.model.formatKzt

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

    val shelterEntities: List<ShelterEntity> = listOf(
        ShelterEntity(
            id = "s1",
            name = "Приют «Тёплый дом»",
            city = "Тараз",
            address = "ул. Толе би, 112",
            phone = "+7 7262 45 11 22",
            workingHours = "09:00–18:00",
            description = "Городской приют Тараза помогает кошкам и собакам найти дом. Здесь делают прививки, стерилизацию и социализацию перед передачей в семью.",
            logoUri = "https://images.unsplash.com/photo-1450778869180-41d0601e046e?auto=format&fit=crop&w=600&q=80",
            bankDetails = "Банк: АО «Halyk Bank»\nПолучатель: ОО «Тёплый дом»\nБИН: 180140012345\nIBAN: KZ12345H000012345678\nБИК: HSBKKZKX",
            latitude = 42.8996,
            longitude = 71.3660
        ),
        ShelterEntity(
            id = "s2",
            name = "Приют «Лапка»",
            city = "Алматы",
            address = "ул. Абая, 44",
            phone = "+7 727 250 22 22",
            workingHours = "10:00–19:00",
            description = "Приют в Алматы для кошек и собак. Волонтёры выгуливают питомцев, а гости могут познакомиться с животными по предварительной записи.",
            logoUri = "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&w=600&q=80",
            bankDetails = "Банк: АО «Kaspi Bank»\nПолучатель: ОФ «Лапка»\nБИН: 190540023456\nIBAN: KZ98722K000098765432\nБИК: CASPKZKA",
            latitude = 43.2380,
            longitude = 76.9450
        ),
        ShelterEntity(
            id = "s3",
            name = "Приют «Доброе сердце»",
            city = "Астана",
            address = "ул. Кабанбай батыра, 18",
            phone = "+7 7172 40 33 33",
            workingHours = "09:30–17:30",
            description = "Столичный приют с ветеринарным блоком. Принимаем животных с улицы, лечим и готовим к усыновлению в Астане и области.",
            logoUri = "https://images.unsplash.com/photo-1601758228041-f3b2795255f1?auto=format&fit=crop&w=600&q=80",
            bankDetails = "Банк: АО «ForteBank»\nПолучатель: ОО «Доброе сердце»\nБИН: 170340034567\nIBAN: KZ55977F000055667788\nБИК: IRTYKZKA",
            latitude = 51.1694,
            longitude = 71.4491
        ),
        ShelterEntity(
            id = "s4",
            name = "Приют «Хвостатые»",
            city = "Шымкент",
            address = "пр. Тауке хана, 7",
            phone = "+7 7252 53 44 44",
            workingHours = "08:00–16:00",
            description = "Приют Шымкента для крупных и активных собак, а также кошек. Есть вольеры, площадка для выгула и комната адаптации.",
            logoUri = "https://images.unsplash.com/photo-1530281700549-e82e7bf110d6?auto=format&fit=crop&w=600&q=80",
            bankDetails = "Банк: АО «Halyk Bank»\nПолучатель: ОФ «Хвостатые»\nБИН: 210240045678\nIBAN: KZ44125H000044556677\nБИК: HSBKKZKX",
            latitude = 42.3419,
            longitude = 69.5901
        ),
        ShelterEntity(
            id = "s5",
            name = "Приют «Амиго»",
            city = "Алматы",
            address = "ул. Ер Косай батыра, 15а",
            phone = "+7 701 750 0984",
            workingHours = "Пн–Вс 09:00–18:00",
            description = "Приют «Амиго» существует с 2018 года. На сегодняшний день насчитывается около 200 собак и 80 кошек. Расположен в Алматинской области.",
            logoUri = "https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&w=600&q=80",
            bankDetails = "Банк: АО «KASPI BANK»\nПолучатель: Общественный фонд «Амиго-4-Лапы»\nБИН: 200640003939\nIBAN: KZ98722S000047583950\nБИК: CASPKZKA",
            latitude = 43.2565,
            longitude = 76.9284
        ),
        ShelterEntity(
            id = "s6",
            name = "Приют «Четыре лапы»",
            city = "Караганда",
            address = "ул. Бухар жырау, 25",
            phone = "+7 7212 41 55 55",
            workingHours = "09:00–18:00",
            description = "Карагандинский приют помогает бездомным животным области. Есть карантин, прививочный пункт и волонтёрские смены в выходные.",
            logoUri = "https://images.unsplash.com/photo-1544568100-847a948890b0?auto=format&fit=crop&w=600&q=80",
            bankDetails = "Банк: АО «Jusan Bank»\nПолучатель: ОО «Четыре лапы»\nБИН: 160840056789\nIBAN: KZ77890J000077889900\nБИК: TSESKZKA",
            latitude = 49.8060,
            longitude = 73.0850
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
            location = "Приют «Тёплый дом», Тараз",
            city = "Тараз",
            shelterId = "s1",
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
            location = "Приют «Лапка», Алматы",
            city = "Алматы",
            shelterId = "s2",
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
            location = "Приют «Тёплый дом», Тараз",
            city = "Тараз",
            shelterId = "s1",
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
            location = "Приют «Доброе сердце», Астана",
            city = "Астана",
            shelterId = "s3",
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
            location = "Приют «Лапка», Алматы",
            city = "Алматы",
            shelterId = "s2",
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
            location = "Приют «Хвостатые», Шымкент",
            city = "Шымкент",
            shelterId = "s4",
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
            location = "Приют «Доброе сердце», Астана",
            city = "Астана",
            shelterId = "s3",
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
            location = "Приют «Лапка», Алматы",
            city = "Алматы",
            shelterId = "s2",
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
            location = "Приют «Доброе сердце», Астана",
            city = "Астана",
            shelterId = "s3",
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
            location = "Приют «Амиго», Алматы",
            city = "Алматы",
            shelterId = "s5",
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
            location = "Приют «Четыре лапы», Караганда",
            city = "Караганда",
            shelterId = "s6",
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
            location = "Приют «Амиго», Алматы",
            city = "Алматы",
            shelterId = "s5",
            size = "Маленький",
            sterilized = false
        )
    )

    val shelters: List<Shelter> = shelterEntities.map { entity ->
        Shelter(
            id = entity.id,
            name = entity.name,
            address = entity.address,
            phone = entity.phone,
            latitude = entity.latitude,
            longitude = entity.longitude,
            openHours = entity.workingHours,
            city = entity.city,
            description = entity.description,
            logoUri = entity.logoUri,
            bankDetails = entity.bankDetails
        )
    }

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
        DonationOption("d1", "Корм на неделю", "Качественный корм для одного питомца", formatKzt(1500), "food", 1500, "food"),
        DonationOption("d2", "Лечение", "Вакцинация и осмотр врача", formatKzt(4000), "medical", 4000, "medical"),
        DonationOption("d3", "Поддержка приюта", "Лежанки, миски и игрушки", formatKzt(2500), "shelter", 2500, "shelter_support"),
        DonationOption("d5", "Произвольная сумма", "Выберите сумму, которая вам по силам", "₸", "custom", 0, "custom"),
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
            "Посмотрите животных рядом: Алматы, Астана, Шымкент, Караганда и Тараз.",
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
        goodWithKids: Boolean = true,
        city: String,
        shelterId: String
    ): AnimalEntity {
        val shelter = shelterEntities.first { it.id == shelterId }
        return AnimalEntity(
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
            goodWithKids = goodWithKids,
            city = city,
            ownerType = OwnerType.SHELTER.name,
            ownerName = shelter.name,
            ownerPhone = shelter.phone,
            shelterId = shelterId,
            isUserCreated = false
        )
    }
}
