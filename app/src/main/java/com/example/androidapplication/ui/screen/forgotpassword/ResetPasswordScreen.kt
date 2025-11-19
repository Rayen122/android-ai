package com.example.androidapplication.ui.screen.resetpassword

import ResetPasswordViewModel
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
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import com.example.androidapplication.ui.theme.PrimaryYellowLight
import com.example.androidapplication.ui.container.NavGraph
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    navHost: NavController,
    resetToken: String
) {
    val context = LocalContext.current
    val viewModel: ResetPasswordViewModel = viewModel()

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var newPasswordFocused by remember { mutableStateOf(false) }
    var confirmPasswordFocused by remember { mutableStateOf(false) }
    
    // Animation states
    var titleVisible by remember { mutableStateOf(false) }
    var fieldsVisible by remember { mutableStateOf(false) }
    var buttonsVisible by remember { mutableStateOf(false) }
    
    // Animated gradient background
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientOffset"
    )
    
    LaunchedEffect(Unit) {
        delay(200)
        titleVisible = true
        delay(300)
        fieldsVisible = true
        delay(200)
        buttonsVisible = true
    }

    Box(
        modifier = Modifier
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
            .padding(24.dp)
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Modern title card
            AnimatedVisibility(
                visible = titleVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(animationSpec = tween(800)),
                exit = fadeOut()
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Réinitialiser le mot de passe",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A1A2E),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Entrez votre nouveau mot de passe",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Normal
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
                    // New Password TextField
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
                        val newPasswordScale by animateFloatAsState(
                            targetValue = if (newPasswordFocused) 1.02f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "newPasswordScale"
                        )
                        
                        val newPasswordElevation by animateFloatAsState(
                            targetValue = if (newPasswordFocused) 8f else 2f,
                            animationSpec = tween(300),
                            label = "newPasswordElevation"
                        )
                        
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(newPasswordScale)
                                .shadow(newPasswordElevation.dp, RoundedCornerShape(16.dp))
                                .onFocusChanged { focusState ->
                                    newPasswordFocused = focusState.isFocused
                                },
                            singleLine = true,
                            placeholder = {
                                Text(
                                    text = "Nouveau mot de passe",
                                    color = Color.Gray.copy(alpha = 0.6f),
                                    fontSize = 15.sp
                                )
                            },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (newPasswordFocused) PrimaryYellowDark.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Password",
                                        tint = if (newPasswordFocused) PrimaryYellowDark else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            visualTransformation = PasswordVisualTransformation(),
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
                                fontWeight = if (newPasswordFocused) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Confirm Password TextField
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
                        val confirmPasswordScale by animateFloatAsState(
                            targetValue = if (confirmPasswordFocused) 1.02f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "confirmPasswordScale"
                        )
                        
                        val confirmPasswordElevation by animateFloatAsState(
                            targetValue = if (confirmPasswordFocused) 8f else 2f,
                            animationSpec = tween(300),
                            label = "confirmPasswordElevation"
                        )
                        
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(confirmPasswordScale)
                                .shadow(confirmPasswordElevation.dp, RoundedCornerShape(16.dp))
                                .onFocusChanged { focusState ->
                                    confirmPasswordFocused = focusState.isFocused
                                },
                            singleLine = true,
                            placeholder = {
                                Text(
                                    text = "Confirmer le mot de passe",
                                    color = Color.Gray.copy(alpha = 0.6f),
                                    fontSize = 15.sp
                                )
                            },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (confirmPasswordFocused) PrimaryYellowDark.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Confirm Password",
                                        tint = if (confirmPasswordFocused) PrimaryYellowDark else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            visualTransformation = PasswordVisualTransformation(),
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
                                fontWeight = if (confirmPasswordFocused) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Reset Button
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
                                if (newPassword != confirmPassword) {
                                    Toast.makeText(context, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                viewModel.resetPassword(
                                    newPassword = newPassword,
                                    resetToken = resetToken,
                                    onSuccess = { message ->
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                                        // Navigate to Login screen and clear backstack
                                        navHost.navigate(NavGraph.Login.route) {
                                            popUpTo(0)
                                        }
                                    },
                                    onError = { error ->
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                    }
                                )
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
                                "Réinitialiser",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Back to Login Button
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
                            onClick = { navHost.navigate(NavGraph.Login.route) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(textButtonScale),
                            interactionSource = textButtonInteractionSource
                        ) {
                            Text(
                                text = "Retour à la connexion",
                                color = Color(0xFF1A1A2E),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResetPasswordScreenPreview() {
    val fakeNavController = rememberNavController()
    ResetPasswordScreen(
        navHost = fakeNavController,
        resetToken = "dummy_token"
    )
}
