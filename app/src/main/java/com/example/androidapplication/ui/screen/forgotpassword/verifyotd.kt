package com.example.androidapplication.ui.screen.forgotpassword

import VerifyOtpViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.androidapplication.ui.components.BackButton
import com.example.androidapplication.ui.container.NavGraph
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import com.example.androidapplication.ui.theme.PrimaryYellowLight
@OptIn(ExperimentalMaterial3Api::class) // ✅ explicitly opt-in instead of warning
@Composable
fun VerifyOtpScreen(
    navHost: NavController
) {
    val context = LocalContext.current
    val viewModel: VerifyOtpViewModel = viewModel()

    var otp by remember { mutableStateOf(List(6) { "" }) }
    val focusRequesters = remember { List(6) { FocusRequester() } }

    // Focus on first field when screen loads
    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to PrimaryYellowDark,
                    0.5f to PrimaryYellowLight,
                    1f to Color.White
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back button at top
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                BackButton(navController = navHost)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Verify OTP",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enter the 6-digit code",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // OTP TextFields
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                otp.forEachIndexed { index, value ->
                    OutlinedTextField(
                        value = value,
                        onValueChange = { newValue ->
                            // Only allow single digit
                            if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                                val updatedOtp = otp.toMutableList()
                                updatedOtp[index] = newValue
                                otp = updatedOtp
                                
                                // Auto-focus next field if digit entered
                                if (newValue.isNotEmpty() && index < 5) {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            } else if (newValue.isEmpty()) {
                                // If deleted, move focus to previous field
                                val updatedOtp = otp.toMutableList()
                                updatedOtp[index] = ""
                                otp = updatedOtp
                                if (index > 0) {
                                    focusRequesters[index - 1].requestFocus()
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .width(50.dp)
                            .height(60.dp)
                            .background(Color.White, shape = RoundedCornerShape(12.dp))
                            .focusRequester(focusRequesters[index]),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedTextColor = Color.Black,
                            focusedBorderColor = PrimaryYellowDark,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = PrimaryYellowDark,
                            containerColor = Color.White
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Verify Button
            Button(
                onClick = {
                    val enteredOtp = otp.joinToString("") // join 6 digits
                    viewModel.verifyOtp(
                        otp = enteredOtp,
                        onSuccess = { resetToken ->
                            Toast.makeText(context, "OTP verified!", Toast.LENGTH_SHORT).show()
                            navHost.navigate("${NavGraph.ResetPassword.route}?resetToken=$resetToken")
                        },
                        onError = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryYellowDark,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Verify OTP")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VerifyOtpScreenPreview() {
    val fakeNavController = rememberNavController()
    VerifyOtpScreen(navHost = fakeNavController)
}
