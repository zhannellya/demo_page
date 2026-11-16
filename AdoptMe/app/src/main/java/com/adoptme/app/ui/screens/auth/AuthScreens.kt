package com.adoptme.app.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.R
import com.adoptme.app.data.local.DemoAccounts
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptTextField
import com.adoptme.app.ui.components.DemoBanner
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.ErrorRose
import com.adoptme.app.ui.theme.LilacCard
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    onGoRegister: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: AuthViewModel = viewModel(key = "login", factory = AdoptMeViewModelFactory(container))
    val state by vm.ui.collectAsState()

    LaunchedEffect(state.success) {
        if (state.success) onLoggedIn()
    }

    SoftBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Image(
                painter = painterResource(R.drawable.adoptme_logo),
                contentDescription = "Логотип ADOPTME",
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.height(16.dp))
            Text("ADOPTME", style = MaterialTheme.typography.headlineLarge, color = PurplePrimary)
            Text("Приюты животных", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
            Spacer(Modifier.height(20.dp))
            DemoBanner(
                "Демонстрационный вход хранится только на этом устройстве. Пароль сохраняется в виде хеша, а не открытым текстом. " +
                    "Администратор для проверки заявок: ${DemoAccounts.ADMIN_EMAIL} / ${DemoAccounts.ADMIN_PASSWORD}. ${DemoAccounts.NOTICE}"
            )
            Spacer(Modifier.height(16.dp))
            AuthCard {
                AdoptTextField(
                    value = state.email,
                    onValueChange = { vm.update { form -> form.copy(email = it, error = null) } },
                    label = "Электронная почта",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                )
                Spacer(Modifier.height(12.dp))
                AdoptTextField(
                    value = state.password,
                    onValueChange = { vm.update { form -> form.copy(password = it, error = null) } },
                    label = "Пароль",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
                state.error?.let { message ->
                    Spacer(Modifier.height(8.dp))
                    Text(message, color = ErrorRose, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(20.dp))
                GradientButton(text = "Продолжить", onClick = vm::login, loading = state.loading)
                TextButton(
                    onClick = {
                        vm.update {
                            it.copy(
                                email = DemoAccounts.ADMIN_EMAIL,
                                password = DemoAccounts.ADMIN_PASSWORD,
                                error = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Заполнить данные администратора", color = PurplePrimary)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Нет учётной записи?", color = TextSecondary)
                TextButton(onClick = onGoRegister) {
                    Text("Регистрация", color = PurplePrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onGoLogin: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: AuthViewModel = viewModel(key = "register", factory = AdoptMeViewModelFactory(container))
    val state by vm.ui.collectAsState()

    LaunchedEffect(state.success) {
        if (state.success) onRegistered()
    }

    SoftBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text("Регистрация", style = MaterialTheme.typography.headlineLarge, color = PurplePrimary)
            Spacer(Modifier.height(8.dp))
            Text(
                "Создайте локальную учётную запись. Это демонстрация без сервера: права администратора так получить нельзя.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(Modifier.height(20.dp))
            AuthCard {
                AdoptTextField(
                    value = state.name,
                    onValueChange = { vm.update { form -> form.copy(name = it, error = null) } },
                    label = "Имя",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(Modifier.height(12.dp))
                AdoptTextField(
                    value = state.email,
                    onValueChange = { vm.update { form -> form.copy(email = it, error = null) } },
                    label = "Электронная почта",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                )
                Spacer(Modifier.height(12.dp))
                AdoptTextField(
                    value = state.password,
                    onValueChange = { vm.update { form -> form.copy(password = it, error = null) } },
                    label = "Пароль",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(Modifier.height(12.dp))
                AdoptTextField(
                    value = state.confirmPassword,
                    onValueChange = { vm.update { form -> form.copy(confirmPassword = it, error = null) } },
                    label = "Подтверждение пароля",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
                state.error?.let { message ->
                    Spacer(Modifier.height(8.dp))
                    Text(message, color = ErrorRose, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(20.dp))
                GradientButton(text = "Создать аккаунт", onClick = vm::signUp, loading = state.loading)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Уже есть аккаунт?", color = TextSecondary)
                TextButton(onClick = onGoLogin) {
                    Text("Войти", color = PurplePrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun AuthCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(LilacCard)
            .padding(20.dp)
    ) {
        content()
    }
}
