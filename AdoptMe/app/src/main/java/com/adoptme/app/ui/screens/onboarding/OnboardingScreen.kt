package com.adoptme.app.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adoptme.app.R
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.PageIndicator
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.ui.theme.White
import kotlinx.coroutines.launch

private data class WelcomePage(val title: String, val description: String, val imageRes: Int)

private val pages = listOf(
    WelcomePage(
        title = "Рядом кто-то ждёт",
        description = "ADOPTME помогает найти кошку или собаку из приюта и познакомиться с её историей.",
        imageRes = R.drawable.onboarding_cat
    ),
    WelcomePage(
        title = "Сначала узнайте друг друга",
        description = "В анкете есть возраст, порода, характер и приют. Заявку можно отправить прямо из карточки.",
        imageRes = R.drawable.onboarding_dog
    ),
    WelcomePage(
        title = "Дом начинается с заботы",
        description = "Сохраняйте избранное, следите за статусом заявки и пишите приюту в демонстрационном чате.",
        imageRes = R.drawable.onboarding_friends
    )
)

@Composable
fun WelcomeScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val container = LocalAppContainer.current
    val lastPage = pagerState.currentPage == pages.lastIndex

    SoftBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
            ) { page ->
                val item = pages[page]
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .shadow(16.dp, CircleShape)
                            .clip(CircleShape)
                            .background(White),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(item.imageRes),
                            contentDescription = item.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(Modifier.height(28.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
            PageIndicator(pageCount = pages.size, currentPage = pagerState.currentPage)
            Spacer(Modifier.height(20.dp))
            GradientButton(
                text = if (lastPage) "Начать" else "Далее",
                modifier = Modifier.widthIn(max = 560.dp),
                onClick = {
                    if (lastPage) {
                        scope.launch {
                            container.authRepository.completeOnboarding()
                            onFinished()
                        }
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                }
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}
