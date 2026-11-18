package com.adoptme.app.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddComment
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.adoptme.app.R
import com.adoptme.app.data.model.AdoptMeAiChat
import com.adoptme.app.data.model.ChatContact
import com.adoptme.app.data.model.ChatConversation
import com.adoptme.app.data.model.ChatParticipantType
import com.adoptme.app.di.LocalAppContainer
import com.adoptme.app.ui.components.AdoptMeCard
import com.adoptme.app.ui.components.EmptyState
import com.adoptme.app.ui.components.ScreenHeader
import com.adoptme.app.ui.components.SoftBackground
import com.adoptme.app.ui.theme.LilacCard
import com.adoptme.app.ui.theme.OnGradient
import com.adoptme.app.ui.theme.PinkNeon
import com.adoptme.app.ui.theme.Plum
import com.adoptme.app.ui.theme.PurpleGradient
import com.adoptme.app.ui.theme.TextMuted
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.ui.theme.White
import com.adoptme.app.viewmodel.AdoptMeViewModelFactory
import com.adoptme.app.viewmodel.ChatsViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    onOpenConversation: (participantId: String, type: ChatParticipantType, name: String) -> Unit,
    onOpenAi: () -> Unit
) {
    val container = LocalAppContainer.current
    val vm: ChatsViewModel = viewModel(factory = AdoptMeViewModelFactory(container))
    val state by vm.ui.collectAsState()
    var showNewChat by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    SoftBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(
                title = "Чаты",
                actions = {
                    IconButton(onClick = { showNewChat = true }) {
                        Icon(
                            Icons.Outlined.AddComment,
                            contentDescription = "Новое сообщение",
                            tint = OnGradient
                        )
                    }
                }
            )
            when {
                state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = White)
                }
                state.conversations.isEmpty() -> EmptyState(
                    title = "Пока нет диалогов",
                    subtitle = "Напишите приюту или владельцу — чат появится здесь. ИИ-помощник откроется автоматически."
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.conversations, key = { it.id }) { conversation ->
                        ChatRow(
                            conversation = conversation,
                            onClick = {
                                if (conversation.participantType == ChatParticipantType.AI) onOpenAi()
                                else onOpenConversation(
                                    conversation.participantId,
                                    conversation.participantType,
                                    conversation.participantName
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (showNewChat) {
        ModalBottomSheet(
            onDismissRequest = { showNewChat = false },
            sheetState = sheetState,
            containerColor = LilacCard
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Text("Новый чат", style = MaterialTheme.typography.headlineSmall, color = Plum)
                Spacer(Modifier.height(4.dp))
                Text("Выберите приют или владельца", color = TextSecondary)
                Spacer(Modifier.height(12.dp))
                if (state.contacts.isEmpty()) {
                    EmptyState("Пока не с кем написать", "Откройте анкету животного или страницу приюта.")
                } else {
                    state.contacts.forEach { contact ->
                        ContactRow(contact) {
                            scope.launch {
                                vm.startChat(contact)
                                showNewChat = false
                                onOpenConversation(contact.participantId, contact.participantType, contact.name)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ChatRow(conversation: ChatConversation, onClick: () -> Unit) {
    val unread = conversation.unreadCount > 0
    val isAi = conversation.participantType == ChatParticipantType.AI
    AdoptMeCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChatAvatar(conversation)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = conversation.participantName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Plum,
                    fontWeight = if (unread) FontWeight.Bold else FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = when {
                        conversation.lastMessage.isNotBlank() -> conversation.lastMessage
                        isAi -> AdoptMeAiChat.SUBTITLE
                        else -> "Нет сообщений"
                    },
                    color = if (unread) Plum else TextSecondary,
                    fontWeight = if (unread) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    formatChatTime(conversation.lastMessageAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (unread) PinkNeon else TextMuted
                )
                if (unread) {
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(PinkNeon),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (conversation.unreadCount > 9) "9+" else conversation.unreadCount.toString(),
                            color = White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatAvatar(conversation: ChatConversation) {
    val isAi = conversation.participantType == ChatParticipantType.AI
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .then(
                if (isAi) Modifier.background(PurpleGradient)
                else Modifier.background(Plum.copy(0.12f))
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isAi) {
            Icon(Icons.Outlined.SmartToy, contentDescription = null, tint = White, modifier = Modifier.size(28.dp))
        } else if (conversation.avatarUri.isNotBlank()) {
            AsyncImage(
                model = conversation.avatarUri,
                contentDescription = conversation.participantName,
                placeholder = painterResource(R.drawable.ic_paw),
                error = painterResource(R.drawable.ic_paw),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                conversation.participantName.firstOrNull()?.uppercaseChar()?.toString() ?: "A",
                color = Plum,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
private fun ContactRow(contact: ChatContact, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Plum.copy(0.12f)),
            contentAlignment = Alignment.Center
        ) {
            if (contact.avatarUri.isNotBlank()) {
                AsyncImage(
                    model = contact.avatarUri,
                    contentDescription = contact.name,
                    placeholder = painterResource(R.drawable.ic_paw),
                    error = painterResource(R.drawable.ic_paw),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(contact.name.firstOrNull()?.uppercaseChar()?.toString() ?: "A", color = Plum)
            }
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(contact.name, style = MaterialTheme.typography.titleMedium, color = Plum)
            if (contact.subtitle.isNotBlank()) {
                Text(contact.subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}

private fun formatChatTime(millis: Long): String {
    if (millis <= 0L) return ""
    val locale = Locale("ru")
    val now = Calendar.getInstance()
    val then = Calendar.getInstance().apply { timeInMillis = millis }
    return if (now.get(Calendar.YEAR) == then.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) == then.get(Calendar.DAY_OF_YEAR)
    ) {
        SimpleDateFormat("HH:mm", locale).format(Date(millis))
    } else {
        SimpleDateFormat("d MMM", locale).format(Date(millis))
    }
}
