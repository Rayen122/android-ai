package com.example.androidapplication.ui.screen.forgotpassword

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.*
import com.example.androidapplication.R
import com.example.androidapplication.ui.theme.PrimaryGreenLight
import com.example.androidapplication.ui.theme.PrimaryYellowLight
import com.example.androidapplication.models.Password.ForgotPasswordViewModel
import com.example.androidapplication.ui.components.BackButton
import com.example.androidapplication.ui.container.NavGraph
import com.example.androidapplication.ui.screen.registration.RegistrationScreen
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    onRestPasswordClicked: (String) -> Unit = {} ,
    onBackToLoginClicked: () -> Unit = {} // 👈 new callback

)
 {
    val context = LocalContext.current
    val viewModel = remember { ForgotPasswordViewModel() } // ViewModel instance

    // Animation states
    var lottieVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var emailFieldVisible by remember { mutableStateOf(false) }
    var buttonsVisible by remember { mutableStateOf(false) }
    
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
    
    // Lottie animation setup
    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.brain))
    val lottieAnimationState = rememberLottieAnimatable()

    LaunchedEffect(lottieComposition) {
        lottieAnimationState.animate(
            composition = lottieComposition,
            iterations = LottieConstants.IterateForever
        )
    }
    
    // Sequential entrance animations
    LaunchedEffect(Unit) {
        delay(200)
        lottieVisible = true
        delay(300)
        titleVisible = true
        delay(200)
        emailFieldVisible = true
        delay(200)
        buttonsVisible = true
    }
    
    var email by remember { mutableStateOf("") }
    var emailFocused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
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
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Back button at top if navController is provided
            navController?.let {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    BackButton(
                        navController = it,
                        onClick = {
                            onBackToLoginClicked()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Animated Lottie Animation
            AnimatedVisibility(
                visible = lottieVisible,
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
                    targetValue = if (lottieVisible) 1f else 0.5f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "lottieScale"
                )
                
                LottieAnimation(
                    composition = lottieComposition,
                    progress = { lottieAnimationState.progress },
                    modifier = Modifier
                        .size(190.dp)
                        .padding(bottom = 32.dp)
                        .graphicsLayer(scaleX = lottieScale, scaleY = lottieScale)
                )
            }

            // Title Section - sans cadre
            AnimatedVisibility(
                visible = titleVisible,
                enter = fadeIn(animationSpec = tween(400)) + 
                        slideInVertically(
                            initialOffsetY = { -it / 2 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        text = "Mot de passe oublié?",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier
                            .graphicsLayer(
                                alpha = 0.95f + gradientOffset1 * 0.05f
                            )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ne vous inquiétez pas, cela arrive.",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Form container - sans cadre
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                    // Animated Email Input Field
                    AnimatedVisibility(
                        visible = emailFieldVisible,
                        enter = slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn(animationSpec = tween(600))
                    ) {
                        val emailScale by animateFloatAsState(
                            targetValue = if (emailFocused) 1.02f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "emailScale"
                        )
                        
                        val emailElevation by animateFloatAsState(
                            targetValue = if (emailFocused) 8f else 2f,
                            animationSpec = tween(300),
                            label = "emailElevation"
                        )
                        
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(emailScale)
                                .shadow(emailElevation.dp, RoundedCornerShape(16.dp))
                                .onFocusChanged { focusState ->
                                    emailFocused = focusState.isFocused
                                },
                            singleLine = true,
                            placeholder = {
                                Text(
                                    text = "Adresse email",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 15.sp
                                )
                            },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (emailFocused) PrimaryYellowDark.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = "Email",
                                        tint = if (emailFocused) PrimaryYellowDark else Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = PrimaryYellowDark,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                containerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White.copy(alpha = 0.9f)
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = if (emailFocused) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Animated Submit Button
                    AnimatedVisibility(
                        visible = buttonsVisible,
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
                
                        Button(
                            onClick = {
                                if (email.isNotEmpty()) {
                                    viewModel.sendResetLink(
                                        email = email,
                                        onSuccess = {
                                            Toast.makeText(context, "OTP envoyé à $email", Toast.LENGTH_SHORT).show()
                                            onRestPasswordClicked(email)
                                        },
                                        onError = { error ->
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                } else {
                                    Toast.makeText(context, "Veuillez entrer votre email", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .scale(buttonScale)
                                .shadow(buttonElevation.dp, RoundedCornerShape(16.dp)),
                            interactionSource = buttonInteractionSource,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryYellowDark,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                "Envoyer le lien de réinitialisation",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Animated Back to Login Button
                    AnimatedVisibility(
                        visible = buttonsVisible,
                        enter = fadeIn(animationSpec = tween(600, delayMillis = 100)) + 
                                slideInHorizontally(
                                    initialOffsetX = { 20 },
                                    animationSpec = tween(400, delayMillis = 100)
                                )
                    ) {
                val textButtonInteractionSource = remember { MutableInteractionSource() }
                val isPressed by textButtonInteractionSource.collectIsPressedAsState()
                val textButtonScale by animateFloatAsState(
                    targetValue = if (isPressed) 0.95f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessHigh
                    ),
                    label = "textButtonScale"
                )
                
                TextButton(
                    onClick = { onBackToLoginClicked() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(textButtonScale),
                    interactionSource = textButtonInteractionSource
                ) {
                        Text(
                            text = "Retour à la connexion",
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                    }
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen()
}