package com.example.androidapplication.ui.screen.welcome

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.androidapplication.R
import com.example.androidapplication.ui.components.ActionButton
import com.example.androidapplication.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onOpenLoginClicked: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.pencil))

    // Flag indiquant si la composition est attachée / mesurée au moins une fois:
    var isLayoutAttached by remember { mutableStateOf(false) }

    // Contrôle lecture Lottie pour pouvoir l'arrêter proprement:
    var isPlaying by remember { mutableStateOf(true) }

    // Offset animé (mais on ne le changera que quand l'écran est attaché)
    var animationOffset by remember { mutableStateOf(-300.dp) }
    val animatedOffset by animateDpAsState(
        targetValue = animationOffset,
        animationSpec = tween(durationMillis = 7500),
        label = "offsetAnim"
    )

    var isTitleVisible by remember { mutableStateOf(false) }
    var isButtonVisible by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // On démarre les animations *seulement* après que le layout a été positionné (attach)
    LaunchedEffect(isLayoutAttached) {
        if (!isLayoutAttached) return@LaunchedEffect
        // petit délai supplémentaire pour laisser Compose se stabiliser
        delay(120)
        isTitleVisible = true
        animationOffset = 0.dp
        delay(200)
        isButtonVisible = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coords ->
                // la première fois qu'on obtient des coordonnées, la vue est attachée au ViewRoot
                if (!isLayoutAttached) isLayoutAttached = true
            }
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
            enter = fadeIn(animationSpec = tween(2000)),
            exit = fadeOut(animationSpec = tween(1000))
        ) {
            Text(
                text = "Create art with ease!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 21.dp),
                color = DarkTextColor
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.weight(1f))

        // Lottie: on ne l'affiche que si la composition est prête
        if (composition != null) {
            LottieAnimation(
                composition = composition,
                isPlaying = isPlaying,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .size(300.dp)
                    .offset(x = animatedOffset)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = isButtonVisible,
            enter = slideInVertically(
                animationSpec = tween(2500),
                initialOffsetY = { it }
            )
        ) {
            ActionButton(
                text = "Next",
                isNavigationArrowVisible = true,
                onClicked = {
                    // stoppe les animations AVANT la navigation pour éviter le remeasure sur node détaché
                    scope.launch {
                        isPlaying = false
                        // fermer visibilités pour déclencher sorties
                        isButtonVisible = false
                        isTitleVisible = false
                        delay(250) // laisse finir les animations de sortie
                        onOpenLoginClicked()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryYellowDark,
                    contentColor = PrimaryYellowLight
                ),
                modifier = Modifier.padding(24.dp)
            )
        }
    }

    // Nettoyage final si le composable est détruit
    DisposableEffect(Unit) {
        onDispose {
            isPlaying = false
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(onOpenLoginClicked = {})
}
