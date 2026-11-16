package com.adoptme.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptTextField
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.Blush
import com.adoptme.app.ui.theme.ErrorRose
import com.adoptme.app.ui.theme.LilacCard
import com.adoptme.app.ui.theme.OnGradient
import com.adoptme.app.ui.theme.Plum
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.AuthViewModel

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
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Регистрация",
                style = MaterialTheme.typography.headlineLarge,
                color = OnGradient
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Создайте аккаунт и знакомьтесь с животными из приютов. Пароль хранится только в виде хеша.",
                style = MaterialTheme.typography.bodyLarge,
                color = Blush
            )
            Spacer(modifier = Modifier.height(24.dp))
            AuthCard {
                AdoptTextField(
                    value = state.name,
                    onValueChange = { vm.update { s -> s.copy(name = it, error = null) } },
                    label = "Имя",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(12.dp))
                AdoptTextField(
                    value = state.email,
                    onValueChange = { vm.update { s -> s.copy(email = it, error = null) } },
                    label = "Email",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(12.dp))
                AdoptTextField(
                    value = state.phone,
                    onValueChange = { vm.update { s -> s.copy(phone = it, error = null) } },
                    label = "Телефон",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(12.dp))
                AdoptTextField(
                    value = state.city,
                    onValueChange = { vm.update { s -> s.copy(city = it, error = null) } },
                    label = "Город",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(12.dp))
                AdoptTextField(
                    value = state.password,
                    onValueChange = { vm.update { s -> s.copy(password = it, error = null) } },
                    label = "Пароль",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(12.dp))
                AdoptTextField(
                    value = state.confirmPassword,
                    onValueChange = { vm.update { s -> s.copy(confirmPassword = it, error = null) } },
                    label = "Повторите пароль",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { vm.signUp() })
                )
                AuthError(state.error)
                Spacer(modifier = Modifier.height(20.dp))
                GradientButton(text = "Создать аккаунт", onClick = vm::signUp, loading = state.loading)
            }
            AuthSwitchRow(prompt = "Уже есть аккаунт?", action = "Войти", onClick = onGoLogin)
        }
    }
}

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
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Вход",
                style = MaterialTheme.typography.headlineLarge,
                color = OnGradient
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Войдите, чтобы открыть избранное и свои заявки.",
                style = MaterialTheme.typography.bodyLarge,
                color = Blush
            )
            Spacer(modifier = Modifier.height(24.dp))
            AuthCard {
                AdoptTextField(
                    value = state.email,
                    onValueChange = { vm.update { s -> s.copy(email = it, error = null) } },
                    label = "Email",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(12.dp))
                AdoptTextField(
                    value = state.password,
                    onValueChange = { vm.update { s -> s.copy(password = it, error = null) } },
                    label = "Пароль",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { vm.login() })
                )
                AuthError(state.error)
                Spacer(modifier = Modifier.height(20.dp))
                GradientButton(text = "Войти", onClick = vm::login, loading = state.loading)
            }
            AuthSwitchRow(prompt = "Нет аккаунта?", action = "Регистрация", onClick = onGoRegister)
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

@Composable
private fun AuthError(message: String?) {
    if (message != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message, color = ErrorRose, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun AuthSwitchRow(prompt: String, action: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = prompt, color = Plum)
        TextButton(onClick = onClick) {
            Text(text = action, color = Plum, fontWeight = FontWeight.Bold)
        }
    }
}
