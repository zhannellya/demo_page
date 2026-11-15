package com.adoptme.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.adoptme.app.data.local.SampleData
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.PageIndicator
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.LilacCard
import com.adoptme.app.ui.theme.PinkNeon
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val pages = SampleData.onboardingPages
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val container = LocalAppContainer.current

    SoftBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    scope.launch {
                        container.authRepository.completeOnboarding()
                        onFinished()
                    }
                }) {
                    Text("Skip", color = PurplePrimary, fontWeight = FontWeight.SemiBold)
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
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .shadow(
                                24.dp,
                                RoundedCornerShape(40.dp),
                                ambientColor = PurplePrimary.copy(0.25f),
                                spotColor = PinkNeon.copy(0.35f)
                            )
                            .clip(RoundedCornerShape(40.dp))
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

                    Spacer(Modifier.height(36.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(12.dp, RoundedCornerShape(32.dp), ambientColor = PurplePrimary.copy(0.12f))
                            .clip(RoundedCornerShape(32.dp))
                            .background(LilacCard)
                            .padding(horizontal = 24.dp, vertical = 28.dp)
                    ) {
                        AnimatedContent(
                            targetState = page,
                            transitionSpec = {
                                (fadeIn() + slideInHorizontally { it / 4 }) togetherWith
                                    (fadeOut() + slideOutHorizontally { -it / 4 })
                            },
                            label = "onboardingText"
                        ) { p ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = pages[p].title,
                                    style = MaterialTheme.typography.headlineLarge,
                                    textAlign = TextAlign.Center,
                                    color = PurplePrimary
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = pages[p].description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    color = TextSecondary
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
                    .padding(vertical = 20.dp)
            )

            GradientButton(
                text = if (pagerState.currentPage == pages.lastIndex) "Get Started" else "Next",
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage < pages.lastIndex) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            container.authRepository.completeOnboarding()
                            onFinished()
                        }
                    }
                }
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}
