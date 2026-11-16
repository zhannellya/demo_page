package com.adoptme.app.ui.screens.welcome

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.adoptme.app.data.local.SampleData
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.PageIndicator
import com.adoptme.app.ui.components.SecondaryButton
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.Blush
import com.adoptme.app.ui.theme.LilacCard
import com.adoptme.app.ui.theme.OnGradient
import com.adoptme.app.ui.theme.Plum
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.ui.theme.White

@Composable
fun WelcomeScreen(
    onLogin: () -> Unit,
    onRegister: () -> Unit
) {
    val pages = SampleData.onboardingPages
    val pagerState = rememberPagerState(pageCount = { pages.size })

    SoftBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_paw),
                    contentDescription = null,
                    tint = Blush,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(Modifier.size(10.dp))
                Column {
                    Text(
                        text = "ADOPTME",
                        style = MaterialTheme.typography.headlineMedium,
                        color = OnGradient,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Приюты животных",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Blush
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                val item = pages[page]
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(item.imageRes),
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(230.dp)
                            .shadow(18.dp, RoundedCornerShape(36.dp))
                            .clip(RoundedCornerShape(36.dp))
                            .background(White)
                    )
                    Spacer(Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .background(LilacCard)
                            .padding(horizontal = 20.dp, vertical = 18.dp)
                    ) {
                        AnimatedContent(
                            targetState = page,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "welcomeText"
                        ) { index ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = pages[index].title,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Plum,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = pages[index].description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            PageIndicator(
                pageCount = pages.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )
            GradientButton(text = "Войти", onClick = onLogin)
            Spacer(Modifier.height(10.dp))
            SecondaryButton(text = "Создать аккаунт", onClick = onRegister)
            Spacer(Modifier.height(8.dp))
        }
    }
}
