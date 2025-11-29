package com.example.androidapplication.ui.screen.home

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import com.example.androidapplication.ui.components.BottomNavigationBar
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.androidapplication.models.NotificationViewModel
import com.example.androidapplication.models.Photo
import com.example.androidapplication.models.PhotoViewModel
import com.example.androidapplication.models.ProfileViewModel
import com.example.androidapplication.models.login.getRefreshToken
import com.example.androidapplication.models.logout.LogoutState
import com.example.androidapplication.models.logout.LogoutViewModel
import com.example.androidapplication.ui.container.NavGraph
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import com.example.androidapplication.ui.theme.PrimaryYellowLight
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import com.example.androidapplication.models.story.StoryViewModel
import com.example.androidapplication.models.story.StoryUserGroup
import com.example.androidapplication.ui.components.StoriesRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    photoViewModel: PhotoViewModel = viewModel(key = "shared_photo_viewmodel"),
    storyViewModel: StoryViewModel = viewModel(key = "shared_story_viewmodel")
) {
    val context = LocalContext.current
    val logoutViewModel = remember { LogoutViewModel() }
    val logoutState by logoutViewModel.logoutState.collectAsState()
    val notificationViewModel: NotificationViewModel = viewModel()
    val unreadCount by notificationViewModel.unreadCount.observeAsState(initial = 0)
    val profileViewModel: ProfileViewModel = viewModel()
    val userData by profileViewModel.userData.observeAsState()
    val storyGroups by storyViewModel.storyGroups.collectAsState()

    // Photo data
    val photos by photoViewModel.photos.observeAsState(initial = emptyList())
    val isLoading by photoViewModel.isLoading.observeAsState(initial = false)
    val uploadSuccess by photoViewModel.uploadSuccess.observeAsState(initial = false)

    // Search state
    var searchQuery by remember { mutableStateOf("") }
    val filteredPhotos = remember(photos, searchQuery) {
        val regularPhotos = photos.filter { photo -> 
            photo.description?.contains("Created with Magic Paintbrush") != true 
        }
        
        if (searchQuery.isBlank()) {
            regularPhotos
        } else {
            regularPhotos.filter { photo ->
                photo.userName?.contains(searchQuery, ignoreCase = true) == true
            }
        }
    }

    // Debug: Log photos when they change
    LaunchedEffect(photos) {
        Log.d("HomeScreen", "Photos updated: ${photos.size} photos")
        photos.forEach { photo ->
            Log.d("HomeScreen", "Photo: id=${photo.id}, imageUrl=${photo.imageUrl}, title=${photo.title}")
        }
    }

    // Get current route to reload when coming back to HomeScreen
    val currentRoute = navController.currentDestination?.route
    var previousRoute by remember { mutableStateOf<String?>(null) }

    // Load photos, notifications, and user data on first appearance
    LaunchedEffect(Unit) {
        photoViewModel.getAllPhotos()
        notificationViewModel.getUnreadCount()
        profileViewModel.fetchUserData()
        storyViewModel.loadStories(context)    // 👈 Charger les stories
    }

    // Reload photos and user data when returning to HomeScreen from another screen
    LaunchedEffect(currentRoute) {
        if (currentRoute == NavGraph.Home.route && previousRoute != null && previousRoute != NavGraph.Home.route) {
            // We're coming back to HomeScreen, reload photos and user data
            photoViewModel.getAllPhotos()
            profileViewModel.fetchUserData()
            storyViewModel.loadStories(context)    // 👈 Recharger les stories
        }
        previousRoute = currentRoute
    }

    // Reload photos when a new photo is uploaded successfully
    LaunchedEffect(uploadSuccess) {
        if (uploadSuccess) {
            // Wait a bit for backend to process, then reload
            kotlinx.coroutines.delay(1000)
            if (currentRoute == NavGraph.Home.route) {
                photoViewModel.getAllPhotos()
                notificationViewModel.getUnreadCount() // Refresh notification count
            }
            photoViewModel.clearUploadSuccess()
        }
    }

    // Refresh notification count when returning to HomeScreen
    LaunchedEffect(currentRoute) {
        if (currentRoute == NavGraph.Home.route) {
            notificationViewModel.getUnreadCount()
        }
    }

    // Handle logout
    LaunchedEffect(logoutState) {
        if (logoutState is LogoutState.Success) {
            // Navigate to Welcome screen and clear entire back stack
            navController.navigate(NavGraph.Welcome.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Animation states
    var headerVisible by remember { mutableStateOf(false) }
    var searchVisible by remember { mutableStateOf(false) }
    
    // Multiple animated gradients for dynamic background - animations plus lentes
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val gradientOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientOffset1"
    )
    val gradientOffset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientOffset2"
    )
    
    // Floating particles animation - plus lente
    val particleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleOffset"
    )
    
    // Pulsing effect for decorative elements - plus subtil
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    // Wave animation for dynamic background - plus lente
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )
    
    // Glow pulse animation - plus subtil
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )
    
    LaunchedEffect(Unit) {
        delay(100)
        headerVisible = true
        delay(200)
        searchVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0F1E),
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        PrimaryYellowDark.copy(alpha = 0.3f + gradientOffset1 * 0.2f)
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        // Animated decorative elements
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top right animated circle - mouvement réduit
            val topCircleX = 50.dp + (gradientOffset1 * 15).dp
            val topCircleY = (-50).dp + (gradientOffset2 * 10).dp
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(200.dp + (gradientOffset1 * 15).dp)
                    .offset(x = topCircleX, y = topCircleY)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PrimaryYellowDark.copy(alpha = 0.25f + gradientOffset1 * 0.1f),
                                PrimaryYellowLight.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                    .graphicsLayer(alpha = 0.6f + gradientOffset1 * 0.1f)
            )
            
            // Bottom left animated circle - mouvement réduit
            val bottomCircleX = (-30).dp - (gradientOffset2 * 10).dp
            val bottomCircleY = 100.dp + (gradientOffset1 * 15).dp
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(160.dp + (gradientOffset2 * 20).dp)
                    .offset(x = bottomCircleX, y = bottomCircleY)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PrimaryYellowLight.copy(alpha = 0.2f + gradientOffset2 * 0.1f),
                                PrimaryYellowDark.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                    .graphicsLayer(alpha = 0.5f + gradientOffset2 * 0.15f)
            )
            
            // Enhanced floating particles effect with variety - réduit
            repeat(8) { index ->
                val angle = (particleOffset + index * 45f) * (kotlin.math.PI / 180f)
                val radius = 100.dp + (gradientOffset1 * 40).dp + (index * 8).dp
                val particleX = (cos(angle) * radius.value).dp
                val particleY = (sin(angle) * radius.value).dp
                val particleSize = (5.dp + (gradientOffset1 * 3).dp + ((index % 3) * 1.5).dp)
                val particleAlpha = (0.25f + glowPulse * 0.15f - (index * 0.02f)).coerceIn(0.15f, 0.5f)
                
                // Varying colors for particles
                val particleColor = when (index % 4) {
                    0 -> PrimaryYellowLight
                    1 -> PrimaryYellowDark
                    2 -> Color.White.copy(alpha = 0.6f)
                    else -> PrimaryYellowLight.copy(alpha = 0.8f)
                }
                
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(particleSize)
                        .offset(x = particleX, y = particleY)
                        .background(
                            particleColor.copy(alpha = particleAlpha),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                        .graphicsLayer(
                            alpha = particleAlpha,
                            scaleX = pulseScale,
                            scaleY = pulseScale
                        )
                        .shadow(
                            elevation = 2.dp,
                            shape = androidx.compose.foundation.shape.CircleShape,
                            spotColor = particleColor.copy(alpha = 0.3f)
                        )
                )
            }
            
            // Additional wave effect layers - plus subtiles
            repeat(2) { waveIndex ->
                val waveY = (waveOffset * 150f + waveIndex * 80f) % 300f
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.5.dp)
                        .offset(y = waveY.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    PrimaryYellowLight.copy(alpha = 0.15f - waveIndex * 0.05f),
                                    PrimaryYellowDark.copy(alpha = 0.1f - waveIndex * 0.03f),
                                    Color.Transparent
                                )
                            )
                        )
                        .graphicsLayer(alpha = 0.3f - waveIndex * 0.1f)
                )
            }
        }
        
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Animated Header with user name, notification, and logout
            AnimatedVisibility(
                visible = headerVisible,
                enter = fadeIn(animationSpec = tween(600)) + 
                        slideInVertically(
                            initialOffsetY = { -it / 2 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
            ) {
                Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 56.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Display user name from backend (saved during signup)
                Text(
                    text = "Welcome ${userData?.name.orEmpty()}",
                    fontSize = 15.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.2.sp,
                    modifier = Modifier
                        .graphicsLayer(
                            alpha = 0.95f + gradientOffset1 * 0.05f
                        )
                )


                // Notification and Logout buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Notification Button - avec effet glow
                    Box {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = false),
                                    onClick = {
                                        navController.navigate(NavGraph.Notifications.route)
                                    }
                                )
                                .background(
                                    PrimaryYellowDark.copy(alpha = 0.9f + glowPulse * 0.1f),
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                                .shadow(
                                    elevation = 8.dp,
                                    shape = androidx.compose.foundation.shape.CircleShape,
                                    spotColor = PrimaryYellowDark.copy(alpha = glowPulse * 0.4f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        // Red notification badge
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(14.dp)
                                    .background(
                                        Color(0xFFFF0000), // Red color
                                        shape = androidx.compose.foundation.shape.CircleShape
                                    )
                                    .shadow(
                                        elevation = 2.dp,
                                        shape = androidx.compose.foundation.shape.CircleShape
                                    )
                            )
                        }
                    }
                }
                }
            }

            // Animated Search Bar with modern design
            AnimatedVisibility(
                visible = searchVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 100)) + 
                        slideInVertically(
                            initialOffsetY = { 20 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 20.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = PrimaryYellowDark.copy(alpha = 0.3f)
                        ),
                    placeholder = {
                        Text(
                            text = "Rechercher par nom d'utilisateur",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 15.sp
                        )
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .background(
                                    PrimaryYellowDark.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = PrimaryYellowDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = PrimaryYellowDark,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        containerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White.copy(alpha = 0.9f)
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 15.sp
                    ),
                    singleLine = true
                )
            }
            
            // Stories Section
            if (storyGroups.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                StoriesRow(
                    groups = storyGroups,
                    onStoryClick = { group ->
                        storyViewModel.openStories(group.stories)
                        navController.navigate(NavGraph.StoryViewer.route)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Photos Grid
            Box(modifier = Modifier.weight(1f)) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFFF9800))
                    }
                } else if (filteredPhotos.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotBlank()) "No photos found" else "No photos available",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    val sortedPhotos = remember(filteredPhotos) {
                        filteredPhotos.sortedByDescending { it.createdAt ?: "" }
                    }
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        contentPadding = PaddingValues(bottom = 100.dp, top = 4.dp)
                    ) {
                        items(
                            count = sortedPhotos.size,
                            key = { index -> sortedPhotos[index].id }
                        ) { index ->
                            val photo = sortedPhotos[index]
                            PhotoCard(
                                photo = photo,
                                onClick = {
                                    navController.navigate("${NavGraph.PhotoDetail.route}/${photo.id}")
                                },
                                index = index
                            )
                        }
                    }
                }
            }

            // Bottom Navigation Bar
            BottomNavigationBar(navController = navController)
        }

        // Avatar Generator FAB
        FloatingActionButton(
            onClick = { navController.navigate(NavGraph.AvatarGenerator.route) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 90.dp, end = 20.dp), // Above bottom nav
            containerColor = PrimaryYellowLight,
            contentColor = Color.Black
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar Generator"
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoCard(
    photo: Photo,
    onClick: () -> Unit,
    index: Int = 0
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 50L)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(400)) +
                scaleIn(
                    initialScale = 0.9f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val cardScale by animateFloatAsState(
            targetValue = if (isPressed) 0.95f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessHigh
            ),
            label = "cardScale"
        )

        // Simple card style like the capture - just image with rounded corners
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // Square aspect ratio like in the capture
                .scale(cardScale)
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(
                        color = Color.White.copy(alpha = 0.3f),
                        bounded = true
                    ),
                    onClick = onClick
                )
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    spotColor = Color.Black.copy(alpha = 0.2f)
                )
        ) {
            // Image with rounded corners - simple style
            AsyncImage(
                model = photo.imageUrl,
                contentDescription = photo.title ?: "Photo",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                placeholder = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_gallery),
                error = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_report_image)
            )
        }
    }
}

