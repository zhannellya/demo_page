package com.adoptme.app.data.local

import com.adoptme.app.R
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.AnimalSize
import com.adoptme.app.data.model.AnimalType
import com.adoptme.app.data.model.Availability
import com.adoptme.app.data.model.Clinic
import com.adoptme.app.data.model.Gender
import com.adoptme.app.data.model.HealthStatus
import com.adoptme.app.data.model.LostFoundPost
import com.adoptme.app.data.model.LostFoundType
import com.adoptme.app.data.model.NewsArticle
import com.adoptme.app.data.model.NewsCategory
import com.adoptme.app.data.model.OnboardingPage
import com.adoptme.app.data.model.Shelter

object SeedData {
    const val CONTACT_NOTE =
        "Точный адрес и телефон не публикуются: карточка демонстрационная и не описывает реальную организацию. Напишите в чат приложения."

    val onboardingPages = listOf(
        OnboardingPage(
            title = "Найди друга",
            description = "Милые котята и собаки из приютов ждут заботливую семью.",
            imageRes = R.drawable.onboarding_cat
        ),
        OnboardingPage(
            title = "Подай заявку",
            description = "Расскажите о себе и отправьте заявку на усыновление в несколько шагов.",
            imageRes = R.drawable.onboarding_dog
        ),
        OnboardingPage(
            title = "Забери домой",
            description = "Сохраняйте избранное, смотрите приюты и следите за статусом заявки.",
            imageRes = R.drawable.onboarding_friends
        )
    )

    val shelters = listOf(
        Shelter(
            id = "shelter-moscow",
            name = "Демонстрационный приют «Тёплый дом»",
            city = "Москва",
            description = "Учебная карточка приюта для приложения ADOPTME. Здесь живут Шарик и Бобик, пока им не найдётся семья в этом демонстрационном сценарии.",
            contactNote = CONTACT_NOTE,
            hours = "Ежедневно, 10:00–18:00 (пример графика, не режим реального приюта)"
        ),
        Shelter(
            id = "shelter-spb",
            name = "Демонстрационный приют «Верный друг»",
            city = "Санкт-Петербург",
            description = "Учебная карточка приюта. В каталоге отсюда показаны Тоша и Дружок.",
            contactNote = CONTACT_NOTE,
            hours = "Ежедневно, 11:00–19:00 (пример графика, не режим реального приюта)"
        ),
        Shelter(
            id = "shelter-kazan",
            name = "Демонстрационный приют «Лапа помощи»",
            city = "Казань",
            description = "Учебная карточка приюта. В каталоге отсюда показаны Белла и Рекс.",
            contactNote = CONTACT_NOTE,
            hours = "Ежедневно, 09:00–17:00 (пример графика, не режим реального приюта)"
        ),
        Shelter(
            id = "shelter-nsk",
            name = "Демонстрационный приют «Счастливый хвост»",
            city = "Новосибирск",
            description = "Учебная карточка приюта. В каталоге отсюда показаны Барсик и Макс.",
            contactNote = CONTACT_NOTE,
            hours = "Ежедневно, 10:00–18:00 (пример графика, не режим реального приюта)"
        )
    )

