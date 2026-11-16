package com.adoptme.app.ui.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptTextField
import com.adoptme.app.ui.components.DemoBanner
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.ErrorRose
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.AuthViewModel

@Composable
fun LoginScreen(onLoggedIn: () -> Unit, onRegister: () -> Unit) {
    val container = LocalAppContainer.current
    val vm: AuthViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val state by vm.ui.collectAsState()
    LaunchedEffect(state.success) { if (state.success) onLoggedIn() }

    AuthScaffold(
        title = "Вход",
        subtitle = "Демонстрационный режим: пароль проверяется локально и в базе хранится только его хеш."
    ) {
        AdoptTextField(
            value = state.email,
            onValueChange = { value -> vm.update { it.copy(email = value) } },
            label = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
        )
        Spacer(Modifier.height(12.dp))
        AdoptTextField(
            value = state.password,
            onValueChange = { value -> vm.update { it.copy(password = value) } },
            label = "Пароль",
            isPassword = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
        )
        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = ErrorRose)
        }
        Spacer(Modifier.height(20.dp))
        GradientButton(text = "Войти", onClick = vm::login, loading = state.loading)
        TextButton(onClick = onRegister, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Нет аккаунта? Зарегистрироваться", color = PurplePrimary, fontWeight = FontWeight.SemiBold)
        }
        Text(
            text = "Локальный администратор для проверки заявок: admin@adoptme.local / Admin12345. Это не серверная роль.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
fun RegisterScreen(onRegistered: () -> Unit, onLogin: () -> Unit) {
    val container = LocalAppContainer.current
    val vm: AuthViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val state by vm.ui.collectAsState()
    LaunchedEffect(state.success) { if (state.success) onRegistered() }

    AuthScaffold(
        title = "Регистрация",
        subtitle = "Аккаунт создаётся на устройстве. Серверная авторизация не подключена."
    ) {
        AdoptTextField(
            value = state.name,
            onValueChange = { value -> vm.update { it.copy(name = value) } },
            label = "Имя пользователя"
        )
        Spacer(Modifier.height(12.dp))
        AdoptTextField(
            value = state.email,
            onValueChange = { value -> vm.update { it.copy(email = value) } },
            label = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
        )
        Spacer(Modifier.height(12.dp))
        AdoptTextField(
            value = state.password,
            onValueChange = { value -> vm.update { it.copy(password = value) } },
            label = "Пароль",
            isPassword = true
        )
        Spacer(Modifier.height(12.dp))
        AdoptTextField(
            value = state.confirmPassword,
            onValueChange = { value -> vm.update { it.copy(confirmPassword = value) } },
            label = "Подтверждение пароля",
            isPassword = true
        )
        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = ErrorRose)
        }
        Spacer(Modifier.height(20.dp))
        GradientButton(text = "Создать аккаунт", onClick = vm::register, loading = state.loading)
        TextButton(onClick = onLogin, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Уже есть аккаунт? Войти", color = PurplePrimary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AuthScaffold(
    title: String,
    subtitle: String,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    SoftBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.widthIn(max = 520.dp).fillMaxWidth()) {
                Text(title, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Spacer(Modifier.height(16.dp))
                DemoBanner()
                Spacer(Modifier.height(16.dp))
                content()
            }
        }
    }
}
