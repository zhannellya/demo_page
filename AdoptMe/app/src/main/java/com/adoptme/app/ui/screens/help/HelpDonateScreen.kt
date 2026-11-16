package com.adoptme.app.ui.screens.help

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.Blush
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.ContentViewModel
import kotlinx.coroutines.launch

@Composable
fun HelpDonateScreen(onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val vm: ContentViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    SoftBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(title = "Помощь приютам", onBack = onBack)
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    Text(
                        "Каждая помощь кормит, лечит и приближает питомца к дому.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Blush
                    )
                    Spacer(Modifier.height(8.dp))
                }
                items(vm.donations, key = { it.id }) { option ->
                    AdoptMeCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(option.title, style = MaterialTheme.typography.titleLarge, color = PurplePrimary)
                            Spacer(Modifier.height(4.dp))
                            Text(option.description, color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            Text(option.amountLabel, style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.height(12.dp))
                            GradientButton(
                                text = if (option.icon == "volunteer") "Стать волонтёром" else "Помочь на ${option.amountLabel}",
                                onClick = {
                                    scope.launch {
                                        snackbar.showSnackbar(
                                            if (option.icon == "volunteer")
                                                "Спасибо! Приют свяжется с вами по поводу смен."
                                            else
                                                "Спасибо, что поддерживаете питомцев ADOPTME!"
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
            SnackbarHost(hostState = snackbar)
        }
    }
}