    val animals = listOf(
        Animal(
            id = "sharik",
            name = "Шарик",
            type = AnimalType.DOG,
            breed = "Дворняжка",
            ageLabel = "4 года",
            ageMonths = 48,
            gender = Gender.MALE,
            size = AnimalSize.LARGE,
            healthStatus = HealthStatus.HEALTHY,
            availability = Availability.AVAILABLE,
            personality = "Спокойный и преданный, любит долгие прогулки без суеты.",
            description = "Шарик — крупный пёс с мягким взглядом и рыже-белой шерстью. Он не прыгает на людей и спокойно ждёт у двери.",
            story = "Шарика привезли в демонстрационный приют «Тёплый дом» после того, как его нашли у закрытой стройки. Первые недели он боялся громких звуков, но постепенно начал доверять волонтёрам и теперь встречает их тихим вилянием хвоста. Ему нужен дом с предсказуемым режимом и человеком, который готов гулять утром и вечером. Шарик уже ходит на поводке и может оставаться один на несколько часов, не портя вещи.",
            shelterId = "shelter-moscow",
            shelterName = "Демонстрационный приют «Тёплый дом»",
            city = "Москва",
            imageUrl = "https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=1200",
            vaccinated = true,
            sterilized = true,
            goodWithKids = true
        ),
        Animal(
            id = "tosha",
            name = "Тоша",
            type = AnimalType.CAT,
            breed = "Британская короткошёрстная",
            ageLabel = "2 года",
            ageMonths = 24,
            gender = Gender.MALE,
            size = AnimalSize.MEDIUM,
            healthStatus = HealthStatus.HEALTHY,
            availability = Availability.AVAILABLE,
            personality = "Ласковый и разговорчивый, обожает сидеть на коленях.",
            description = "Тоша — серый кот с плотной шерстью и круглыми янтарными глазами. Он мурлычет, едва его берут на руки.",
            story = "Тоша родился в квартире, откуда всю кошачью семью передали в демонстрационный приют «Верный друг». Он привык к людям и не прячется, когда приходят гости. Любимое место — подоконник с видом во двор. Тоша аккуратно пользуется лотком и не царапает диван, если рядом есть когтеточка. Ему подойдёт тихая квартира: шумные компании его утомляют, зато вечер на коленях он считает лучшим временем суток.",
            shelterId = "shelter-spb",
            shelterName = "Демонстрационный приют «Верный друг»",
            city = "Санкт-Петербург",
            imageUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=1200",
            vaccinated = true,
            sterilized = true,
            goodWithKids = false
        ),
        Animal(
            id = "bella",
            name = "Белла",
            type = AnimalType.DOG,
            breed = "Бигль",
            ageLabel = "3 года",
            ageMonths = 36,
            gender = Gender.FEMALE,
            size = AnimalSize.MEDIUM,
            healthStatus = HealthStatus.RECOVERING,
            availability = Availability.AVAILABLE,
            personality = "Нежная и любопытная, обожает запахи и мягкие пледы.",
            description = "Белла — небольшая гончая с бархатными ушами. После лечения она снова играет, но устаёт быстрее других собак.",
            story = "Белла попала в демонстрационный приют «Лапа помощи» худой и с воспалённой лапой. Курс лечения уже закончен, и сейчас ей нужны спокойные прогулки и контроль веса. Она обожает искать угощение в траве и спать у ног человека. Со взрослыми кошками Белла ладит, если знакомить их медленно. Ей подойдёт семья, которая не оставляет её одну на весь рабочий день и не путает усталость с ленью.",
            shelterId = "shelter-kazan",
            shelterName = "Демонстрационный приют «Лапа помощи»",
            city = "Казань",
            imageUrl = "https://images.unsplash.com/photo-1537151625747-768eb6cf92b2?w=1200",
            vaccinated = true,
            sterilized = true,
            goodWithKids = true
        ),
        Animal(
            id = "barsik",
            name = "Барсик",
            type = AnimalType.CAT,
            breed = "Полосатый домашний",
            ageLabel = "8 месяцев",
            ageMonths = 8,
            gender = Gender.MALE,
            size = AnimalSize.SMALL,
            healthStatus = HealthStatus.HEALTHY,
            availability = Availability.AVAILABLE,
            personality = "Любопытный и шустрый, учится играть без когтей.",
            description = "Барсик — полосатый котёнок с белой манишкой. Он гоняется за мячиком и потом мгновенно засыпает.",
            story = "Барсика нашли в подъезде вместе с сестрой, которую уже забрали в другую семью. Он остался один и быстро привязался к дежурному волонтёру демонстрационного приюта «Счастливый хвост». Котёнок привит по возрасту и приучен к лотку. Ему нужен дом, где будут играть каждый день и прятать провода: он ещё пробует всё на зуб. Барсик не боится переноски и спокойно переносит короткую поездку.",
            shelterId = "shelter-nsk",
            shelterName = "Демонстрационный приют «Счастливый хвост»",
            city = "Новосибирск",
            imageUrl = "https://images.unsplash.com/photo-1574158622682-e40e69881006?w=1200",
            vaccinated = true,
            sterilized = false,
            goodWithKids = true
        ),
        Animal(
            id = "bobik",
            name = "Бобик",
            type = AnimalType.DOG,
            breed = "Корги-микс",
            ageLabel = "1 год",
            ageMonths = 12,
            gender = Gender.MALE,
            size = AnimalSize.SMALL,
            healthStatus = HealthStatus.HEALTHY,
            availability = Availability.AVAILABLE,
            personality = "Весёлый и упрямый, очень любит еду и похвалу.",
            description = "Бобик — коротколапый пёс с лисьей мордой. Он приносит мячик столько раз, сколько вы готовы бросать.",
            story = "Бобика передали в демонстрационный приют «Тёплый дом», когда прежняя семья переехала туда, где собак держать нельзя. Он знает команды «сидеть» и «ко мне», но на прогулке отвлекается на голубей. Ему нужен человек, который будет заниматься с ним хотя бы десять минут в день. С детьми старше семи лет Бобик играет бережно. Один в пустой квартире он скучает и может тихо поскуливать, поэтому лучше, если днём кто-то бывает дома.",
            shelterId = "shelter-moscow",
            shelterName = "Демонстрационный приют «Тёплый дом»",
            city = "Москва",
            imageUrl = "https://images.unsplash.com/photo-1612536057832-2ff7ebd6c4f6?w=1200",
            vaccinated = true,
            sterilized = true,
            goodWithKids = true
        ),
        Animal(
            id = "druzhok",
            name = "Дружок",
            type = AnimalType.DOG,
            breed = "Лабрадор",
            ageLabel = "5 лет",
            ageMonths = 60,
            gender = Gender.MALE,
            size = AnimalSize.LARGE,
            healthStatus = HealthStatus.HEALTHY,
            availability = Availability.AVAILABLE,
            personality = "Терпеливый и дружелюбный, обожает воду и спокойные компании.",
            description = "Дружок — крепкий лабрадор песочного окраса. Он кладёт голову на колени и смотрит так, будто уже выбрал человека.",
            story = "Дружок несколько лет жил во дворе частного дома, пока семья не смогла его кормить. В демонстрационном приюте «Верный друг» он стал любимцем дежурных: встречает гостей и не конфликтует с другими собаками. Дружок умеет ждать у миски и не подбирает еду с земли, если сказать «нельзя». Он ищет дом с ежедневной долгой прогулкой. Квартира ему подходит, если рядом есть парк и место, где можно вытереть лапы после дождя.",
            shelterId = "shelter-spb",
            shelterName = "Демонстрационный приют «Верный друг»",
            city = "Санкт-Петербург",
            imageUrl = "https://images.unsplash.com/photo-1552053831-71594a27632d?w=1200",
            vaccinated = true,
            sterilized = true,
            goodWithKids = true
        ),
        Animal(
            id = "reks",
            name = "Рекс",
            type = AnimalType.DOG,
            breed = "Немецкая овчарка",
            ageLabel = "6 лет",
            ageMonths = 72,
            gender = Gender.MALE,
            size = AnimalSize.LARGE,
            healthStatus = HealthStatus.HEALTHY,
            availability = Availability.AVAILABLE,
            personality = "Внимательный и сдержанный, привязывается к одному человеку.",
            description = "Рекс — овчарка с тёмной спиной и спокойной осанкой. Он не лает без причины и следит за дверью.",
            story = "Рекс работал на частной охране и оказался в демонстрационном приюте «Лапа помощи», когда смена хозяина оставила его без дела. Он знает базовые команды и лучше чувствует себя рядом со взрослым человеком, а не в шумной компании. Рексу не нужен двор и цепь: ему нужны понятные правила, мягкое место и прогулки на длинном поводке. С кошками его не знакомили. Детям он не мешает, но бегать с ними не будет — это не его игра.",
            shelterId = "shelter-kazan",
            shelterName = "Демонстрационный приют «Лапа помощи»",
            city = "Казань",
            imageUrl = "https://images.unsplash.com/photo-1561037404-61cd46aa615b?w=1200",
            vaccinated = true,
            sterilized = true,
            goodWithKids = false
        ),
        Animal(
            id = "maks",
            name = "Макс",
            type = AnimalType.DOG,
            breed = "Хаски",
            ageLabel = "2 года",
            ageMonths = 24,
            gender = Gender.MALE,
            size = AnimalSize.LARGE,
            healthStatus = HealthStatus.HEALTHY,
            availability = Availability.AVAILABLE,
            personality = "Энергичный и разговорчивый, обожает снег и бег.",
            description = "Макс — хаски с голубыми глазами и густой шерстью. Он рассказывает о прогулке на своём языке и готов бежать километры.",
            story = "Макса привезли в демонстрационный приют «Счастливый хвост» из квартиры, где ему не хватало движения. В вольере он скучал, а на площадке сразу оживал. Максу нужна семья, которая много гуляет даже в мороз, бегает или катается на лыжах. Он линяет дважды в год и требует расчёски. Ограду во дворе стоит проверять: хаски ищет лазейку, если заскучает. С другими собаками Макс играет азартно, но без злости, если его не держат на коротком поводке всю прогулку.",
            shelterId = "shelter-nsk",
            shelterName = "Демонстрационный приют «Счастливый хвост»",
            city = "Новосибирск",
            imageUrl = "https://images.unsplash.com/photo-1605568427561-40dd23c2acea?w=1200",
            vaccinated = true,
            sterilized = false,
            goodWithKids = true
        )
    )

