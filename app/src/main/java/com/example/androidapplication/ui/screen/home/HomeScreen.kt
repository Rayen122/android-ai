package com.example.androidapplication.ui.screen.home

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    photoViewModel: PhotoViewModel = viewModel(key = "shared_photo_viewmodel")
) {
    val context = LocalContext.current
    val logoutViewModel = remember { LogoutViewModel() }
    val logoutState by logoutViewModel.logoutState.collectAsState()
    val notificationViewModel: NotificationViewModel = viewModel()
    val unreadCount by notificationViewModel.unreadCount.observeAsState(initial = 0)
    val profileViewModel: ProfileViewModel = viewModel()
    val userData by profileViewModel.userData.observeAsState()

    // Photo data
    val photos by photoViewModel.photos.observeAsState(initial = emptyList())
    val isLoading by photoViewModel.isLoading.observeAsState(initial = false)
    val uploadSuccess by photoViewModel.uploadSuccess.observeAsState(initial = false)
    
    // Search state
    var searchQuery by remember { mutableStateOf("") }
    val filteredPhotos = remember(photos, searchQuery) {
        if (searchQuery.isBlank()) {
            photos
        } else {
            photos.filter { photo ->
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
    }
    
    // Reload photos and user data when returning to HomeScreen from another screen
    LaunchedEffect(currentRoute) {
        if (currentRoute == NavGraph.Home.route && previousRoute != null && previousRoute != NavGraph.Home.route) {
            // We're coming back to HomeScreen, reload photos and user data
            photoViewModel.getAllPhotos()
            profileViewModel.fetchUserData()
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


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to PrimaryYellowDark,
                    0.6f to PrimaryYellowLight,
                    1f to PrimaryYellowLight,
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with user name, notification, and logout
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
                    text = userData?.name.orEmpty(),
                    fontSize = 15.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.2.sp
                )


                // Notification and Logout buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Notification Button - Circular with light brown background
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
                                    Color(0xFFD4A574), // Light brown color
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                                .shadow(
                                    elevation = 2.dp,
                                    shape = androidx.compose.foundation.shape.CircleShape,
                                    spotColor = Color.Black.copy(alpha = 0.1f)
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
                                    .size(12.dp)
                                    .background(
                                        Color(0xFFFF0000), // Red color
                                        shape = androidx.compose.foundation.shape.CircleShape
                                    )
                            )
                        }
                    }

                    // Logout Button - Circular with light brown background
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = false),
                                onClick = {
                val refreshToken = getRefreshToken(context)
                if (refreshToken != null) {
                                        logoutViewModel.logout(refreshToken, context)
                                    }
                                }
                            )
                            .background(
                                Color(0xFFD4A574), // Light brown color
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                            .shadow(
                                elevation = 2.dp,
                                shape = androidx.compose.foundation.shape.CircleShape,
                                spotColor = Color.Black.copy(alpha = 0.1f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp)
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = Color.Black.copy(alpha = 0.1f)
                    ),
                placeholder = {
                    Text(
                        text = "Search by user name",
                        color = Color.Gray.copy(alpha = 0.5f),
                        fontSize = 15.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray.copy(alpha = 0.5f),
                        modifier = Modifier.size(22.dp)
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFFF9800),
                    unfocusedBorderColor = Color.Transparent,
                    containerColor = Color.White
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.Black,
                    fontSize = 15.sp
                ),
                singleLine = true
            )

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
                            items = filteredPhotos.sortedByDescending { 
                                it.createdAt ?: ""
                            },
                            key = { photo -> photo.id }
                        ) { photo ->
                            PhotoCard(
                                photo = photo,
                                onClick = { 
                                    navController.navigate("${NavGraph.PhotoDetail.route}/${photo.id}")
                                }
                            )
                        }
                    }
                }
            }

            // Bottom Navigation Bar
            BottomNavigationBar(navController = navController)
        }
    }

}

@Composable
fun PhotoCard(
    photo: Photo,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Image Card with enhanced shadow
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(),
                    onClick = onClick
                )
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = Color.Black.copy(alpha = 0.12f),
                    ambientColor = Color.Black.copy(alpha = 0.06f)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp // Using shadow modifier instead
            )
        ) {
            // Image with fixed aspect ratio
            AsyncImage(
                model = photo.imageUrl,
                contentDescription = photo.title ?: "Photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f) // 3:4 aspect ratio
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop,
                placeholder = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_gallery),
                error = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_report_image)
            )
        }

        // User button with icon - Simple design matching image
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true),
                    onClick = onClick
                )
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(12.dp),
                    spotColor = Color.Black.copy(alpha = 0.06f),
                    ambientColor = Color.Black.copy(alpha = 0.03f)
                ),
            shape = RoundedCornerShape(12.dp),
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
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Simple yellow person icon (no background box)
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = Color(0xFFFFE7BA), // Yellow color
                    modifier = Modifier.size(20.dp)
                )

                // User name
                Text(
                    text = photo.userName ?: "User",
                    fontSize = 13.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

