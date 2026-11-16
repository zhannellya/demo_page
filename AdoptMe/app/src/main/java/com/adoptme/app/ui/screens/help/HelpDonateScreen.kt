package com.adoptme.app.ui.screens.help

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.TextSecondary

@Composable
fun HelpScreen(onBack: () -> Unit) {
    SoftBackground {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            ScreenHeader("Помощь", onBack = onBack)
            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                HelpCard(
                    "Волонтёрство",
                    "Можно гулять с собаками, фотографировать анкеты и помогать на передержке. В демонстрационном режиме заявка волонтёра не отправляется: договоритесь с приютом в разделе «Чаты»."
                )
                HelpCard(
                    "Как помочь приюту",
                    "Привезите корм, пелёнки, миски и лежаки. Расскажите об анкете друзьям. Денежные платежи в приложении не подключены и не списываются."
                )
                HelpCard(
                    "Контакты",
                    "Демонстрационная поддержка: help@demo.adoptme\nТелефон карточек приютов начинается с +7 000 и не является реальным номером.\nРежим работы смотрите в карточке выбранного приюта."
                )
            }
        }
    }
}

@Composable
private fun HelpCard(title: String, body: String) {
    AdoptMeCard {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(body, color = TextSecondary)
        }
    }
}
