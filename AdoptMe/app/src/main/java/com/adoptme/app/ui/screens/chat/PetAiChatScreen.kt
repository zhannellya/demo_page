package com.adoptme.app.ui.screens.chat

import androidx.compose.runtime.Composable
import com.adoptme.app.data.model.AdoptMeAiChat
import com.adoptme.app.data.model.ChatParticipantType

@Composable
fun PetAiChatScreen(onBack: () -> Unit) {
    ChatScreen(
        participantId = AdoptMeAiChat.PARTICIPANT_ID,
        participantType = ChatParticipantType.AI,
        participantName = AdoptMeAiChat.NAME,
        onBack = onBack,
        quickPrompts = listOf(
            "Чем кормить котёнка?",
            "Как подготовить дом к собаке?",
            "Когда делать прививки?",
            "Как помочь питомцу адаптироваться?"
        )
    )
}
