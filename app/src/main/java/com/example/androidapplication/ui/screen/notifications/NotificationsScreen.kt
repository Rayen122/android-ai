package com.example.androidapplication.ui.screen.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.androidapplication.models.Notification
import com.example.androidapplication.models.NotificationViewModel
import com.example.androidapplication.ui.components.BackButton

@Composable
fun NotificationsScreen(
    navController: NavController,
    notificationViewModel: NotificationViewModel = viewModel()
) {
    val notifications by notificationViewModel.notifications.observeAsState(initial = emptyList())
    val isLoading by notificationViewModel.isLoading.observeAsState(initial = false)
    val unreadCount by notificationViewModel.unreadCount.observeAsState(initial = 0)
    
    var isHeaderVisible by remember { mutableStateOf(false) }

    // Load notifications on first appearance
    LaunchedEffect(Unit) {
        notificationViewModel.getMyNotifications()
        notificationViewModel.getUnreadCount()
        isHeaderVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF2D9),
                        Color(0xFFFFE6B3),
                        Color(0xFFFCD48A),
                        Color(0xFFF2C14F)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with animation
            AnimatedVisibility(
                visible = isHeaderVisible,
                enter = fadeIn(animationSpec = tween(400)) + 
                        slideInVertically(
                            initialOffsetY = { -it / 2 },
                            animationSpec = tween(400)
                        )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 50.dp, bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        BackButton(navController = navController)
                        Text(
                            text = "Notifications",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    if (unreadCount > 0) {
                        TextButton(
                            onClick = { notificationViewModel.markAllAsRead() }
                        ) {
                            Text(
                                text = "Tout marquer lu",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Notifications List
            Box(modifier = Modifier.weight(1f)) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucune notification",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 16.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(notifications) { index, notification ->
                            AnimatedNotificationItem(
                                notification = notification,
                                index = index,
                                onClick = {
                                    if (!notification.isRead) {
                                        notificationViewModel.markAsRead(notification.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedNotificationItem(
    notification: Notification,
    index: Int,
    onClick: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 50L)
        isVisible = true
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            animationSpec = tween(400)
        ) + slideInHorizontally(
            initialOffsetX = { it / 2 },
            animationSpec = tween(400)
        ),
        exit = fadeOut(animationSpec = tween(300)) + 
               slideOutHorizontally(
                   targetOffsetX = { -it },
                   animationSpec = tween(300)
               )
    ) {
        NotificationItem(
            notification = notification,
            onClick = onClick
        )
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(150)
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(),
                onClick = onClick
            )
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.08f),
                ambientColor = Color.Black.copy(alpha = 0.04f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Avatar/Icon on the left with animation
            var avatarVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                avatarVisible = true
            }
            
            AnimatedVisibility(
                visible = avatarVisible,
                enter = fadeIn(animationSpec = tween(300)) + 
                        scaleIn(initialScale = 0.5f, animationSpec = tween(300))
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color(0xFFFFE7BA), // Yellow background
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User",
                        tint = Color(0xFFFF9800), // Orange icon
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Notification content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // User name and unread indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.userName ?: "Utilisateur",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    Color(0xFFFF0000), // Red indicator
                                    shape = CircleShape
                                )
                        )
                    }
                }

                // Notification message
                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = Color.Black.copy(alpha = 0.7f),
                    lineHeight = 20.sp
                )

                // Date and time
                Text(
                    text = notification.dateTime ?: notification.createdAt ?: "",
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.5f)
                )
            }
        }
    }
}

