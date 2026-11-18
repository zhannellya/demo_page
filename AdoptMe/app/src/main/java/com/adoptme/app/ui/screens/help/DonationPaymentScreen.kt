package com.adoptme.app.ui.screens.help

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.data.model.PaymentMethod
import com.adoptme.app.data.model.formatKzt
import com.adoptme.app.data.model.titleRu
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.AdoptTextField
import com.adoptme.app.ui.components.CategoryChip
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SecondaryButton
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.ErrorRose
import com.adoptme.app.ui.theme.OnGradient
import com.adoptme.app.ui.theme.Plum
import com.adoptme.app.ui.theme.SuccessGreen
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.ui.theme.White
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.DonationPaymentViewModel

@Composable
fun DonationPaymentScreen(
    shelterId: String,
    purpose: String,
    amount: Int?,
    onBack: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: DonationPaymentViewModel = viewModel(
        key = "donate_${shelterId}_$purpose",
        factory = AdoptMeViewModelFactory(
            container = container,
            shelterId = shelterId,
            donationPurpose = purpose,
            donationAmount = amount
        )
    )
    val state by vm.ui.collectAsState()

    SoftBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(title = "Оплата помощи", onBack = onBack)
            when {
                state.paying -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = White)
                        Spacer(Modifier.height(16.dp))
                        Text("Оформляем тестовое пожертвование…", color = OnGradient)
                    }
                }
                state.success -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AdoptMeCard {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text("Спасибо!", style = MaterialTheme.typography.headlineMedium, color = Plum)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Пожертвование успешно оформлено",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "${state.purposeTitle} · ${formatKzt(state.receiptAmount ?: 0)}",
                                color = Plum,
                                style = MaterialTheme.typography.titleMedium
                            )
                            state.shelter?.let { Text(it.name, color = TextSecondary) }
                            Spacer(Modifier.height(18.dp))
                            GradientButton("Вернуться", onClick = onBack)
                        }
                    }
                }
                else -> Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    AdoptMeCard {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Цель пожертвования", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
                            Text(state.purposeTitle, style = MaterialTheme.typography.titleLarge, color = Plum)
                            Text("Приют", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
                            Text(state.shelter?.name ?: "Выберите приют", style = MaterialTheme.typography.titleMedium, color = Plum)
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                state.shelters.forEach { shelter ->
                                    CategoryChip(shelter.name, state.shelter?.id == shelter.id) {
                                        vm.selectShelter(shelter.id)
                                    }
                                }
                            }
                            AdoptTextField(
                                value = state.amountInput,
                                onValueChange = vm::setAmount,
                                label = "Сумма, ₸",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            Text("Способ оплаты", style = MaterialTheme.typography.titleSmall, color = Plum)
                            CategoryChip(
                                PaymentMethod.BANK_CARD.titleRu(),
                                selected = state.method == PaymentMethod.BANK_CARD
                            ) { vm.setMethod(PaymentMethod.BANK_CARD) }
                            Text(
                                "Это демонстрационный платёж. Номер карты, CVV и срок действия не запрашиваются и не сохраняются.",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                            state.error?.let { Text(it, color = ErrorRose) }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    GradientButton("Оплатить", onClick = vm::pay, loading = state.paying)
                    Spacer(Modifier.height(12.dp))
                    SecondaryButton("Отмена", onClick = onBack)
                    Spacer(Modifier.height(28.dp))
                }
            }
        }
    }
}
