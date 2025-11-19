package com.example.androidapplication.ui.artiste

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.androidapplication.ui.components.BackButton
import com.example.androidapplication.ui.components.BottomNavigationBar
import com.example.androidapplication.ui.theme.PrimaryYellow
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import com.example.androidapplication.ui.theme.PrimaryYellowLight
import java.net.URLDecoder
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    navController: NavController,
    artistName: String,
    styleDescription: String,
    country: String,
    famousWorks: String
) {
    // Decode URL encoded arguments
    val decodedStyleDescription = URLDecoder.decode(styleDescription, "UTF-8")
    val decodedFamousWorks = URLDecoder.decode(famousWorks, "UTF-8")
    val worksList = decodedFamousWorks.split(",").filter { it.isNotBlank() }
    
    var isVisible by remember { mutableStateOf(false) }
    
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
        isVisible = true
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
            // Top Section - Large Image (Fixed)
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
                        .height(400.dp)
                ) {
                    // Navigation buttons at top - Always visible and clickable
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

                        IconButton(
                            onClick = {
                                // Share functionality
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    PrimaryYellowDark.copy(alpha = 0.9f),
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                                .shadow(
                                    elevation = 8.dp,
                                    shape = androidx.compose.foundation.shape.CircleShape,
                                    spotColor = PrimaryYellowDark.copy(alpha = 0.4f)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Large image with better error handling
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 70.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(
                                    bottomStart = 40.dp,
                                    bottomEnd = 40.dp
                                ),
                                spotColor = PrimaryYellowDark.copy(alpha = 0.4f)
                            )
                            .clip(
                                RoundedCornerShape(
                                    bottomStart = 40.dp,
                                    bottomEnd = 40.dp
                                )
                            )
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.15f),
                                        Color.White.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Try multiple image sources with fallback
                        val seed = artistName.hashCode().toLong().let { if (it < 0) -it else it }
                        val imageUrl = "https://picsum.photos/seed/$seed/400/500"
                        
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "$artistName's artistic representation",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_gallery),
                            error = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_report_image)
                        )
                    }
                }
            }

            // Bottom Section - Details (Scrollable)
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(400, delayMillis = 200)) + 
                        slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(400, delayMillis = 200)
                        )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(
                            Color.Transparent,
                            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                    // Artist Name and Country
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = artistName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.2.sp,
                            modifier = Modifier
                                .graphicsLayer(
                                    alpha = 0.95f + gradientOffset1 * 0.05f
                                )
                        )
                        
                        Text(
                            text = country,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f),
                            letterSpacing = 0.1.sp
                        )
                    }

                    // About the Artist section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "About the artist",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.2.sp
                        )

                        Text(
                            text = decodedStyleDescription,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 22.sp,
                            letterSpacing = 0.1.sp
                        )
                    }

                    // Famous Works section
                    if (worksList.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Famous Works",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.2.sp
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                worksList.forEach { work ->
                                    Text(
                                        text = "• $work",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.White.copy(alpha = 0.9f),
                                        lineHeight = 22.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Bottom Navigation Bar - Fixed at bottom
        }
    }
    }}
