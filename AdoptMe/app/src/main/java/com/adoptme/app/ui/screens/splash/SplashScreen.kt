package com.adoptme.app.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.R
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.PinkNeon
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.ui.theme.White
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.SplashViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onFinished: (onboardingDone: Boolean, loggedIn: Boolean) -> Unit
) {
    val container = LocalAppContainer.current
    val vm: SplashViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val gate by vm.gate.collectAsState()
    val scale = remember { Animatable(0.6f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        val animation = launch {
            scale.animateTo(1f, tween(700, easing = EaseOutBack))
            alpha.animateTo(1f, tween(500))
        }
        val readyGate = vm.gate.first { it.ready && it.error == null }
        animation.join()
        delay(350)
        onFinished(readyGate.onboardingDone, readyGate.loggedIn)
    }

    SoftBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .scale(scale.value)
                    .size(128.dp)
                    .shadow(20.dp, CircleShape, ambientColor = PurplePrimary.copy(0.35f), spotColor = PinkNeon.copy(0.35f))
                    .clip(CircleShape)
                    .background(White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.adoptme_logo),
                    contentDescription = "Логотип ADOPTME",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(28.dp))
            Text(
                text = "ADOPTME",
                modifier = Modifier.alpha(alpha.value),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = PurplePrimary
                )
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Приюты животных",
                modifier = Modifier.alpha(alpha.value),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            gate.error?.let { message ->
                Spacer(Modifier.height(24.dp))
                Text(message, color = PurplePrimary, textAlign = TextAlign.Center)
                Spacer(Modifier.height(16.dp))
                GradientButton(text = "Повторить", onClick = vm::retry)
            }
        }
    }
}
