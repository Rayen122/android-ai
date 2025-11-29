package com.example.androidapplication.ui.artiste

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.androidapplication.ui.components.BackButton
import com.example.androidapplication.ui.components.BottomNavigationBar
import com.example.androidapplication.ui.theme.PrimaryYellow
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import com.example.androidapplication.ui.theme.PrimaryYellowLight
import java.net.URLDecoder
import kotlin.math.cos
import kotlin.math.sin

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.androidapplication.models.artiste.Artwork

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun ArtistDetailScreen(
    navController: NavController,
    artistViewModel: com.example.androidapplication.models.artiste.ArtistViewModel,
    artistName: String
) {
    // Retrieve artist from ViewModel
    val artists by artistViewModel.artists
    val artist = artists.find { it.name == artistName }
    
    // If artist is not found (shouldn't happen if flow is correct), show loading or error
    if (artist == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryYellow)
        }
        return
    }

    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0F1E),
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E)
                    )
                )
            )
    ) {
        // Simplified decorative elements (Static to prevent ANR)
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top right circle
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(200.dp)
                    .offset(x = 50.dp, y = (-50).dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PrimaryYellowDark.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
            
            // Bottom left circle
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(160.dp)
                    .offset(x = (-30).dp, y = 100.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PrimaryYellowLight.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
        
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Section - Large Image (Fixed)
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(400))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {
                    // Navigation buttons at top
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp)
                            .zIndex(1f), // Ensure buttons are on top
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BackButton(
                            navController = navController,
                            onClick = {
                                navController.popBackStack()
                            }
                        )

                        IconButton(
                            onClick = { /* Share functionality */ },
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    PrimaryYellowDark.copy(alpha = 0.9f),
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Large image
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 0.dp) // Remove top padding to let image fill top
                            .clip(
                                RoundedCornerShape(
                                    bottomStart = 40.dp,
                                    bottomEnd = 40.dp
                                )
                            )
                    ) {
                        AsyncImage(
                            model = artist.image_url,
                            contentDescription = "${artist.name}'s portrait",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_gallery),
                            error = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_report_image)
                        )
                        
                        // Gradient overlay for text readability at bottom of image
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0xFF0F0F1E)
                                        ),
                                        startY = 300f
                                    )
                                )
                        )
                    }
                }
            }

            // Bottom Section - Details (Scrollable)
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(initialOffsetY = { it / 4 }) + fadeIn()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Artist Name and Country
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = artist.name,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                        
                        Text(
                            text = artist.country,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryYellow,
                            letterSpacing = 0.1.sp
                        )
                    }

                    // About the Artist section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "About the artist",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = artist.style_description,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.8f),
                            lineHeight = 24.sp
                        )
                    }

                    // Famous Works section
                    if (artist.famous_works.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Popular Artworks",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(end = 16.dp, bottom = 16.dp)
                            ) {
                                items(artist.famous_works) { work ->
                                    ArtworkCard(artwork = work)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
} // Bottom Navigation Bar - Fixed at bottom



@Composable
fun ArtworkCard(artwork: Artwork) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(220.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = artwork.image_url,
                contentDescription = artwork.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Gradient overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 100f
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = artwork.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2
                )
                Text(
                    text = artwork.year,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }
    }
}
