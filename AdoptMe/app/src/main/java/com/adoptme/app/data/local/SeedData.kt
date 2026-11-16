package com.adoptme.app.data.local

import com.adoptme.app.data.local.entity.AnimalEntity
import com.adoptme.app.data.local.entity.NewsEntity
import com.adoptme.app.data.local.entity.ShelterEntity
import com.adoptme.app.data.local.entity.UserEntity
import com.adoptme.app.data.model.UserRole
import com.adoptme.app.data.model.ageGroupOf
import com.adoptme.app.utils.PasswordHasher
import java.util.UUID

object DemoAccounts {
    const val ADMIN_EMAIL = "admin@adoptme.local"
    const val ADMIN_PASSWORD = "Admin12345"
    const val ADMIN_NAME = "Администратор"
}

object SeedData {
    fun shelters(): List<ShelterEntity> = listOf(
        ShelterEntity(
            id = "s-msk",
            name = "Добрые лапы",
            city = "Москва",
            description = "Демонстрационная карточка городского приюта. Здесь можно посмотреть, как в каталоге выглядит приют, и написать в локальный чат.",
            phone = "+7 000 000-00-01",
            email = "msk@demo.adoptme",
            hours = "10:00–18:00",
            addressNote = "Точный адрес и координаты не опубликованы: приложение не подставляет вымышленную точку вместо реального приюта.",
            latitude = null,
            longitude = null
        ),
        ShelterEntity(
            id = "s-spb",
            name = "Невский дом",
            city = "Санкт-Петербург",
            description = "Демонстрационная карточка приюта для кошек и собак. Запись на знакомство уточняется у сотрудника.",
            phone = "+7 000 000-00-02",
            email = "spb@demo.adoptme",
            hours = "11:00–19:00",
            addressNote = "Адрес выдаётся после подтверждения визита. На карте метка не ставится.",
            latitude = null,
            longitude = null
        ),
        ShelterEntity(
            id = "s-kzn",
            name = "Казанский двор",
            city = "Казань",
            description = "Небольшой демонстрационный приют. В карточке есть часы работы и контакт, без геопозиции.",
            phone = "+7 000 000-00-03",
            email = "kzn@demo.adoptme",
            hours = "09:00–17:00",
            addressNote = "Координаты намеренно пустые.",
            latitude = null,
            longitude = null
        ),
        ShelterEntity(
            id = "s-nsk",
            name = "Обской берег",
            city = "Новосибирск",
            description = "Демонстрационный приют для крупных и маленьких собак. Список работает без интернета.",
            phone = "+7 000 000-00-04",
            email = "nsk@demo.adoptme",
            hours = "10:00–16:00",
            addressNote = "Это не почтовый адрес. Уточняйте визит по демо-телефону карточки.",
            latitude = null,
            longitude = null
        )
    )

    fun animals(): List<AnimalEntity> = listOf(
        animal(
            id = "sharik",
            name = "Шарик",
            species = "DOG",
            breed = "Дворняжка",
            ageMonths = 24,
            ageLabel = "2 года",
            gender = "MALE",
            size = "MEDIUM",
            temperament = "Дружелюбный, любит людей и спокойные прогулки",
            story = "Шарик жил во дворе и сам подошёл к волонтёрам. Он уже умеет ходить на поводке, не боится города и ищет дом, где его будут ждать вечером.",
            shelterId = "s-msk",
            city = "Москва",
            imageKey = "animal_sharik"
        ),
        animal(
            id = "tosha",
            name = "Тоша",
            species = "CAT",
            breed = "Европейская короткошёрстная",
            ageMonths = 8,
            ageLabel = "8 месяцев",
            gender = "MALE",
            size = "SMALL",
            temperament = "Любопытный котёнок, мурлычет на руках",
            story = "Тошу нашли у подъезда совсем маленьким. Сейчас он приучен к лотку, играет с бантиками и спокойно остаётся один на несколько часов.",
            shelterId = "s-spb",
            city = "Санкт-Петербург",
            imageKey = "animal_tosha"
        ),
        animal(
            id = "bella",
            name = "Белла",
            species = "DOG",
            breed = "Бигль",
            ageMonths = 48,
            ageLabel = "4 года",
            gender = "FEMALE",
            size = "MEDIUM",
            temperament = "Ласковая, немного пугливая в новых местах",
            story = "Белла попала в приют после переезда прежней семьи. Она любит нюхать следы и лучше чувствует себя в тихом доме без резкого шума.",
            shelterId = "s-kzn",
            city = "Казань",
            imageKey = "animal_bella"
        ),
        animal(
            id = "barsik",
            name = "Барсик",
            species = "CAT",
            breed = "Полосатый домашний кот",
            ageMonths = 36,
            ageLabel = "3 года",
            gender = "MALE",
            size = "MEDIUM",
            temperament = "Самостоятельный, но приходит за вниманием вечером",
            story = "Барсик привык жить в квартире. Он не портит мебель, уживается с другими спокойными кошками и выбирает самое солнечное место на подоконнике.",
            shelterId = "s-msk",
            city = "Москва",
            imageKey = "animal_barsik"
        ),
        animal(
            id = "bobik",
            name = "Бобик",
            species = "DOG",
            breed = "Спаниель",
            ageMonths = 14,
            ageLabel = "1 год",
            gender = "MALE",
            size = "SMALL",
            temperament = "Игривый, быстро учится командам",
            story = "Бобик вырос у волонтёров. Он знает «сидеть» и «ко мне», обожает мягкие игрушки и подойдёт семье, которая готова гулять каждый день.",
            shelterId = "s-nsk",
            city = "Новосибирск",
            imageKey = "animal_bobik"
        ),
        animal(
            id = "druzhok",
            name = "Дружок",
            species = "DOG",
            breed = "Корги",
            ageMonths = 60,
            ageLabel = "5 лет",
            gender = "MALE",
            size = "SMALL",
            temperament = "Весёлый, преданный, любит детей постарше",
            story = "Дружок остался без хозяина, когда семья не смогла забрать его в другой город. Он аккуратный дома и с радостью встречает у двери.",
            shelterId = "s-msk",
            city = "Москва",
            imageKey = "animal_druzhok"
        ),
        animal(
            id = "reks",
            name = "Рекс",
            species = "DOG",
            breed = "Немецкая овчарка",
            ageMonths = 96,
            ageLabel = "8 лет",
            gender = "MALE",
            size = "LARGE",
            temperament = "Спокойный сторож, нуждается в мягком режиме",
            story = "Рекс много лет жил при сторожке и хорошо знает команды. Ему нужен опытный человек, неспешные прогулки и место, где можно лежать рядом.",
            shelterId = "s-kzn",
            city = "Казань",
            imageKey = "animal_reks"
        ),
        animal(
            id = "max",
            name = "Макс",
            species = "DOG",
            breed = "Лабрадор",
            ageMonths = 30,
            ageLabel = "2,5 года",
            gender = "MALE",
            size = "LARGE",
            temperament = "Энергичный, добрый к детям и другим собакам",
            story = "Макса отдали в приют щенком. Он обожает воду и апортировку, уже привит в рамках демонстрационной анкеты и ждёт семью, которой нравятся долгие прогулки.",
            shelterId = "s-spb",
            city = "Санкт-Петербург",
            imageKey = "animal_max"
        )
    )

