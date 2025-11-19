package com.example.androidapplication.ui.screen.registration

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidapplication.models.register.RegistrationState
import com.example.androidapplication.models.register.RegistrationViewModel
import com.example.androidapplication.ui.components.ActionButton
import com.example.androidapplication.ui.theme.DarkTextColor
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import com.example.androidapplication.ui.theme.PrimaryYellowLight
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    modifier: Modifier = Modifier,
    onprofileClicked: () -> Unit = {},
    onOpenLoginClicked: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // ViewModel
    val viewModel = remember { RegistrationViewModel() }
    val context = LocalContext.current
    val registrationState by viewModel.registrationState.collectAsState()
    
    // Animation states
    var nameFieldVisible by remember { mutableStateOf(false) }
    var emailFieldVisible by remember { mutableStateOf(false) }
    var passwordFieldVisible by remember { mutableStateOf(false) }
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
    
    // Sequential entrance animations
    LaunchedEffect(Unit) {
        delay(200)
        nameFieldVisible = true
        delay(200)
        emailFieldVisible = true
        delay(200)
        passwordFieldVisible = true
        delay(200)
        buttonsVisible = true
    }
    
    var nameFocused by remember { mutableStateOf(false) }
    var emailFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }
    
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Title Section - sans cadre
            AnimatedVisibility(
                visible = nameFieldVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(animationSpec = tween(400)),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        text = "Créer un compte",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier
                            .graphicsLayer(
                                alpha = 0.95f + gradientOffset1 * 0.05f
                            )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rejoignez-nous dès aujourd'hui",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Normal
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
            // Animated Name Field
            AnimatedVisibility(
                visible = nameFieldVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(animationSpec = tween(600)),
                exit = fadeOut()
            ) {
                val nameScale by animateFloatAsState(
                    targetValue = if (nameFocused) 1.02f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "nameScale"
                )
                
                val nameElevation by animateFloatAsState(
                    targetValue = if (nameFocused) 8f else 2f,
                    animationSpec = tween(300),
                    label = "nameElevation"
                )
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(nameScale)
                        .shadow(nameElevation.dp, RoundedCornerShape(16.dp))
                        .onFocusChanged { focusState ->
                            nameFocused = focusState.isFocused
                        },
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Nom complet",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 15.sp
                        )
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (nameFocused) PrimaryYellowDark.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Name",
                                tint = if (nameFocused) PrimaryYellowDark else Color.White.copy(alpha = 0.7f),
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
                        fontWeight = if (nameFocused) FontWeight.SemiBold else FontWeight.Normal
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Animated Email Field
                    AnimatedVisibility(
                visible = emailFieldVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(animationSpec = tween(600)),
                exit = fadeOut()
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

                    Spacer(modifier = Modifier.height(20.dp))

                    // Animated Password Field
                    AnimatedVisibility(
                visible = passwordFieldVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(animationSpec = tween(600)),
                exit = fadeOut()
            ) {
                val passwordScale by animateFloatAsState(
                    targetValue = if (passwordFocused) 1.02f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "passwordScale"
                )
                
                val passwordElevation by animateFloatAsState(
                    targetValue = if (passwordFocused) 8f else 2f,
                    animationSpec = tween(300),
                    label = "passwordElevation"
                )
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(passwordScale)
                        .shadow(passwordElevation.dp, RoundedCornerShape(16.dp))
                        .onFocusChanged { focusState ->
                            passwordFocused = focusState.isFocused
                        },
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Mot de passe",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 15.sp
                        )
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (passwordFocused) PrimaryYellowDark.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = if (passwordFocused) PrimaryYellowDark else Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = PasswordVisualTransformation(),
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
                        fontWeight = if (passwordFocused) FontWeight.SemiBold else FontWeight.Normal
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Animated Register Button
                    AnimatedVisibility(
                visible = buttonsVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(animationSpec = tween(600)),
                exit = fadeOut()
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
                        .fillMaxWidth()
                        .scale(buttonScale)
                        .shadow(buttonElevation.dp, RoundedCornerShape(16.dp))
                ) {
                    Button(
                        onClick = {
                            viewModel.registerUser(name, email, password) {
                                onOpenLoginClicked()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryYellowDark,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "S'inscrire",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    }
                }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Animated Navigate to Login
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
                    onClick = { onprofileClicked() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(textButtonScale),
                    interactionSource = textButtonInteractionSource
                ) {
                    Text(
                        "Vous avez déjà un compte? Se connecter",
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    }
                }
            }

            // Handle Registration State
            when (registrationState) {
                is RegistrationState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 16.dp)
                    )
                }
                is RegistrationState.Success -> {
                    Toast.makeText(
                        LocalContext.current,
                        (registrationState as RegistrationState.Success).message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is RegistrationState.Error -> {
                    Toast.makeText(
                        LocalContext.current,
                        (registrationState as RegistrationState.Error).error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> Unit
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegistrationScreenPreview() {
    RegistrationScreen()
}
