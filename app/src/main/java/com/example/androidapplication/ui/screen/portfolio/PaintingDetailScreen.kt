package com.example.androidapplication.ui.screen.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.androidapplication.models.MagicUpgradeViewModel
import com.example.androidapplication.models.PhotoViewModel
import com.example.androidapplication.ui.components.BackButton
import com.example.androidapplication.ui.container.NavGraph
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun PaintingDetailScreen(
    navController: NavController,
    paintingId: String,
    photoViewModel: PhotoViewModel = viewModel(key = "shared_photo_viewmodel")
) {
    val myPhotos by photoViewModel.myPhotos.observeAsState(initial = emptyList())
    
    val painting = remember(myPhotos, paintingId) {
        myPhotos.find { it.id == paintingId }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Dark background for immersive view
    ) {
        if (painting != null) {
            // Full screen image
            AsyncImage(
                model = painting.imageUrl,
                contentDescription = "Painting",
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                contentScale = ContentScale.Fit
            )

            // Top Bar with Back Button (Overlay)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp)
                    .align(Alignment.TopCenter),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(navController = navController)
            }

            // Bottom Bar with Edit and Magic Buttons (Overlay)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        val encodedUrl = URLEncoder.encode(painting.imageUrl, StandardCharsets.UTF_8.toString())
                        navController.navigate("${NavGraph.MagicPaintbrush.route}?imageUrl=$encodedUrl&photoId=${painting.id}")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Painting")
                }

                val context = LocalContext.current
                // Use the new MagicUpgradeViewModel
                val magicUpgradeViewModel: com.example.androidapplication.models.MagicUpgradeViewModel = viewModel()
                val isMagicUpgrading by magicUpgradeViewModel.isMagicUpgrading.observeAsState(initial = false)

                Button(
                    onClick = {
                        val loader = coil.ImageLoader(context)
                        val req = coil.request.ImageRequest.Builder(context)
                            .data(painting.imageUrl)
                            .target { result ->
                                val bitmap = (result as android.graphics.drawable.BitmapDrawable).bitmap
                                magicUpgradeViewModel.magicUpgrade(painting.id, bitmap, context, painting.userId ?: "") { newBitmap ->
                                    // On success, use MagicUpgradeViewModel to update the photo
                                    magicUpgradeViewModel.updatePhoto(painting.id, newBitmap, "Magic Upgrade", "Enhanced by AI", painting.userId ?: "")
                                }
                            }
                            .build()
                        loader.enqueue(req)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE).copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(50),
                    enabled = !isMagicUpgrading
                ) {
                    if (isMagicUpgrading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Magic")
                    }
                }
            }
        } else {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        }
    }
}
