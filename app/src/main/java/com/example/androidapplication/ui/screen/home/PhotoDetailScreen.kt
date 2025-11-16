package com.example.androidapplication.ui.screen.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.androidapplication.models.Photo
import com.example.androidapplication.models.PhotoViewModel
import com.example.androidapplication.ui.components.BackButton
import com.example.androidapplication.ui.container.NavGraph
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import com.example.androidapplication.ui.theme.PrimaryYellowLight

@Composable
fun PhotoDetailScreen(
    photoId: String,
    navController: NavController,
    photoViewModel: PhotoViewModel = viewModel(key = "shared_photo_viewmodel")
) {
    val photos by photoViewModel.photos.observeAsState(initial = emptyList())
    val photo = remember(photos, photoId) {
        photos.find { it.id == photoId }
    }
    
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
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
        if (photo != null) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Navigation bar at top - Always visible and clickable
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackButton(
                        navController = navController,
                        onClick = {
                            navController.popBackStack()
                        }
                    )
                }
                
                // Top Section - Smaller Image Display
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(400, delayMillis = 100)) + 
                            slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = tween(400, delayMillis = 100)
                            )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .padding(horizontal = 20.dp)
                    ) {
                        AsyncImage(
                            model = photo.imageUrl,
                            contentDescription = photo.title ?: "Photo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom Section - Photo Details
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(400, delayMillis = 200)) + 
                            slideInVertically(
                                initialOffsetY = { it / 3 },
                                animationSpec = tween(400, delayMillis = 200)
                            )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // User name
                        Text(
                            text = photo.userName ?: "User",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF5A4A3A),
                            letterSpacing = 0.3.sp
                        )
                        
                        // Title - Large and bold
                        if (!photo.title.isNullOrEmpty()) {
                            Text(
                                text = photo.title ?: "",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF2C2416),
                                lineHeight = 38.sp,
                                letterSpacing = (-0.8).sp
                            )
                        }
                        
                        // Description - Regular text
                        if (!photo.description.isNullOrEmpty()) {
                            Text(
                                text = photo.description ?: "",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF5A4A3A).copy(alpha = 0.9f),
                                lineHeight = 24.sp,
                                letterSpacing = 0.1.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Date if available
                        photo.createdAt?.let { date ->
                            Text(
                                text = formatDate(date),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF8B7355),
                                letterSpacing = 0.3.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Loading or error state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = Color.White)
                    Text(
                        text = "Loading photo...",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val date = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
            .parse(dateString)
        date?.let {
            java.text.SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", java.util.Locale.getDefault())
                .format(it)
        } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}
