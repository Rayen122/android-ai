package com.example.androidapplication.ui.artiste

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.androidapplication.models.artiste.Artist
import com.example.androidapplication.models.artiste.ArtistViewModel
import com.example.androidapplication.ui.components.BottomNavigationBar
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import com.example.androidapplication.ui.theme.PrimaryYellowLight
import java.net.URLEncoder
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistListScreen(navController: NavController, artistViewModel: ArtistViewModel = viewModel()) {
    val artists by artistViewModel.artists
    val isLoading by artistViewModel.isLoading
    val error by artistViewModel.error
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val famousThemes = listOf("All", "Impressionism", "Renaissance", "Surrealism", "Abstract", "Pop Art")

    val keyboardController = LocalSoftwareKeyboardController.current

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

    // Load artists on first appearance
    LaunchedEffect(Unit) {
        artistViewModel.loadInitialArtists()
    }

    Box(
        modifier = Modifier
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
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 56.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Artists",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier
                        .graphicsLayer(
                            alpha = 0.95f + gradientOffset1 * 0.05f
                        )
                )
            }

            // Search Bar
            OutlinedTextField(
                value = searchText,
                onValueChange = { 
                    searchText = it
                    if (it.isNotEmpty()) {
                        selectedFilter = ""
                        artistViewModel.fetchArtists(it)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 16.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = PrimaryYellowDark.copy(alpha = 0.3f)
                    ),
                placeholder = {
                    Text(
                        text = "Search by theme",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 15.sp
                    )
                },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .background(
                                PrimaryYellowDark.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = PrimaryYellowDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = PrimaryYellowDark,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    containerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White.copy(alpha = 0.9f)
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.White,
                    fontSize = 15.sp
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchText.isNotEmpty()) {
                            selectedFilter = ""
                            artistViewModel.fetchArtists(searchText)
                        }
                        keyboardController?.hide()
                    }
                )
            )

            // Filter Bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(famousThemes) { theme ->
                    FilterButton(
                        text = theme,
                        isSelected = selectedFilter == theme,
                        onClick = {
                            selectedFilter = theme
                            if (theme == "All") {
                                searchText = ""
                                artistViewModel.fetchArtists("")
                            } else {
                                searchText = theme
                                artistViewModel.fetchArtists(theme)
                            }
                            keyboardController?.hide()
                        }
                    )
                }
            }

            // Content Header with count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${artists.size} artist(s)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            // Artists List
            Box(modifier = Modifier.weight(1f)) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryYellowDark)
                    }
                } else if (error != null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error ?: "Error loading artists",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 16.sp
                        )
                    }
                } else if (artists.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No artists found",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 16.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = 8.dp,
                            bottom = 100.dp
                        ),

                                verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(artists) { artist ->
                            ArtistListItem(artist = artist) {
                                val encodedStyleDescription =
                                    URLEncoder.encode(artist.style_description, "UTF-8")
                                val encodedFamousWorks =
                                    URLEncoder.encode(artist.famous_works.joinToString(","), "UTF-8")
                                navController.navigate("artistDetail/${artist.name}/${encodedStyleDescription}/${artist.country}/${encodedFamousWorks}")
                            }
                        }
                    }
                }
            }

            // Bottom Navigation Bar - Fixed at bottom
            BottomNavigationBar(navController = navController)
        }
    }
}

@Composable
fun FilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) PrimaryYellowDark.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.2f)
            )
            .shadow(
                elevation = if (isSelected) 6.dp else 2.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = if (isSelected) PrimaryYellowDark.copy(alpha = 0.4f) else Color.Transparent
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = Color.White
        )
    }
}

@Composable
fun ArtistListItem(artist: Artist, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = PrimaryYellowDark.copy(alpha = 0.3f)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Artist Image with better seed handling
            val imageSeed = artist.name.hashCode().toLong().let { if (it < 0) -it else it }
            AsyncImage(
                model = "https://picsum.photos/seed/$imageSeed/120/120",
                contentDescription = "${artist.name} image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                placeholder = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_gallery),
                error = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_report_image)
            )

            // Artist Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = artist.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.2.sp
                )
                
                Text(
                    text = artist.country,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f),
                    letterSpacing = 0.1.sp
                )
                
                if (artist.style_description.isNotEmpty()) {
                    Text(
                        text = artist.style_description.take(80) + if (artist.style_description.length > 80) "..." else "",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = 20.sp,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
