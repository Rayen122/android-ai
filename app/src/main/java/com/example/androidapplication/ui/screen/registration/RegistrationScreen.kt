package com.example.androidapplication.ui.screen.registration

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidapplication.models.register.RegistrationState
import com.example.androidapplication.models.register.RegistrationViewModel
import com.example.androidapplication.ui.components.ActionButton
import com.example.androidapplication.ui.theme.DarkTextColor
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import com.example.androidapplication.ui.theme.PrimaryYellowLight

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
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to PrimaryYellowDark,
                    0.5f to PrimaryYellowLight,
                    1f to Color.White
                )
            )
            .systemBarsPadding()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Removed Lottie Animation Section

            // Name Field
            BasicTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 17.dp),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (name.isEmpty()) {
                            Text(text = "Name", color = Color.Gray)
                        }
                        innerTextField()
                    }
                }
            )

            // Email Field
            BasicTextField(
                value = email,
                onValueChange = { email = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 17.dp),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (email.isEmpty()) {
                            Text(text = "Email", color = Color.Gray)
                        }
                        innerTextField()
                    }
                }
            )

            // Password Field
            BasicTextField(
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 17.dp),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (password.isEmpty()) {
                            Text(text = "Password", color = Color.Gray)
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Register Button
            ActionButton(
                text = "Register",
                isNavigationArrowVisible = true,
                onClicked = {
                    viewModel.registerUser(name, email, password) {
                        onOpenLoginClicked()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryYellowDark,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Navigate to Login
            TextButton(
                onClick = { onprofileClicked() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Already have an account? Login",
                    color = DarkTextColor
                )
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
