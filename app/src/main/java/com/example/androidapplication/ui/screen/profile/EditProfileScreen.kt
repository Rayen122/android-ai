package com.example.androidapplication.ui.screen.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.androidapplication.R
import com.example.androidapplication.ui.theme.PrimaryGreen
import com.example.androidapplication.ui.theme.PrimaryGreenLight
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import com.example.androidapplication.ui.theme.PrimaryYellowLight
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,

    onprofileClicked: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var usernameFocused by remember { mutableStateOf(false) }
    var emailFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }
    
    // Lottie animation
    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.pencil))
    val lottieAnimationState = rememberLottieAnimatable()

    // Animation states
    var lottieVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var fieldsVisible by remember { mutableStateOf(false) }
    var buttonVisible by remember { mutableStateOf(false) }

    LaunchedEffect(lottieComposition) {
        lottieAnimationState.animate(
            composition = lottieComposition,
            iterations = LottieConstants.IterateForever
        )
    }
    
    LaunchedEffect(Unit) {
        delay(200)
        lottieVisible = true
        delay(300)
        titleVisible = true
        delay(300)
        fieldsVisible = true
        delay(200)
        buttonVisible = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E), // Dark blue
                        Color(0xFF16213E), // Darker blue
                        PrimaryYellowDark.copy(alpha = 0.7f)
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .systemBarsPadding()
    ) {
        // Decorative circles
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(160.dp)
                    .offset(x = 40.dp, y = (-40).dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PrimaryYellowDark.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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
                        .size(150.dp)
                        .padding(bottom = 24.dp)
                        .graphicsLayer(scaleX = lottieScale, scaleY = lottieScale)
                )
            }
            
            // Modern title card
            AnimatedVisibility(
                visible = titleVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it / 2 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(animationSpec = tween(600))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .shadow(16.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        IconButton(
                            onClick = { onprofileClicked() },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_profile),
                                contentDescription = "Back",
                                tint = Color(0xFF1A1A2E),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Modifier le profil",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A1A2E),
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Modern card container for form
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Username Field
                    AnimatedVisibility(
                        visible = fieldsVisible,
                        enter = slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn(animationSpec = tween(600))
                    ) {
                        TextFieldWithLabel(
                            label = "Nom d'utilisateur",
                            value = username,
                            onValueChange = { username = it },
                            icon = Icons.Default.Person,
                            focused = usernameFocused,
                            onFocusChanged = { usernameFocused = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Email Field
                    AnimatedVisibility(
                        visible = fieldsVisible,
                        enter = slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn(animationSpec = tween(600, delayMillis = 100))
                    ) {
                        TextFieldWithLabel(
                            label = "Email",
                            value = email,
                            onValueChange = { email = it },
                            icon = Icons.Default.Email,
                            focused = emailFocused,
                            onFocusChanged = { emailFocused = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Password Field
                    AnimatedVisibility(
                        visible = fieldsVisible,
                        enter = slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn(animationSpec = tween(600, delayMillis = 200))
                    ) {
                        TextFieldWithLabel(
                            label = "Mot de passe",
                            value = password,
                            onValueChange = { password = it },
                            icon = Icons.Default.Lock,
                            focused = passwordFocused,
                            onFocusChanged = { passwordFocused = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            isPassword = true
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save Button
                    AnimatedVisibility(
                        visible = buttonVisible,
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
                            onClick = { /* Save profile */ },
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
                                "Enregistrer",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    focused: Boolean = false,
    onFocusChanged: (Boolean) -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isPassword: Boolean = false
) {
    val scale by animateFloatAsState(
        targetValue = if (focused) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fieldScale"
    )
    
    val elevation by animateFloatAsState(
        targetValue = if (focused) 8f else 2f,
        animationSpec = tween(300),
        label = "fieldElevation"
    )
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(elevation.dp, RoundedCornerShape(16.dp))
            .onFocusChanged { focusState ->
                onFocusChanged(focusState.isFocused)
            },
        singleLine = true,
        placeholder = {
            Text(
                text = label,
                color = Color.Gray.copy(alpha = 0.6f),
                fontSize = 15.sp
            )
        },
        leadingIcon = icon?.let {
            {
                Box(
                    modifier = Modifier
                        .background(
                            if (focused) PrimaryYellowDark.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = it,
                        contentDescription = label,
                        tint = if (focused) PrimaryYellowDark else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = keyboardOptions,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = PrimaryYellowDark,
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
            containerColor = Color.White,
            focusedTextColor = Color(0xFF1A1A2E),
            unfocusedTextColor = Color(0xFF1A1A2E)
        ),
        textStyle = androidx.compose.ui.text.TextStyle(
            color = Color(0xFF1A1A2E),
            fontSize = 16.sp,
            fontWeight = if (focused) FontWeight.SemiBold else FontWeight.Normal
        ),
        shape = RoundedCornerShape(16.dp)
    )
}
