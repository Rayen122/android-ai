package com.example.androidapplication.ui.screen.forgotpassword

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
import com.example.androidapplication.ui.screen.registration.RegistrationScreen
import com.example.androidapplication.ui.theme.PrimaryYellowDark

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    onRestPasswordClicked: (String) -> Unit = {} ,
    onBackToLoginClicked: () -> Unit = {} // 👈 new callback

)
 {
    val context = LocalContext.current
    val viewModel = remember { ForgotPasswordViewModel() } // ViewModel instance

    // Lottie animation setup
    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.brain))
    val lottieAnimationState = rememberLottieAnimatable()

    LaunchedEffect(lottieComposition) {
        lottieAnimationState.animate(
            composition = lottieComposition,
            iterations = LottieConstants.IterateForever
        )
    }

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
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Lottie Animation
            LottieAnimation(
                composition = lottieComposition,
                progress = { lottieAnimationState.progress },
                modifier = Modifier
                    .size(190.dp)
                    .padding(bottom = 32.dp) // Adjust padding between animation and form
            )

            Text(
                text = "Forgot Password?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Don't worry .this happen by age.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Email Input Field
            var email by remember { mutableStateOf("") }
            BasicTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.White),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        if (email.isEmpty()) {
                            Text(text = "Your email", color = Color.Gray)
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    if (email.isNotEmpty()) {
                        viewModel.sendResetLink(
                            email = email,
                            onSuccess = {
                                Toast.makeText(context, "OTP sent to $email", Toast.LENGTH_SHORT).show()
                                onRestPasswordClicked(email)
                            },
                            onError = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryYellowDark, // background color
                    contentColor = Color.White           // text color
                ),
                shape = RoundedCornerShape(18.dp) // optional rounded corners
            ) {
                Text("Send Reset Link")
            }


            Spacer(modifier = Modifier.height(8.dp))

            // Back to Login Button
            TextButton(
                onClick = { onBackToLoginClicked() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Back to Login", color = Color.Black)
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen()
}