    val news = listOf(
        NewsArticle(
            id = "news-visit",
            title = "Что взять на знакомство с животным",
            summary = "Короткий список для первой встречи в демонстрационном приюте.",
            body = "Возьмите сменную обувь, спокойный голос и двадцать минут без спешки. Не кормите животное едой из сумки: в карточках ADOPTME рацион назначает приют. Если питомец отвернулся, не тяните его к себе. Повторный визит часто говорит о характере больше, чем первые пять минут. Эта заметка учебная и не заменяет правила конкретного приюта.",
            category = NewsCategory.SHELTER,
            publishedLabel = "12 марта 2026"
        ),
        NewsArticle(
            id = "news-kitten",
            title = "Как подготовить дом к котёнку",
            summary = "Лоток, когтеточка и спрятанные провода важнее новой лежанки.",
            body = "Поставьте лоток в тихом месте и не двигайте его первую неделю. Закройте щели за стиральной машиной и уберите нитки. Котёнку нужны две миски, когтеточка выше его вытянутой лапы и игра по расписанию, а не когда-нибудь. Не наказывайте за лужу: отнесите его к лотку после сна и еды. Совет общий и не заменяет осмотр ветеринара.",
            category = NewsCategory.CARE,
            publishedLabel = "2 апреля 2026"
        ),
        NewsArticle(
            id = "news-winter",
            title = "Зимние прогулки со взрослой собакой",
            summary = "Коротколапым и выздоравливающим собакам холод даётся тяжелее.",
            body = "Проверяйте подушечки лап после реагента и промывайте их тёплой водой. Длинная шерсть не отменяет усталость: если собака садится на снег, прогулку пора заканчивать. Белла из каталога как раз из тех, кому лучше короткие выходы, чем один изматывающий маршрут. Не оставляйте собаку в машине «на минуту». Это общий совет, а не медицинское назначение.",
            category = NewsCategory.CARE,
            publishedLabel = "18 января 2026"
        ),
        NewsArticle(
            id = "news-mura",
            title = "История Муры: первый месяц дома",
            summary = "Демонстрационный рассказ о том, как кошка привыкала к новой квартире.",
            body = "Мура — вымышленная героиня этой учебной истории, её нет в текущем каталоге. Первую неделю она жила в одной комнате, ела только ночью и не подходила к гостям. Хозяева не вытаскивали её из-под дивана и кормили в одно время. На третьей неделе Мура сама легла на плед у батареи. История показывает темп адаптации и не описывает реальное усыновление.",
            category = NewsCategory.STORY,
            publishedLabel = "9 февраля 2026"
        ),
        NewsArticle(
            id = "news-volunteer",
            title = "Как проходит день волонтёра",
            summary = "Прогулки, уборка и спокойные знакомства важнее фотографий.",
            body = "В демонстрационном сценарии волонтёр начинает с мисок и свежей воды, затем выводит собак по одной и отмечает, кто нервничает у двери. Фото для каталога делают только после прогулки, когда животное расслаблено. Если хотите помочь, откройте раздел «Помощь»: платежи в приложении не подключены, а заявка на волонтёрство остаётся информационной.",
            category = NewsCategory.SHELTER,
            publishedLabel = "21 мая 2026"
        ),
        NewsArticle(
            id = "news-friend",
            title = "Как Дружок выбирал человека",
            summary = "Учебная зарисовка о том, почему спокойный визит важнее громких обещаний.",
            body = "В этой демонстрационной сцене Дружок не пошёл к посетителю, который звал его через вольер. Он подошёл к тому, кто сел на лавку и ждал. Сотрудник приюта записал наблюдение в карточку: пёс выбирает тихий темп. Зарисовка объясняет, как читать поведение, и не означает, что Дружка уже забрали.",
            category = NewsCategory.STORY,
            publishedLabel = "7 июня 2026"
        )
    )

