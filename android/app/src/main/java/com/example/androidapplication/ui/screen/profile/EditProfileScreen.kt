package com.example.androidapplication.ui.screen.profile

import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.androidapplication.ui.theme.PrimaryYellowLight

@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,

    onprofileClicked: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
// Lottie animation
    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.pencil))
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
                    0f to PrimaryGreenLight,
                    0.5f to PrimaryYellowLight,
                    1f to Color.White
                )
            )
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()

                .padding(16.dp)
        ) {
            // Lottie Animation
            LottieAnimation(
                composition = lottieComposition,
                progress = { lottieAnimationState.progress },
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 32.dp) // Adjust padding between animation and form
            )
            // Header with back button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onprofileClicked() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Edit Profile",
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Username Field
            TextFieldWithLabel(
                label = "Username",
                value = username,
                onValueChange = { username = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            TextFieldWithLabel(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Field
            TextFieldWithLabel(
                label = "password",
                value = password
                ,
                onValueChange = { password = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button

        }
    }
}

@Composable
fun TextFieldWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions.Default,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = MaterialTheme.shapes.small)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}
