package com.adoptme.app.ui.screens.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.AdoptTextField
import com.adoptme.app.ui.components.DemoBanner
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.GradientButton
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.LavenderMist
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.ui.theme.White
import com.adoptme.app.utils.toRussianDateTime
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.ChatListViewModel
import com.adoptme.app.viewmodel.ChatThreadViewModel
import androidx.compose.foundation.background

@Composable
fun ChatListScreen(onBack: () -> Unit, onOpen: (String) -> Unit) {
    val container = LocalAppContainer.current
    val vm: ChatListViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val conversations by vm.conversations.collectAsState()

    SoftBackground {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader("Чаты", onBack)
            Column(Modifier.padding(horizontal = 16.dp)) {
                DemoBanner("Локальный демонстрационный режим. Сервер переписки не подключён, история хранится на устройстве.")
                Spacer(Modifier.height(12.dp))
            }
            if (conversations.isEmpty()) {
                EmptyState("Диалогов нет", "Когда база подготовится, здесь появится чат приюта.")
            } else {
                LazyColumn(Modifier.padding(horizontal = 16.dp)) {
                    items(conversations, key = { it.id }) { conversation ->
                        AdoptMeCard(
                            Modifier
                                .padding(bottom = 12.dp)
                                .clickable { onOpen(conversation.id) }
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(conversation.title, style = MaterialTheme.typography.titleLarge, color = PurplePrimary)
                                Text(conversation.subtitle, color = TextSecondary)
                                Spacer(Modifier.height(6.dp))
                                Text(conversation.lastMessage, color = TextSecondary, maxLines = 2)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatThreadScreen(conversationId: String, onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val vm: ChatThreadViewModel = viewModel(
        key = conversationId,
        factory = AdoptMeViewModelFactory(container, conversationId = conversationId)
    )
    val messages by vm.messages.collectAsState()
    val draft by vm.draft.collectAsState()
    val error by vm.error.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    SoftBackground {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader("Диалог", onBack)
            DemoBanner(
                "Сообщения не уходят в приют. Ответ ниже пишет само приложение.",
                Modifier.padding(horizontal = 16.dp)
            )
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (message.fromUser) Alignment.End else Alignment.Start
                    ) {
                        Text(
                            text = message.text,
                            color = if (message.fromUser) White else PurplePrimary,
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (message.fromUser) PurplePrimary else LavenderMist)
                                .padding(12.dp)
                        )
                        Text(
                            message.sentAt.toRussianDateTime(),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }
            Column(Modifier.padding(16.dp)) {
                error?.let { Text(it, color = com.adoptme.app.ui.theme.ErrorRose) }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AdoptTextField(
                        value = draft,
                        onValueChange = vm::setDraft,
                        label = "Сообщение",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(8.dp))
                GradientButton(text = "Отправить", onClick = vm::send, enabled = draft.isNotBlank())
            }
        }
    }
}