    val clinics = listOf(
        Clinic(
            id = "clinic-1",
            name = "Демонстрационная клиника «Забота»",
            city = "Москва",
            note = "Карточка для интерфейса. Это не действующая клиника, адрес и телефон намеренно не указаны.",
            specialties = "Осмотр, прививки, обработка от паразитов"
        ),
        Clinic(
            id = "clinic-2",
            name = "Демонстрационная клиника «Мягкая лапа»",
            city = "Казань",
            note = "Карточка для интерфейса. Не используйте её, чтобы искать реальный кабинет.",
            specialties = "Наблюдение после лечения, консультация по кормлению"
        )
    )

    val lostFound = listOf(
        LostFoundPost(
            id = "lf-1",
            type = LostFoundType.LOST,
            animalName = "Симба",
            species = "Кот",
            description = "Учебное объявление. Рыжий кот, на фото в карточке — иллюстрация, а не подтверждённый снимок пропавшего животного.",
            location = "Город не уточнён. Точка на карте не ставится, чтобы не выдавать пример за реальный адрес.",
            contact = "Связь только через демонстрационный чат приложения.",
            imageUrl = "https://images.unsplash.com/photo-1495366691023-cc4ead609f61?w=800",
            date = "Пример, 2 дня назад"
        ),
        LostFoundPost(
            id = "lf-2",
            type = LostFoundType.FOUND,
            animalName = "Щенок без клички",
            species = "Собака",
            description = "Учебное объявление о найденном щенке. Место находки сознательно не указано.",
            location = "Район не публикуется.",
            contact = "Связь только через демонстрационный чат приложения.",
            imageUrl = "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=800",
            date = "Пример, вчера"
        )
    )
}
