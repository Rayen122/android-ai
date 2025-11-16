package com.example.androidapplication.ui.screen.welcome

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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

    // Your Lottie + UI animation code remains the same
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.pencil))
    var animationOffset by remember { mutableStateOf(-300.dp) }
    val animatedOffset by androidx.compose.animation.core.animateDpAsState(
        targetValue = animationOffset,
        animationSpec = tween(durationMillis = 7500)
    )
    var isTitleVisible by remember { mutableStateOf(false) }
    var isButtonVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isTitleVisible = true
        animationOffset = 0.dp
        isButtonVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to PrimaryYellowDark,
                    0.6f to PrimaryYellowLight,
                    1f to PrimaryYellowLight,
                )
            )
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(54.dp))
        AnimatedVisibility(
            visible = isTitleVisible,
            enter = fadeIn(animationSpec = tween(2000))
        ) {
            Text(
                text = "Create art with ease!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = DarkTextColor
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        LottieAnimation(
            composition = composition,
            iterations = Int.MAX_VALUE,
            modifier = Modifier
                .size(300.dp)
                .offset(x = animatedOffset)
        )

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = isButtonVisible,
            enter = slideInVertically(animationSpec = tween(2500), initialOffsetY = { it })
        ) {
            ActionButton(
                text = "Next",
                isNavigationArrowVisible = true,
                onClicked = { navigateTo?.invoke() }, // Navigate based on token
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryYellowDark,
                    contentColor = PrimaryYellowLight
                ),
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}