    fun news(now: Long = System.currentTimeMillis()): List<NewsEntity> {
        val day = 24L * 60L * 60L * 1000L
        return listOf(
            NewsEntity(
                id = "n1",
                title = "Как подготовить дом к котёнку",
                category = "Советы по уходу",
                summary = "Лоток, укрытие и первые дни без гостей.",
                body = "Закройте провода, поставьте лоток в тихом месте и не зовите гостей в первую неделю. Котёнок осваивает дом постепенно: сначала одну комнату, потом остальные. Свежая вода и миска лучше, чем молоко.",
                publishedAt = now - day
            ),
            NewsEntity(
                id = "n2",
                title = "Шарик нашёл семью — история из демо-каталога",
                category = "Успешное усыновление",
                summary = "Пример того, как в приложении выглядит история счастливого дома.",
                body = "Эта заметка демонстрационная. В рабочей версии сюда попадают реальные истории приютов. Семья сначала приходила на прогулку, потом заполнила заявку и забрала собаку после проверки условий.",
                publishedAt = now - 2 * day
            ),
            NewsEntity(
                id = "n3",
                title = "Прогулка зимой: короче, но каждый день",
                category = "Советы по уходу",
                summary = "Лапы, тёплая подстилка и вода после улицы.",
                body = "Собаке нужна ежедневная прогулка даже в мороз, но время лучше сократить. После улицы осмотрите подушечки лап и дайте попить. Кошкам зимой достаточно тёплого лежака у батареи, без сквозняка.",
                publishedAt = now - 3 * day
            ),
            NewsEntity(
                id = "n4",
                title = "Приютам нужны волонтёры на выходные",
                category = "Новости",
                summary = "Помощь с выгулом и фотографиями животных.",
                body = "В демонстрационном режиме запись к волонтёрам не отправляется на сервер. На экране «Помощь» собраны способы поддержать приют: время, корм, лежаки и распространение анкет.",
                publishedAt = now - 5 * day
            )
        )
    }

    fun adminUser(now: Long = System.currentTimeMillis()): UserEntity {
        val salt = PasswordHasher.newSalt()
        return UserEntity(
            id = "local-admin",
            name = DemoAccounts.ADMIN_NAME,
            email = DemoAccounts.ADMIN_EMAIL,
            passwordHash = PasswordHasher.hash(DemoAccounts.ADMIN_PASSWORD, salt),
            passwordSalt = salt,
            phone = "",
            city = "",
            role = UserRole.LOCAL_ADMIN.name,
            createdAt = now
        )
    }

    fun newUserId(): String = UUID.randomUUID().toString()

    private fun animal(
        id: String,
        name: String,
        species: String,
        breed: String,
        ageMonths: Int,
        ageLabel: String,
        gender: String,
        size: String,
        temperament: String,
        story: String,
        shelterId: String,
        city: String,
        imageKey: String
    ) = AnimalEntity(
        id = id,
        name = name,
        species = species,
        breed = breed,
        ageMonths = ageMonths,
        ageLabel = ageLabel,
        ageGroup = ageGroupOf(ageMonths).name,
        gender = gender,
        size = size,
        temperament = temperament,
        story = story,
        shelterId = shelterId,
        city = city,
        imageKey = imageKey,
        status = "AVAILABLE"
    )
}

class DatabaseSeeder(private val database: AdoptMeDatabase) {
    suspend fun seed() {
        val shelters = database.shelterDao()
        if (shelters.count() == 0) {
            shelters.insertAll(SeedData.shelters())
        }
        val animals = database.animalDao()
        if (animals.count() == 0) {
            animals.insertAll(SeedData.animals())
        }
        val news = database.newsDao()
        if (news.count() == 0) {
            news.insertAll(SeedData.news())
        }
        val users = database.userDao()
        if (users.findByEmail(DemoAccounts.ADMIN_EMAIL) == null) {
            users.insert(SeedData.adminUser())
        }
    }
}
