package com.example.androidapplication.ui.screen.welcome

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.androidapplication.R
import com.example.androidapplication.models.login.getSavedTokens
import com.example.androidapplication.ui.components.ActionButton
import com.example.androidapplication.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WelcomeScreen(
    onOpenLoginClicked: () -> Unit,
    onAutoLogin: () -> Unit
) {
    val context = LocalContext.current
    var navigateTo by remember { mutableStateOf<(() -> Unit)?>(null) }

    LaunchedEffect(Unit) {
        val (_, refreshToken) = getSavedTokens(context)
        navigateTo = if (!refreshToken.isNullOrEmpty()) {
            onAutoLogin // Go directly to HomeScreen
        } else {
            onOpenLoginClicked // Go to LoginScreen
        }
    }

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
    
    // Shimmer effect for buttons - plus lent
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
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
    
    // Lottie + UI animation
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.pencil))
    var animationOffset by remember { mutableStateOf(-300.dp) }
    val animatedOffset by animateDpAsState(
        targetValue = animationOffset,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "animatedOffset"
    )
    
    var isTitleVisible by remember { mutableStateOf(false) }
    var isLottieVisible by remember { mutableStateOf(false) }
    var isButtonVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        isTitleVisible = true
        delay(300)
        isLottieVisible = true
        animationOffset = 0.dp
        delay(400)
        isButtonVisible = true
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
            .systemBarsPadding()
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
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Spacer(modifier = Modifier.height(54.dp))
        
        // Animated Title with scale and fade
        AnimatedVisibility(
            visible = isTitleVisible,
            enter = fadeIn(animationSpec = tween(1000)) + 
                    slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
        ) {
            val titleScale by animateFloatAsState(
                targetValue = if (isTitleVisible) 1f else 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "titleScale"
            )
            
            Text(
                text = "Create art with ease!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = titleScale,
                        scaleY = titleScale,
                        alpha = 0.95f + gradientOffset1 * 0.05f
                    )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Animated Lottie with scale
        AnimatedVisibility(
            visible = isLottieVisible,
            enter = fadeIn(animationSpec = tween(800)) + 
                    scaleIn(
                        initialScale = 0.5f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
        ) {
            val lottieScale by animateFloatAsState(
                targetValue = if (isLottieVisible) 1f else 0.5f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "lottieScale"
            )
            
            LottieAnimation(
                composition = composition,
                iterations = Int.MAX_VALUE,
                modifier = Modifier
                    .size(300.dp)
                    .offset(x = animatedOffset)
                    .scale(lottieScale)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Animated Button with scale on press
        AnimatedVisibility(
            visible = isButtonVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(animationSpec = tween(600))
        ) {
            val buttonInteractionSource = remember { MutableInteractionSource() }
            val isPressed by buttonInteractionSource.collectIsPressedAsState()
            val buttonScale by animateFloatAsState(
                targetValue = if (isPressed) 0.95f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                ),
                label = "buttonScale"
            )
            
            val buttonElevation by animateFloatAsState(
                targetValue = if (isPressed) 4f else 12f,
                animationSpec = tween(200),
                label = "buttonElevation"
            )
            
            Box(
                modifier = Modifier
                    .scale(buttonScale)
                    .shadow(
                        elevation = buttonElevation.dp,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                        spotColor = PrimaryYellowDark.copy(alpha = glowPulse * 0.3f)
                    )
                    .padding(24.dp)
            ) {
                // Enhanced shimmer effect overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.4f * (shimmerOffset + 1f) / 2f),
                                    Color.White.copy(alpha = 0.5f * (shimmerOffset + 1f) / 2f),
                                    Color.Transparent
                                ),
                                start = Offset(
                                    shimmerOffset * 1000f,
                                    0f
                                ),
                                end = Offset(
                                    shimmerOffset * 1000f + 400f,
                                    100f
                                )
                            ),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp)
                        )
                )
                
                // Glow effect
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    PrimaryYellowLight.copy(alpha = glowPulse * 0.2f),
                                    Color.Transparent
                                )
                            ),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp)
                        )
                )
                
                ActionButton(
                    text = "Next",
                    isNavigationArrowVisible = true,
                    onClicked = { navigateTo?.invoke() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryYellowDark.copy(alpha = 0.95f + glowPulse * 0.05f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                )
            }
        }
        }
    }
}
