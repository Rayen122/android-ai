package com.example.androidapplication.ui.screen.studio

<<<<<<< HEAD
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
=======
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
<<<<<<< HEAD
import androidx.compose.material.icons.filled.Edit
=======
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
<<<<<<< HEAD
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
=======
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
<<<<<<< HEAD
import com.example.androidapplication.ui.components.BackButton
import com.example.androidapplication.ui.components.BottomNavigationBar
import com.example.androidapplication.ui.container.NavGraph
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import com.example.androidapplication.ui.theme.PrimaryYellowLight
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StudioScreen(navController: NavController) {
    // Animation states
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
    
    val particleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleOffset"
    )
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )
    
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

=======
import com.example.androidapplication.ui.components.BottomNavigationBar
import com.example.androidapplication.ui.container.NavGraph

@Composable
fun StudioScreen(navController: NavController) {
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        containerColor = Color(0xFF0F0F1E)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F0F1E),
                            Color(0xFF1A1A2E),
<<<<<<< HEAD
                            Color(0xFF16213E),
                            PrimaryYellowDark.copy(alpha = 0.3f + gradientOffset1 * 0.2f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
=======
                            Color(0xFF16213E)
                        )
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
                    )
                )
                .padding(paddingValues)
        ) {
<<<<<<< HEAD
            // Animated decorative elements
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top right animated circle
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
                            shape = CircleShape
                        )
                        .graphicsLayer(alpha = 0.6f + gradientOffset1 * 0.1f)
                )
                
                // Bottom left animated circle
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
                            shape = CircleShape
                        )
                        .graphicsLayer(alpha = 0.5f + gradientOffset2 * 0.15f)
                )
                
                // Floating particles
                repeat(8) { index ->
                    val angle = (particleOffset + index * 45f) * (Math.PI / 180f)
                    val radius = 100.dp + (gradientOffset1 * 40).dp + (index * 8).dp
                    val particleX = (cos(angle) * radius.value).dp
                    val particleY = (sin(angle) * radius.value).dp
                    val particleSize = (5.dp + (gradientOffset1 * 3).dp + ((index % 3) * 1.5).dp)
                    val particleAlpha = (0.25f + glowPulse * 0.15f - (index * 0.02f)).coerceIn(0.15f, 0.5f)
                    
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
                                shape = CircleShape
                            )
                            .graphicsLayer(
                                alpha = particleAlpha,
                                scaleX = pulseScale,
                                scaleY = pulseScale
                            )
                            .shadow(
                                elevation = 2.dp,
                                shape = CircleShape,
                                spotColor = particleColor.copy(alpha = 0.3f)
                            )
                    )
                }
                
                // Wave effect layers
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

=======
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
<<<<<<< HEAD
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    BackButton(navController = navController)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Studio",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
=======
                Text(
                    text = "Studio",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Card 1: Magic Paintbrush
                    StudioCard(
<<<<<<< HEAD
                        title = "My painting",
=======
                        title = "Magic Paintbrush",
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
                        subtitle = "Draw your soulmate",
                        icon = Icons.Default.Brush,
                        onClick = { navController.navigate(NavGraph.MagicPaintbrush.route) }
                    )

                    // Card 2: Draw Your Mood
                    StudioCard(
<<<<<<< HEAD
                        title = "Coloring",
                        subtitle = "Visualize your daily mood",
                        icon = Icons.Default.Mood,
                        onClick = { navController.navigate(NavGraph.SketchSearch.route) }
=======
                        title = "Draw Your Mood",
                        subtitle = "Visualize your daily mood",
                        icon = Icons.Default.Mood,
                        onClick = { /* TODO: Navigate to Draw Your Mood */ }
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
                    )

                    // Card 3: Image Référence
                    StudioCard(
                        title = "Image Référence",
                        subtitle = "Trouvez l'inspiration",
                        icon = Icons.Default.Image,
                        onClick = { navController.navigate(NavGraph.Genrerai.route) }
                    )
<<<<<<< HEAD

                    // Card 4: Painting Process (Making-Of)
                    StudioCard(
                        title = "Painting Process",
                        subtitle = "Visualize the creation steps",
                        icon = Icons.Default.Brush, // Or use a more specific icon if available
                        onClick = { navController.navigate(NavGraph.PaintingProcess.route) }
                    )

                    // Card 5: Sketch & Trace
                    StudioCard(
                        title = "Sketch & Trace",
                        subtitle = "Draw over your photos",
                        icon = Icons.Default.Edit,
                        onClick = { navController.navigate(NavGraph.Sketch.route) }
                    )
=======
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
                }
            }
        }
    }
}

@Composable
fun StudioCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(
<<<<<<< HEAD
                color = Color.Transparent,
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.3f),
=======
                color = Color.White,
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
<<<<<<< HEAD
                color = Color.White
=======
                color = Color.Black
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                fontSize = 16.sp,
<<<<<<< HEAD
                color = Color.LightGray
=======
                color = Color.Gray
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
            )
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(40.dp)
                .background(Color(0xFFFFF3E0), RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
             Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFFFDA858)
            )
        }
    }
}

