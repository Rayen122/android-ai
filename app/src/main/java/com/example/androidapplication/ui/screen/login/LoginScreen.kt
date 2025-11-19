package com.example.androidapplication.ui.screen.login

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidapplication.models.login.LoginState
import com.example.androidapplication.models.login.LoginViewModel
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import com.example.androidapplication.ui.theme.PrimaryYellowLight
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginClicked: () -> Unit = {},
    onRegistrationClicked: () -> Unit = {},
    onForgotPasswordClicked: () -> Unit = {}
) {
    val viewModel = remember { LoginViewModel() }
    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsState()
    
    // Animation states - animations plus rapides
    var titleVisible by remember { mutableStateOf(false) }
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
    
    // Rotation animation for background elements
    val backgroundRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "backgroundRotation"
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
    
    // Text shimmer animation - plus lent
    val textShimmer by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "textShimmer"
    )
    
    // Handle side-effects safely
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                Toast.makeText(context, (loginState as LoginState.Success).message, Toast.LENGTH_SHORT).show()
                onLoginClicked()
            }
            is LoginState.Error -> {
                Toast.makeText(context, (loginState as LoginState.Error).error, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }
    
    // Sequential entrance animations - plus rapides
    LaunchedEffect(Unit) {
        titleVisible = true
        delay(150)
        emailFieldVisible = true
        delay(100)
        passwordFieldVisible = true
        delay(100)
        buttonsVisible = true
    }
    
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Title Section - sans cadre
            AnimatedVisibility(
                visible = titleVisible,
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
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    val titleScale by animateFloatAsState(
                        targetValue = if (titleVisible) 1f else 0.8f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "titleScale"
                    )
                    
                    Text(
                        text = "Welcome Back!",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.sp,
                        modifier = Modifier
                            .graphicsLayer(
                                scaleX = titleScale,
                                scaleY = titleScale,
                                alpha = 0.95f + gradientOffset1 * 0.05f
                            )
                    )
                    
                    Text(
                        text = "Connectez-vous à votre compte",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier
                            .graphicsLayer(
                                scaleX = titleScale,
                                scaleY = titleScale
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var passwordVisible by remember { mutableStateOf(false) }
            var emailFocused by remember { mutableStateOf(false) }
            var passwordFocused by remember { mutableStateOf(false) }
            
            // Form container - sans cadre
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Animated Email Field with focus animation - plus réactif
                AnimatedVisibility(
                    visible = emailFieldVisible,
                    enter = slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessHigh
                        )
                    ) + fadeIn(animationSpec = tween(300)),
                    exit = fadeOut()
                ) {
                    val emailScale by animateFloatAsState(
                        targetValue = if (emailFocused) 1.03f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessHigh
                        ),
                        label = "emailScale"
                    )
                    
                    val emailElevation by animateFloatAsState(
                        targetValue = if (emailFocused) 12f else 4f,
                        animationSpec = tween(200),
                        label = "emailElevation"
                    )
                    
                    val emailGlow by animateFloatAsState(
                        targetValue = if (emailFocused) 1f else 0f,
                        animationSpec = tween(200),
                        label = "emailGlow"
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(emailScale)
                            .shadow(
                                elevation = emailElevation.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = PrimaryYellowDark.copy(alpha = emailGlow * 0.5f)
                            )
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier
                                .fillMaxWidth()
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
                            val iconRotation by animateFloatAsState(
                                targetValue = if (emailFocused) 360f else 0f,
                                animationSpec = tween(500, easing = FastOutSlowInEasing),
                                label = "iconRotation"
                            )
                            val iconScale by animateFloatAsState(
                                targetValue = if (emailFocused) 1.2f else 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessHigh
                                ),
                                label = "iconScale"
                            )
                            val iconGlow by animateFloatAsState(
                                targetValue = if (emailFocused) glowPulse else 0f,
                                animationSpec = tween(2000),
                                label = "iconGlow"
                            )
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (emailFocused) 
                                            PrimaryYellowDark.copy(alpha = 0.15f + iconGlow * 0.1f) 
                                        else 
                                            Color.White.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                                    .shadow(
                                        elevation = if (emailFocused) (4f + iconGlow * 4f).dp else 0.dp,
                                        shape = RoundedCornerShape(8.dp),
                                        spotColor = PrimaryYellowDark.copy(alpha = iconGlow * 0.5f)
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = if (emailFocused) PrimaryYellowDark else Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .graphicsLayer(
                                            rotationZ = iconRotation,
                                            scaleX = iconScale,
                                            scaleY = iconScale
                                        )
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
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = if (emailFocused) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))

                // Animated Password Field with focus animation - plus réactif
                AnimatedVisibility(
                    visible = passwordFieldVisible,
                    enter = slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessHigh
                        )
                    ) + fadeIn(animationSpec = tween(300)),
                    exit = fadeOut()
                ) {
                    val passwordScale by animateFloatAsState(
                        targetValue = if (passwordFocused) 1.03f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessHigh
                        ),
                        label = "passwordScale"
                    )
                    
                    val passwordElevation by animateFloatAsState(
                        targetValue = if (passwordFocused) 12f else 4f,
                        animationSpec = tween(200),
                        label = "passwordElevation"
                    )
                    
                    val passwordGlow by animateFloatAsState(
                        targetValue = if (passwordFocused) 1f else 0f,
                        animationSpec = tween(200),
                        label = "passwordGlow"
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(passwordScale)
                            .shadow(
                                elevation = passwordElevation.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = PrimaryYellowDark.copy(alpha = passwordGlow * 0.5f)
                            )
                    ) {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier
                                .fillMaxWidth()
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
                            val iconRotation by animateFloatAsState(
                                targetValue = if (passwordFocused) 360f else 0f,
                                animationSpec = tween(500, easing = FastOutSlowInEasing),
                                label = "lockRotation"
                            )
                            val iconScale by animateFloatAsState(
                                targetValue = if (passwordFocused) 1.2f else 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessHigh
                                ),
                                label = "iconScale"
                            )
                            val iconGlow by animateFloatAsState(
                                targetValue = if (passwordFocused) glowPulse else 0f,
                                animationSpec = tween(2000),
                                label = "iconGlow"
                            )
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (passwordFocused) 
                                            PrimaryYellowDark.copy(alpha = 0.15f + iconGlow * 0.1f) 
                                        else 
                                            Color.White.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                                    .shadow(
                                        elevation = if (passwordFocused) (4f + iconGlow * 4f).dp else 0.dp,
                                        shape = RoundedCornerShape(8.dp),
                                        spotColor = PrimaryYellowDark.copy(alpha = iconGlow * 0.5f)
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Password",
                                    tint = if (passwordFocused) PrimaryYellowDark else Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .graphicsLayer(
                                            rotationZ = iconRotation,
                                            scaleX = iconScale,
                                            scaleY = iconScale
                                        )
                                )
                            }
                        },
                        trailingIcon = {
                            val iconInteractionSource = remember { MutableInteractionSource() }
                            val isPressed by iconInteractionSource.collectIsPressedAsState()
                            val iconScale by animateFloatAsState(
                                targetValue = if (isPressed) 0.8f else 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessHigh
                                ),
                                label = "visibilityScale"
                            )
                            
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible },
                                interactionSource = iconInteractionSource,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = PrimaryYellowDark,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .scale(iconScale)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = PrimaryYellowDark,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                            containerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White.copy(alpha = 0.9f)
                        ),
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = if (passwordFocused) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
                
                var rememberMe by remember { mutableStateOf(false) }

                AnimatedVisibility(
                    visible = passwordFieldVisible,
                    enter = fadeIn(animationSpec = tween(400)) + slideInVertically(
                        initialOffsetY = { 20 },
                        animationSpec = tween(400)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
                    ) {
                        val checkboxScale by animateFloatAsState(
                            targetValue = if (rememberMe) 1.1f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessHigh
                            ),
                            label = "checkboxScale"
                        )
                        
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            modifier = Modifier.scale(checkboxScale),
                            colors = CheckboxDefaults.colors(
                                checkedColor = PrimaryYellowDark,
                                uncheckedColor = Color.White.copy(alpha = 0.7f)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Se souvenir de moi",
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))

                // Animated Forgot Password button
                AnimatedVisibility(
                    visible = passwordFieldVisible,
                    enter = fadeIn(animationSpec = tween(400)) + slideInHorizontally(
                        initialOffsetX = { 20 },
                        animationSpec = tween(400)
                    )
                ) {
                    val forgotInteractionSource = remember { MutableInteractionSource() }
                    val isPressed by forgotInteractionSource.collectIsPressedAsState()
                    val forgotScale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessHigh
                        ),
                        label = "forgotScale"
                    )
                    
                    TextButton(
                        onClick = onForgotPasswordClicked,
                        modifier = Modifier
                            .align(Alignment.End)
                            .scale(forgotScale),
                        interactionSource = forgotInteractionSource
                    ) {
                        Text(
                            "Mot de passe oublié?",
                            color = PrimaryYellowDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Animated Login Button with shimmer effect - plus dynamique
                AnimatedVisibility(
                    visible = buttonsVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessHigh
                        )
                    ) + fadeIn(animationSpec = tween(300)),
                    exit = fadeOut()
                ) {
                    val loginInteractionSource = remember { MutableInteractionSource() }
                    val isPressed by loginInteractionSource.collectIsPressedAsState()
                    val loginScale by animateFloatAsState(
                        targetValue = if (isPressed) 0.96f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = 2000f // Very high stiffness for quick response
                        ),
                        label = "loginScale"
                    )
                    
                    val buttonElevation by animateFloatAsState(
                        targetValue = if (isPressed) 6f else 16f,
                        animationSpec = tween(150),
                        label = "buttonElevation"
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .scale(loginScale)
                            .shadow(
                                elevation = buttonElevation.dp,
                                shape = RoundedCornerShape(18.dp),
                                spotColor = PrimaryYellowDark.copy(alpha = glowPulse * 0.3f)
                            )
                    ) {
                        // Enhanced shimmer effect overlay
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
                                        start = Offset(
                                            shimmerOffset * 1000f,
                                            0f
                                        ),
                                        end = Offset(
                                            shimmerOffset * 1000f + 400f,
                                            100f
                                        )
                                    ),
                                    shape = RoundedCornerShape(18.dp)
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
                                    shape = RoundedCornerShape(18.dp)
                                )
                        )
                        
                        Button(
                            onClick = { viewModel.loginUser(email, password, context, rememberMe) },
                            modifier = Modifier
                                .fillMaxSize(),
                            interactionSource = loginInteractionSource,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryYellowDark.copy(alpha = 0.95f + glowPulse * 0.05f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Text(
                                "Se connecter",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                                modifier = Modifier
                                    .graphicsLayer(
                                        alpha = 1f + (glowPulse - 0.75f) * 0.1f
                                    )
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Animated Register Button - plus dynamique
            AnimatedVisibility(
                visible = buttonsVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                ) + fadeIn(animationSpec = tween(300, delayMillis = 50)),
                exit = fadeOut()
            ) {
                val registerInteractionSource = remember { MutableInteractionSource() }
                val isPressed by registerInteractionSource.collectIsPressedAsState()
                val registerScale by animateFloatAsState(
                    targetValue = if (isPressed) 0.96f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = 2000f // Very high stiffness for quick response
                    ),
                    label = "registerScale"
                )
                
                val registerElevation by animateFloatAsState(
                    targetValue = if (isPressed) 6f else 16f,
                    animationSpec = tween(150),
                    label = "registerElevation"
                )
                
                val borderWidth by animateFloatAsState(
                    targetValue = if (isPressed) 3f else 2.5f,
                    animationSpec = tween(150),
                    label = "borderWidth"
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .scale(registerScale)
                        .shadow(
                            elevation = registerElevation.dp,
                            shape = RoundedCornerShape(18.dp),
                            spotColor = PrimaryYellowDark.copy(alpha = glowPulse * 0.2f)
                        )
                ) {
                    // Subtle shimmer for register button
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        PrimaryYellowLight.copy(alpha = 0.2f * (shimmerOffset + 1f) / 2f),
                                        Color.Transparent
                                    ),
                                    start = Offset(
                                        shimmerOffset * 800f,
                                        0f
                                    ),
                                    end = Offset(
                                        shimmerOffset * 800f + 300f,
                                        100f
                                    )
                                ),
                                shape = RoundedCornerShape(18.dp)
                            )
                    )
                    
                    Button(
                        onClick = onRegistrationClicked,
                        modifier = Modifier
                            .fillMaxSize(),
                        interactionSource = registerInteractionSource,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = PrimaryYellowDark
                        ),
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            borderWidth.dp,
                            PrimaryYellowDark.copy(
                                alpha = (0.9f + gradientOffset1 * 0.1f) * (0.8f + glowPulse * 0.2f)
                            )
                        )
                    ) {
                        Text(
                            "Créer un compte",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            modifier = Modifier
                                .graphicsLayer(
                                    alpha = 1f + (glowPulse - 0.75f) * 0.1f
                                )
                        )
                    }
                }
            }
            } // Close Column
        }
    }



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
