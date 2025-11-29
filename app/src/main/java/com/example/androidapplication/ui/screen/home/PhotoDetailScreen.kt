package com.example.androidapplication.ui.screen.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.PhotoFilter
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.example.androidapplication.models.Photo
import com.example.androidapplication.models.PhotoViewModel
import com.example.androidapplication.models.LikeViewModel
import com.example.androidapplication.models.CommentViewModel
import com.example.androidapplication.ui.components.BackButton
import com.example.androidapplication.ui.container.NavGraph
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import com.example.androidapplication.ui.theme.PrimaryYellowLight
import kotlin.math.cos
import kotlin.math.sin

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

private fun downloadImage(context: Context, url: String, title: String) {
    try {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle("Downloading $title")
        request.setDescription("Downloading image...")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "MagicPaintbrush_${System.currentTimeMillis()}.jpg")
        request.setAllowedOverMetered(true)
        request.setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
        Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Log.e("PhotoDetailScreen", "Download failed", e)
        Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
    
    // Like and Comment ViewModels
    val likeViewModel: LikeViewModel = viewModel()
    val commentViewModel: CommentViewModel = viewModel()
    val isLiked by likeViewModel.isLiked.collectAsState()
    val likesCount by likeViewModel.likesCount.collectAsState()
    val comments by commentViewModel.comments.collectAsState()
    val likeError by likeViewModel.error.collectAsState()
    val commentError by commentViewModel.error.collectAsState()
    val addCommentSuccess by commentViewModel.addCommentSuccess.collectAsState()
    
    var isVisible by remember { mutableStateOf(false) }
    var showConvertedImage by remember { mutableStateOf(false) }
    var showConversionMenu by remember { mutableStateOf(false) }
    var showComments by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    
    // Load like status and comments when photo is loaded
    LaunchedEffect(photo?.id) {
        photo?.id?.let { photoId ->
            Log.d("PhotoDetailScreen", "Loading like status and comments for photo: $photoId")
            likeViewModel.checkLikeStatus(photoId, context)
            commentViewModel.getComments(photoId, context)
        } ?: Log.e("PhotoDetailScreen", "Photo ID is null")
    }
    
    // Show error toasts
    LaunchedEffect(likeError) {
        likeError?.let { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(context, "Erreur like: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    LaunchedEffect(commentError) {
        commentError?.let { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(context, "Erreur commentaire: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    // Observer les changements dans les commentaires pour rafraîchir l'affichage
    LaunchedEffect(comments.size) {
        Log.d("PhotoDetailScreen", "Comments count changed: ${comments.size}")
    }
    
    // Afficher un toast de succès quand un commentaire est ajouté
    LaunchedEffect(addCommentSuccess) {
        if (addCommentSuccess) {
            Toast.makeText(context, "Commentaire ajouté avec succès !", Toast.LENGTH_SHORT).show()
            commentViewModel.clearAddCommentSuccess()
        }
    }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Animation states
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
    
    val particleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleOffset"
    )
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )
    
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

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
            // Top right animated circle
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
                        shape = CircleShape
                    )
                    .graphicsLayer(alpha = 0.6f + gradientOffset1 * 0.1f)
            )
            
            // Bottom left animated circle
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
                        shape = CircleShape
                    )
                    .graphicsLayer(alpha = 0.5f + gradientOffset2 * 0.15f)
            )
            
            // Floating particles
            repeat(8) { index ->
                val angle = (particleOffset + index * 45f) * (Math.PI / 180f)
                val radius = 100.dp + (gradientOffset1 * 40).dp + (index * 8).dp
                val particleX = (cos(angle) * radius.value).dp
                val particleY = (sin(angle) * radius.value).dp
                val particleSize = (5.dp + (gradientOffset1 * 3).dp + ((index % 3) * 1.5).dp)
                val particleAlpha = (0.25f + glowPulse * 0.15f - (index * 0.02f)).coerceIn(0.15f, 0.5f)
                
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
                            shape = CircleShape
                        )
                        .graphicsLayer(
                            alpha = particleAlpha,
                            scaleX = pulseScale,
                            scaleY = pulseScale
                        )
                        .shadow(
                            elevation = 2.dp,
                            shape = CircleShape,
                            spotColor = particleColor.copy(alpha = 0.3f)
                        )
                )
            }
            
            // Wave effect layers
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
                    
                    // Save to Portfolio Button
                    IconButton(
                        onClick = {
                            val imageToSave = if (showConvertedImage && (sketchImageUrl != null || convertedImageUrl != null)) {
                                convertedImageUrl ?: sketchImageUrl
                            } else {
                                photo.imageUrl
                            }

                            if (imageToSave != null) {
                                // Launch a coroutine to load the bitmap and save
                                val request = ImageRequest.Builder(context)
                                    .data(imageToSave)
                                    .allowHardware(false) // Important for Bitmap manipulation
                                    .target { result ->
                                        val bitmap = (result as android.graphics.drawable.BitmapDrawable).bitmap
                                        photoViewModel.saveToPortfolio(
                                            bitmap = bitmap,
                                            title = photo.title ?: "Portfolio Item",
                                            description = "Saved from Photo Detail"
                                        )
                                        Toast.makeText(context, "Saved to Portfolio", Toast.LENGTH_SHORT).show()
                                    }
                                    .build()
                                imageLoader.enqueue(request)
                            } else {
                                Toast.makeText(context, "No image to save", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Save to Portfolio",
                            tint = Color.White
                        )
                    }
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
                            color = Color.White,
                            letterSpacing = 0.3.sp
                        )
                        
                        // Title - Large and bold
                        if (!photo.title.isNullOrEmpty()) {
                            Text(
                                text = photo.title ?: "",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
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
                                color = Color.LightGray,
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
                                color = Color.Gray,
                                letterSpacing = 0.3.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Like and Comment buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Like button
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.clickable {
                                    photo?.id?.let { photoId ->
                                        Log.d("PhotoDetailScreen", "Toggling like for photo: $photoId")
                                        likeViewModel.toggleLike(photoId, context)
                                    } ?: Log.e("PhotoDetailScreen", "Photo ID is null")
                                }
                            ) {
                                Icon(
                                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Like",
                                    tint = if (isLiked) Color.Red else Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = likesCount.toString(),
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            // Comment button
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.clickable {
                                    showComments = !showComments
                                    if (showComments) {
                                        photo?.id?.let { photoId ->
                                            commentViewModel.getComments(photoId, context)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Comment,
                                    contentDescription = "Comment",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = comments.size.toString(),
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        
                        // Comments section
                        if (showComments) {
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Add comment section
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = commentText,
                                    onValueChange = { commentText = it },
                                    placeholder = { Text("Ajouter un commentaire...", color = Color.Gray) },
                                    modifier = Modifier.weight(1f),
                                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = PrimaryYellowDark,
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                        cursorColor = PrimaryYellowDark
                                    ),
                                    singleLine = true
                                )
                                IconButton(
                                    onClick = {
                                        if (commentText.isNotBlank()) {
                                            photo?.id?.let { photoId ->
                                                commentViewModel.addComment(photoId, commentText, context)
                                                commentText = ""
                                                // Le toast de succès sera affiché après confirmation du backend
                                            }
                                        } else {
                                            Toast.makeText(context, "Veuillez entrer un commentaire", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Send",
                                        tint = PrimaryYellowDark
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Comments list
                            if (comments.isEmpty()) {
                                Text(
                                    text = "Aucun commentaire",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            } else {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    comments.forEach { comment ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color.White.copy(alpha = 0.05f)
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(12.dp)
                                            ) {
                                                Text(
                                                    text = comment.userName ?: "User",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = comment.text,
                                                    fontSize = 14.sp,
                                                    color = Color.LightGray
                                                )
                                            }
                                        }
                                    }
                                }
                            }
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
