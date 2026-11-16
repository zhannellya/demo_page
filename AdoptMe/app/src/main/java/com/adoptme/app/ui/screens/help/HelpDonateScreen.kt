package com.adoptme.app.ui.screens.help

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.DemoBanner
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary

@Composable
fun HelpDonateScreen(onBack: () -> Unit) {
    SoftBackground {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ScreenHeader("Помощь", onBack)
            Column(Modifier.padding(horizontal = 16.dp)) {
                DemoBanner("Раздел информационный. Платёжный сервис не подключён, деньги из приложения не списываются.")
                Spacer(Modifier.height(12.dp))
                HelpCard(
                    "Волонтёрство",
                    "В демонстрационном сценарии волонтёр выгуливает собак, меняет воду и помогает на знакомстве. Настоящую смену можно согласовать только с реальным приютом вне этого приложения."
                )
                HelpCard(
                    "Поддержка приюта",
                    "Приютам обычно нужны корм, пелёнки, когтеточки и спокойные руки. Уточняйте потребность напрямую: карточки ADOPTME не принимают заказы и не резервируют вещи."
                )
                HelpCard(
                    "Пожертвования",
                    "Оплата не подключена, потому что нет платёжного провайдера. Не вводите номер карты в этом приложении. Если решите помочь живому приюту, используйте его официальный способ перевода и проверяйте получателя сами."
                )
                HelpCard(
                    "Контакты",
                    "Напишите в раздел «Чаты». Сообщение останется на устройстве. Телефон и улица демонстрационных приютов намеренно не указаны, чтобы пример нельзя было принять за реальную организацию."
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun HelpCard(title: String, body: String) {
    AdoptMeCard(Modifier.padding(bottom = 12.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = PurplePrimary)
            Spacer(Modifier.height(6.dp))
            Text(body, color = TextSecondary)
        }
    }
}
