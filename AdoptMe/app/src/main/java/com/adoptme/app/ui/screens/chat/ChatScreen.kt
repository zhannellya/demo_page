package com.adoptme.app.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.adoptme.app.R
import com.adoptme.app.data.model.ChatParticipantType
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptTextField
import com.adoptme.app.ui.components.CategoryChip
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.ErrorRose
import com.adoptme.app.ui.theme.LilacCard
import com.adoptme.app.ui.theme.OnGradient
import com.adoptme.app.ui.theme.Plum
import com.adoptme.app.ui.theme.PurpleGradient
import com.adoptme.app.ui.theme.TextMuted
import com.adoptme.app.ui.theme.White
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConversationScreen(
    participantId: String,
    participantType: ChatParticipantType,
    participantName: String,
    onBack: () -> Unit
) = ChatScreen(participantId, participantType, participantName, onBack)

@Composable
fun ChatScreen(
    participantId: String,
    participantType: ChatParticipantType,
    participantName: String,
    onBack: () -> Unit,
    quickPrompts: List<String> = emptyList()
) {
    val container = LocalAppContainer.current
    val vm: ChatViewModel = viewModel(
        key = "chat_${participantType}_$participantId",
        factory = AdoptMeViewModelFactory(
            container = container,
            participantId = participantId,
            participantType = participantType.name,
            animalName = participantName
        )
    )
    val state by vm.ui.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    SoftBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(
                title = state.title.ifBlank { participantName.ifBlank { "Чат" } },
                onBack = onBack,
                actions = {
                    when {
                        participantType == ChatParticipantType.AI -> {
                            Box(
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PurpleGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.SmartToy, contentDescription = null, tint = White)
                            }
                        }
                        state.avatarUri.isNotBlank() -> {
                            AsyncImage(
                                model = state.avatarUri,
                                contentDescription = state.title,
                                placeholder = painterResource(R.drawable.ic_paw),
                                error = painterResource(R.drawable.ic_paw),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                            )
                        }
                        else -> Spacer(Modifier.width(8.dp))
                    }
                }
            )
            when {
                state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = White)
                }
                else -> {
                    if (state.messages.isEmpty()) {
                        Box(modifier = Modifier.weight(1f)) {
                            EmptyState(
                                title = "Начните переписку",
                                subtitle = "Сообщения сохраняются на устройстве и не пропадут после перезапуска."
                            )
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.messages, key = { it.id }) { message ->
                                val mine = message.isFromCurrentUser
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .widthIn(max = 280.dp)
                                            .clip(RoundedCornerShape(20.dp))
                                            .then(
                                                if (mine) Modifier.background(PurpleGradient)
                                                else Modifier.background(LilacCard)
                                            )
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        horizontalAlignment = if (mine) Alignment.End else Alignment.Start
                                    ) {
                                        Text(
                                            message.text,
                                            color = if (mine) White else Plum,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            formatTime(message.sentAt),
                                            color = if (mine) White.copy(0.8f) else TextMuted,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (quickPrompts.isNotEmpty()) {
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                quickPrompts.forEach { prompt ->
                                    CategoryChip(prompt, selected = false) {
                                        vm.setDraft(prompt)
                                        vm.send()
                                    }
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                        }
                        state.error?.let {
                            Text(it, color = ErrorRose, style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(6.dp))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.weight(1f)) {
                                AdoptTextField(
                                    value = state.draft,
                                    onValueChange = vm::setDraft,
                                    label = "Сообщение"
                                )
                            }
                            IconButton(
                                onClick = vm::send,
                                enabled = !state.sending && state.draft.isNotBlank()
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Отправить",
                                    tint = OnGradient
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    return SimpleDateFormat("HH:mm", Locale("ru")).format(Date(millis))
}
