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
import com.adoptme.app.ui.theme.ErrorRose
import com.adoptme.app.ui.theme.LilacCard
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    onSignedUp: () -> Unit,
    onGoLogin: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: AuthViewModel = viewModel(key = "signup", factory = AdoptMeViewModelFactory(container))
    val state by vm.ui.collectAsState()

    LaunchedEffect(state.success) {
        if (state.success) onSignedUp()
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
                text = "Create account",
                style = MaterialTheme.typography.headlineLarge,
                color = PurplePrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Join AdoptMe and meet your new best friend.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(28.dp))
                    .clip(RoundedCornerShape(28.dp))
                    .background(LilacCard)
                    .padding(20.dp)
            ) {
                AdoptTextField(
                    value = state.name,
                    onValueChange = { vm.update { s -> s.copy(name = it, error = null) } },
                    label = "Full name",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(12.dp))
                AdoptTextField(
                    value = state.email,
                    onValueChange = { vm.update { s -> s.copy(email = it, error = null) } },
                    label = "Email",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                AdoptTextField(
                    value = state.phone,
                    onValueChange = { vm.update { s -> s.copy(phone = it, error = null) } },
                    label = "Phone",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                AdoptTextField(
                    value = state.password,
                    onValueChange = { vm.update { s -> s.copy(password = it, error = null) } },
                    label = "Password",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(12.dp))
                AdoptTextField(
                    value = state.confirmPassword,
                    onValueChange = { vm.update { s -> s.copy(confirmPassword = it, error = null) } },
                    label = "Confirm password",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
                state.error?.let { message ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = message,
                        color = ErrorRose,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                GradientButton(
                    text = "Sign Up",
                    onClick = vm::signUp,
                    loading = state.loading
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Already have an account?", color = TextSecondary)
                TextButton(onClick = onGoLogin) {
                    Text(
                        text = "Log in",
                        color = PurplePrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    onGoSignUp: () -> Unit
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
                text = "Welcome back",
                style = MaterialTheme.typography.headlineLarge,
                color = PurplePrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Log in to continue your adoption journey.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(28.dp))
                    .clip(RoundedCornerShape(28.dp))
                    .background(LilacCard)
                    .padding(20.dp)
            ) {
                AdoptTextField(
                    value = state.email,
                    onValueChange = { vm.update { s -> s.copy(email = it, error = null) } },
                    label = "Email",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                AdoptTextField(
                    value = state.password,
                    onValueChange = { vm.update { s -> s.copy(password = it, error = null) } },
                    label = "Password",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
                state.error?.let { message ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = message,
                        color = ErrorRose,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                GradientButton(
                    text = "Log In",
                    onClick = vm::login,
                    loading = state.loading
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "New here?", color = TextSecondary)
                TextButton(onClick = onGoSignUp) {
                    Text(
                        text = "Sign up",
                        color = PurplePrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
