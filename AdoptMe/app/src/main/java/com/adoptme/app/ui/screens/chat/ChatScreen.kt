package com.adoptme.app.ui.screens.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adoptme.app.data.model.MessageSender
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.AdoptTextField
import com.adoptme.app.ui.components.DemoBanner
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.LavenderMist
import com.adoptme.app.ui.theme.Plum
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.ui.theme.White
import com.adoptme.app.utils.formatDateTime
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.ChatListViewModel
import com.adoptme.app.viewmodel.ChatThreadViewModel
import androidx.compose.foundation.background

@Composable
fun ChatListScreen(onOpenThread: (String) -> Unit) {
    val container = LocalAppContainer.current
    val vm: ChatListViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val threads by vm.threads.collectAsState()

    SoftBackground {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader("Чаты")
            Column(Modifier.padding(horizontal = 16.dp)) {
                DemoBanner("Локальный демонстрационный чат. Сервер сообщений не подключён.")
            }
            LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(threads, key = { it.id }) { thread ->
                    AdoptMeCard(modifier = Modifier.clickable { onOpenThread(thread.id) }) {
                        Column(Modifier.padding(16.dp)) {
                            Text(thread.title, style = MaterialTheme.typography.titleLarge)
                            Text(thread.subtitle, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatThreadScreen(threadId: String, onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val vm: ChatThreadViewModel = viewModel(factory = AdoptMeViewModelFactory(container, threadId))
    val messages by vm.messages.collectAsState()
    val draft by vm.draft.collectAsState()
    val sending by vm.sending.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    SoftBackground {
        Column(Modifier.fillMaxSize().imePadding()) {
            ScreenHeader("Переписка", onBack = onBack)
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    val mine = message.sender == MessageSender.USER
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start) {
                        Column(
                            modifier = Modifier
                                .widthIn(max = 320.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (mine) Plum else LavenderMist)
                                .padding(12.dp)
                        ) {
                            Text(message.text, color = if (mine) White else TextSecondary)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                formatDateTime(message.sentAt),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (mine) White.copy(alpha = 0.8f) else TextSecondary
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AdoptTextField(
                    value = draft,
                    onValueChange = vm::setDraft,
                    label = "Сообщение",
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = vm::send, enabled = !sending && draft.isNotBlank()) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Отправить")
                }
            }
        }
    }
}
