package com.example.androidapplication.ui.screen.profile

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import com.example.androidapplication.ui.theme.PrimaryYellowLight
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoUploadScreen(
    bitmap: Bitmap,
    isUploading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onUploadComplete: (String?, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Multiple animated gradients for dynamic background
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
    
    // Floating particles animation
    val particleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleOffset"
    )
    
    // Pulsing effect for decorative elements
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    // Wave animation
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )
    
    // Glow pulse animation
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        // Animated background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            // Modal Card with animated background
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.85f)
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
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .shadow(
                        elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                        spotColor = PrimaryYellowDark.copy(alpha = 0.3f)
                    )
            ) {
                // Animated decorative elements
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Top right animated circle
                    val topCircleX = 30.dp + (gradientOffset1 * 10).dp
                    val topCircleY = (-30).dp + (gradientOffset2 * 8).dp
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(120.dp + (gradientOffset1 * 10).dp)
                            .offset(x = topCircleX, y = topCircleY)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        PrimaryYellowDark.copy(alpha = 0.2f + gradientOffset1 * 0.08f),
                                        PrimaryYellowLight.copy(alpha = 0.12f),
                                        Color.Transparent
                                    )
                                ),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                            .graphicsLayer(alpha = 0.5f + gradientOffset1 * 0.08f)
                    )
                    
                    // Bottom left animated circle
                    val bottomCircleX = (-20).dp - (gradientOffset2 * 8).dp
                    val bottomCircleY = 60.dp + (gradientOffset1 * 10).dp
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .size(100.dp + (gradientOffset2 * 15).dp)
                            .offset(x = bottomCircleX, y = bottomCircleY)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        PrimaryYellowLight.copy(alpha = 0.15f + gradientOffset2 * 0.08f),
                                        PrimaryYellowDark.copy(alpha = 0.08f),
                                        Color.Transparent
                                    )
                                ),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                            .graphicsLayer(alpha = 0.4f + gradientOffset2 * 0.12f)
                    )
                    
                    // Floating particles
                    repeat(6) { index ->
                        val angle = (particleOffset + index * 60f) * (kotlin.math.PI / 180f)
                        val radius = 60.dp + (gradientOffset1 * 25).dp + (index * 6).dp
                        val particleX = (cos(angle) * radius.value).dp
                        val particleY = (sin(angle) * radius.value).dp
                        val particleSize = (4.dp + (gradientOffset1 * 2).dp + ((index % 3) * 1).dp)
                        val particleAlpha = (0.2f + glowPulse * 0.12f - (index * 0.015f)).coerceIn(0.12f, 0.4f)
                        
                        val particleColor = when (index % 4) {
                            0 -> PrimaryYellowLight
                            1 -> PrimaryYellowDark
                            2 -> Color.White.copy(alpha = 0.5f)
                            else -> PrimaryYellowLight.copy(alpha = 0.7f)
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
                                    elevation = 1.5.dp,
                                    shape = androidx.compose.foundation.shape.CircleShape,
                                    spotColor = particleColor.copy(alpha = 0.25f)
                                )
                        )
                    }
                    
                    // Wave effect layers
                    repeat(2) { waveIndex ->
                        val waveY = (waveOffset * 100f + waveIndex * 50f) % 200f
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .offset(y = waveY.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            PrimaryYellowLight.copy(alpha = 0.12f - waveIndex * 0.04f),
                                            PrimaryYellowDark.copy(alpha = 0.08f - waveIndex * 0.025f),
                                            Color.Transparent
                                        )
                                    )
                                )
                                .graphicsLayer(alpha = 0.25f - waveIndex * 0.08f)
                        )
                    }
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Publier une photo",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier
                                .graphicsLayer(
                                    alpha = 0.95f + gradientOffset1 * 0.05f
                                )
                        )
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = "Annuler",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Image Preview
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Photo preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = PrimaryYellowDark.copy(alpha = 0.3f)
                            )
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    // Inputs
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = {
                                Text(
                                    text = "Titre de la photo",
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(12.dp),
                                    spotColor = PrimaryYellowDark.copy(alpha = 0.2f)
                                ),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = PrimaryYellowDark,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                containerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White.copy(alpha = 0.9f)
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = {
                                Text(
                                    text = "Description",
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(12.dp),
                                    spotColor = PrimaryYellowDark.copy(alpha = 0.2f)
                                ),
                            minLines = 3,
                            maxLines = 5,
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = PrimaryYellowDark,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                containerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White.copy(alpha = 0.9f)
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Upload Button with shimmer effect
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(28.dp),
                                spotColor = PrimaryYellowDark.copy(alpha = glowPulse * 0.3f)
                            )
                    ) {
                        // Shimmer effect overlay
                        val shimmerOffset by infiniteTransition.animateFloat(
                            initialValue = -1f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(3000, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "shimmerOffset"
                        )
                        
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.White.copy(alpha = 0.4f * (shimmerOffset + 1f) / 2f),
                                            Color.White.copy(alpha = 0.5f * (shimmerOffset + 1f) / 2f),
                                            Color.Transparent
                                        ),
                                        start = androidx.compose.ui.geometry.Offset(
                                            shimmerOffset * 1000f,
                                            0f
                                        ),
                                        end = androidx.compose.ui.geometry.Offset(
                                            shimmerOffset * 1000f + 400f,
                                            100f
                                        )
                                    ),
                                    shape = RoundedCornerShape(28.dp)
                                )
                        )
                        
                        // Glow effect
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            PrimaryYellowLight.copy(alpha = glowPulse * 0.2f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = RoundedCornerShape(28.dp)
                                )
                        )
                        
                    Button(
                        onClick = {
                            onUploadComplete(
                                title.takeIf { it.isNotEmpty() },
                                description.takeIf { it.isNotEmpty() }
                            )
                        },
                            modifier = Modifier.fillMaxSize(),
                        enabled = !isUploading,
                            shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryYellowDark.copy(alpha = 0.95f + glowPulse * 0.05f),
                                contentColor = Color.White
                        )
                    ) {
                        if (isUploading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Publier",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }
                        }
                    }

                    // Error Message
                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = Color(0xFFFF5252),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

