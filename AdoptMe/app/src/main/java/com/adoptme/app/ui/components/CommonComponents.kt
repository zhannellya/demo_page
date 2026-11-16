package com.adoptme.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import com.adoptme.app.R
import com.adoptme.app.data.model.Animal
import com.adoptme.app.data.model.ApplicationStatus
import com.adoptme.app.data.model.Availability
import com.adoptme.app.data.model.HealthStatus
import com.adoptme.app.utils.label
import com.adoptme.app.ui.theme.ButtonShape
import com.adoptme.app.ui.theme.CardShape
import com.adoptme.app.ui.theme.ChipShape
import com.adoptme.app.ui.theme.CyanNeon
import com.adoptme.app.ui.theme.DividerSoft
import com.adoptme.app.ui.theme.ErrorRose
import com.adoptme.app.ui.theme.GlowCyan
import com.adoptme.app.ui.theme.GlowPink
import com.adoptme.app.ui.theme.GlowPurple
import com.adoptme.app.ui.theme.LavenderBg
import com.adoptme.app.ui.theme.LavenderMist
import com.adoptme.app.ui.theme.LilacCard
import com.adoptme.app.ui.theme.PinkNeon
import com.adoptme.app.ui.theme.PurpleGradient
import com.adoptme.app.ui.theme.PurplePrimary
import com.adoptme.app.ui.theme.SearchShape
import com.adoptme.app.ui.theme.SoftBgGradient
import com.adoptme.app.ui.theme.SuccessGreen
import com.adoptme.app.ui.theme.TextMuted
import com.adoptme.app.ui.theme.TextPrimary
import com.adoptme.app.ui.theme.TextSecondary
import com.adoptme.app.ui.theme.WarningAmber
import com.adoptme.app.ui.theme.White

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(12.dp, ButtonShape, ambientColor = PurplePrimary.copy(0.35f), spotColor = PinkNeon.copy(0.4f)),
        shape = ButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = White,
            disabledContentColor = White.copy(0.7f)
        ),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (enabled) Modifier.background(PurpleGradient, ButtonShape)
                    else Modifier.background(LavenderMist, ButtonShape)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                CircularProgressIndicator(color = White, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
            } else {
                Text(text = text, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun AdoptTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    supportingText: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = singleLine,
        isError = isError,
        supportingText = supportingText?.let { { Text(it, color = ErrorRose) } },
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = TextMuted
                    )
                }
            }
        } else null,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PurplePrimary,
            unfocusedBorderColor = DividerSoft,
            focusedContainerColor = White,
            unfocusedContainerColor = White,
            cursorColor = PurplePrimary,
            focusedLabelColor = PurplePrimary,
            errorBorderColor = ErrorRose
        )
    )
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Поиск по кличке"
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text(placeholder, color = TextMuted) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = PurplePrimary)
        },
        shape = SearchShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyanNeon.copy(0.7f),
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = White,
            unfocusedContainerColor = White,
            cursorColor = PurplePrimary
        )
    )
}

@Composable
fun ScreenHeader(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = TextPrimary)
            }
        } else {
            Spacer(Modifier.width(12.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.weight(1f)
        )
        actions()
    }
}

@Composable
fun PageIndicator(pageCount: Int, currentPage: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val selected = index == currentPage
            Box(
                Modifier
                    .height(8.dp)
                    .width(if (selected) 28.dp else 8.dp)
                    .clip(CircleShape)
                    .then(
                        if (selected) Modifier.background(PurpleGradient, CircleShape)
                        else Modifier.background(LavenderMist, CircleShape)
                    )
            )
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    selected: Boolean,
    accent: Color = PurplePrimary,
    onClick: () -> Unit
) {
    val bg = if (selected) accent else White
    val fg = if (selected) White else TextSecondary
    Box(
        modifier = Modifier
            .shadow(if (selected) 8.dp else 2.dp, ChipShape, ambientColor = accent.copy(0.25f))
            .clip(ChipShape)
            .background(bg)
            .border(1.dp, if (selected) Color.Transparent else DividerSoft, ChipShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(label, color = fg, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun HealthBadge(status: HealthStatus) {
    val (label, color) = when (status) {
        HealthStatus.HEALTHY -> status.label() to SuccessGreen
        HealthStatus.RECOVERING -> status.label() to WarningAmber
        HealthStatus.NEEDS_CARE -> status.label() to ErrorRose
    }
    Box(
        Modifier
            .clip(ChipShape)
            .background(color.copy(0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, color = color, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun AnimalCard(
    animal: Animal,
    onClick: () -> Unit,
    onFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    imageHeight: Dp = 150.dp
) {
    Box(modifier = modifier.clickable(onClick = onClick)) {
        AdoptMeCard {
            Column {
                Box {
                    AsyncImage(
                        model = animal.imageUrl.takeIf { it.isNotBlank() },
                        contentDescription = animal.name,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_paw),
                        error = painterResource(R.drawable.ic_paw),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(imageHeight)
                            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                            .background(LavenderMist)
                    )
                    IconButton(
                        onClick = onFavorite,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(40.dp)
                            .background(White.copy(alpha = 0.92f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (animal.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Избранное",
                            tint = if (animal.isFavorite) PinkNeon else TextMuted
                        )
                    }
                }
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = animal.name,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = animal.breed + " · " + animal.ageLabel,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = animal.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HealthBadge(status = animal.healthStatus)
                    if (animal.availability != Availability.AVAILABLE) {
                        Text(
                            text = animal.availability.label(),
                            color = PurplePrimary,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(title: String, subtitle: String, actionLabel: String? = null, onAction: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(16.dp))
            TextButton(onClick = onAction) {
                Text(actionLabel, color = PurplePrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun DemoBanner(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LavenderMist)
            .padding(12.dp),
        color = TextPrimary,
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
fun StatusChip(status: ApplicationStatus) {
    val color = when (status) {
        ApplicationStatus.PENDING -> WarningAmber
        ApplicationStatus.APPROVED -> SuccessGreen
        ApplicationStatus.REJECTED -> ErrorRose
        ApplicationStatus.CANCELLED -> TextMuted
    }
    Box(
        Modifier
            .clip(ChipShape)
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            status.label(),
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SectionTitle(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
        if (action != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(action, color = PurplePrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
