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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.PhotoFilter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush as GradientBrush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.CachePolicy
import coil.size.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import android.util.Log
import com.example.androidapplication.models.Photo
import com.example.androidapplication.models.PhotoViewModel
import com.example.androidapplication.ui.components.BackButton
import com.example.androidapplication.ui.container.NavGraph
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import com.example.androidapplication.ui.theme.PrimaryYellowLight

// ImageLoader personnalisé avec des timeouts plus longs pour Pollinations
@Composable
private fun rememberImageLoader(context: android.content.Context): ImageLoader {
    return remember(context) {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS) // 2 minutes pour la génération d'images
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
        
        ImageLoader.Builder(context)
            .okHttpClient(okHttpClient)
            .build()
    }
}

@Composable
fun PhotoDetailScreen(
    photoId: String,
    navController: NavController,
    photoViewModel: PhotoViewModel = viewModel(key = "shared_photo_viewmodel")
) {
    val context = LocalContext.current
    val imageLoader = rememberImageLoader(context)
    
    val photos by photoViewModel.photos.observeAsState(initial = emptyList())
    val photo = remember(photos, photoId) {
        photos.find { it.id == photoId }
    }
    
    val isConvertingSketch by photoViewModel.isConvertingSketch.observeAsState(initial = false)
    val sketchImageUrl by photoViewModel.sketchImageUrl.observeAsState(initial = null)
    val convertedImageUrl by photoViewModel.convertedImageUrl.observeAsState(initial = null)
    val error by photoViewModel.error.observeAsState(initial = "")
    
    var isVisible by remember { mutableStateOf(false) }
    var showConvertedImage by remember { mutableStateOf(false) }
    var showConversionMenu by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                GradientBrush.verticalGradient(
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
                        // Afficher l'image convertie si disponible, sinon l'image originale
                        val imageToShow = if (showConvertedImage && (sketchImageUrl != null || convertedImageUrl != null)) {
                            convertedImageUrl ?: sketchImageUrl
                        } else {
                            photo.imageUrl
                        }
                        
                        // Créer une ImageRequest avec configuration pour les images Pollinations
                        val imageRequest = remember(imageToShow) {
                            ImageRequest.Builder(context)
                                .data(imageToShow)
                                .size(Size.ORIGINAL)
                                .crossfade(true)
                                .allowHardware(false)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .networkCachePolicy(CachePolicy.ENABLED)
                                .setHeader("User-Agent", "Mozilla/5.0 (Android) Coil/2.5.0")
                                .setHeader("Accept", "image/*")
                                .listener(
                                    onStart = { 
                                        Log.d("PhotoDetailScreen", "Image loading started: ${imageToShow?.take(100)}...")
                                    },
                                    onSuccess = { _, _ -> 
                                        Log.d("PhotoDetailScreen", "Image loaded successfully: ${imageToShow?.take(100)}...")
                                    },
                                    onError = { _, result -> 
                                        Log.e("PhotoDetailScreen", "Image loading failed: ${imageToShow?.take(100)}...")
                                        val throwable = result.throwable
                                        if (throwable != null) {
                                            Log.e("PhotoDetailScreen", "Error message: ${throwable.message}")
                                            Log.e("PhotoDetailScreen", "Error cause: ${throwable.cause}")
                                        }
                                    }
                                )
                                .build()
                        }
                        
                        AsyncImage(
                            model = imageRequest,
                            imageLoader = imageLoader,
                            contentDescription = if (showConvertedImage) "Image convertie de ${photo.title ?: "Photo"}" else photo.title ?: "Photo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp)),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                            error = painterResource(android.R.drawable.ic_menu_report_image)
                        )
                        
                        // Menu de conversion avec plusieurs options
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                        ) {
                            // Menu déroulant
                            DropdownMenu(
                                expanded = showConversionMenu,
                                onDismissRequest = { showConversionMenu = false },
                                modifier = Modifier.background(Color(0xFF1A1A2E))
                            ) {
                                // Option 1: Croquis (Sketch)
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                            Text(
                                                text = "Croquis",
                                                color = Color.White
                                            )
                                        }
                                    },
                                    onClick = {
                                        showConversionMenu = false
                                        if (isConvertingSketch) {
                                            photoViewModel.cancelSketchConversion()
                                        } else if (showConvertedImage && (sketchImageUrl != null || convertedImageUrl != null)) {
                                            showConvertedImage = false
                                            photoViewModel.clearSketchImage()
                                        } else {
                                            photoViewModel.convertToSketch(photo)
                                        }
                                    }
                                )
                                
                                // Option 2: Aquarelle
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Brush,
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                            Text(
                                                text = "Aquarelle",
                                                color = Color.White
                                            )
                                        }
                                    },
                                    onClick = {
                                        showConversionMenu = false
                                        photoViewModel.convertToWatercolor(photo)
                                    }
                                )
                                
                                // Option 3: Vintage/Sépia
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PhotoFilter,
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                            Text(
                                                text = "Vintage",
                                                color = Color.White
                                            )
                                        }
                                    },
                                    onClick = {
                                        showConversionMenu = false
                                        photoViewModel.convertToVintage(photo)
                                    }
                                )
                                
                                // Option 4: Noir et blanc
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                            Text(
                                                text = "Noir et blanc",
                                                color = Color.White
                                            )
                                        }
                                    },
                                    onClick = {
                                        showConversionMenu = false
                                        photoViewModel.convertToBlackAndWhite(photo)
                                    }
                                )
                                
                                // Option 5: Peinture à l'huile
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Palette,
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                            Text(
                                                text = "Peinture à l'huile",
                                                color = Color.White
                                            )
                                        }
                                    },
                                    onClick = {
                                        showConversionMenu = false
                                        photoViewModel.convertToOilPainting(photo)
                                    }
                                )
                            }
                            
                            // Bouton principal pour ouvrir le menu
                            FloatingActionButton(
                                onClick = {
                                if (isConvertingSketch) {
                                    photoViewModel.cancelSketchConversion()
                                } else if (showConvertedImage && (sketchImageUrl != null || convertedImageUrl != null)) {
                                    showConvertedImage = false
                                    photoViewModel.clearSketchImage()
                                } else {
                                    showConversionMenu = !showConversionMenu
                                }
                                },
                                modifier = Modifier.size(48.dp),
                                containerColor = when {
                                isConvertingSketch -> Color(0xFFD32F2F) // Rouge pour annuler
                                showConvertedImage -> Color(0xFF4A4A4A)
                                else -> PrimaryYellowDark
                            },
                                contentColor = Color.White
                            ) {
                                if (isConvertingSketch) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Annuler",
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "Options de conversion",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Afficher le résultat de la conversion
                LaunchedEffect(sketchImageUrl, convertedImageUrl) {
                    if (sketchImageUrl != null || convertedImageUrl != null) {
                        val url = convertedImageUrl ?: sketchImageUrl
                        Log.d("PhotoDetailScreen", "Converted image URL received: $url")
                        showConvertedImage = true
                    } else {
                        Log.d("PhotoDetailScreen", "Converted image URL is null")
                    }
                }
                
                // Log pour déboguer
                LaunchedEffect(showConvertedImage, sketchImageUrl, convertedImageUrl) {
                    Log.d("PhotoDetailScreen", "showConvertedImage: $showConvertedImage, sketchImageUrl: $sketchImageUrl, convertedImageUrl: $convertedImageUrl")
                }
                
                // Afficher les erreurs (afficher toutes les erreurs liées à la conversion)
                if (error.isNotEmpty() && (error.contains("croquis", ignoreCase = true) || 
                    error.contains("conversion", ignoreCase = true) || 
                    error.contains("sketch", ignoreCase = true) ||
                    error.contains("convertir", ignoreCase = true))) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { photoViewModel.clearError() }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Fermer",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
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
