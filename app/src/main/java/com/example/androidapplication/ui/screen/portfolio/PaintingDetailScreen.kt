package com.example.androidapplication.ui.screen.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
<<<<<<< HEAD
import androidx.compose.material.icons.filled.Save
=======
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
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
<<<<<<< HEAD
import coil.ImageLoader
import coil.request.ImageRequest
=======
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
import com.example.androidapplication.models.MagicUpgradeViewModel
import com.example.androidapplication.models.PhotoViewModel
import com.example.androidapplication.ui.components.BackButton
import com.example.androidapplication.ui.container.NavGraph
<<<<<<< HEAD
import android.widget.Toast
=======
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun PaintingDetailScreen(
    navController: NavController,
    paintingId: String,
    photoViewModel: PhotoViewModel = viewModel(key = "shared_photo_viewmodel")
) {
<<<<<<< HEAD
    val context = LocalContext.current
    val imageLoader = remember { ImageLoader(context) }
    
    val portfolioPhotos by photoViewModel.portfolioPhotos.observeAsState(initial = emptyList())
    val isConvertingSketch by photoViewModel.isConvertingSketch.observeAsState(initial = false)
    val convertedImageUrl by photoViewModel.convertedImageUrl.observeAsState(initial = null)
    val sketchImageUrl by photoViewModel.sketchImageUrl.observeAsState(initial = null)
    
    var showConvertedImage by remember { mutableStateOf(false) }
    
    val painting = remember(portfolioPhotos, paintingId) {
        portfolioPhotos.find { it.id == paintingId }
    }
    
    // Afficher l'image convertie si disponible
    LaunchedEffect(convertedImageUrl, sketchImageUrl) {
        if (convertedImageUrl != null || sketchImageUrl != null) {
            showConvertedImage = true
        }
=======
    val myPhotos by photoViewModel.myPhotos.observeAsState(initial = emptyList())
    
    val painting = remember(myPhotos, paintingId) {
        myPhotos.find { it.id == paintingId }
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Dark background for immersive view
    ) {
        if (painting != null) {
<<<<<<< HEAD
            // Afficher l'image convertie si disponible, sinon l'image originale
            val imageToShow = if (showConvertedImage && (convertedImageUrl != null || sketchImageUrl != null)) {
                convertedImageUrl ?: sketchImageUrl
            } else {
                painting.imageUrl
            }
            
            // Full screen image
            AsyncImage(
                model = imageToShow,
=======
            // Full screen image
            AsyncImage(
                model = painting.imageUrl,
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
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

<<<<<<< HEAD
            // Bottom Bar with Edit, Magic, Black & White, and Save Buttons (Overlay)
=======
            // Bottom Bar with Edit and Magic Buttons (Overlay)
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
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

<<<<<<< HEAD
=======
                val context = LocalContext.current
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
                // Use the new MagicUpgradeViewModel
                val magicUpgradeViewModel: com.example.androidapplication.models.MagicUpgradeViewModel = viewModel()
                val isMagicUpgrading by magicUpgradeViewModel.isMagicUpgrading.observeAsState(initial = false)

                Button(
                    onClick = {
<<<<<<< HEAD
                        val req = ImageRequest.Builder(context)
=======
                        val loader = coil.ImageLoader(context)
                        val req = coil.request.ImageRequest.Builder(context)
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
                            .data(painting.imageUrl)
                            .target { result ->
                                val bitmap = (result as android.graphics.drawable.BitmapDrawable).bitmap
                                magicUpgradeViewModel.magicUpgrade(painting.id, bitmap, context, painting.userId ?: "") { newBitmap ->
                                    // On success, use MagicUpgradeViewModel to update the photo
                                    magicUpgradeViewModel.updatePhoto(painting.id, newBitmap, "Magic Upgrade", "Enhanced by AI", painting.userId ?: "")
                                }
                            }
                            .build()
<<<<<<< HEAD
                        imageLoader.enqueue(req)
=======
                        loader.enqueue(req)
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
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
<<<<<<< HEAD
                
                // Noir et blanc Button
                Button(
                    onClick = {
                        if (showConvertedImage && (convertedImageUrl != null || sketchImageUrl != null)) {
                            // Si l'image convertie est déjà affichée, revenir à l'originale
                            showConvertedImage = false
                            photoViewModel.clearSketchImage()
                        } else {
                            // Convertir en noir et blanc
                            photoViewModel.convertToBlackAndWhite(painting)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showConvertedImage) Color(0xFF4A4A4A) else Color(0xFF2C2C2C).copy(alpha = 0.8f)
                    ),
                    shape = RoundedCornerShape(50),
                    enabled = !isConvertingSketch
                ) {
                    if (isConvertingSketch) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (showConvertedImage) "Original" else "Noir et blanc")
                    }
                }
                
                // Save to Portfolio Button
                Button(
                    onClick = {
                        val imageToSave = if (showConvertedImage && (convertedImageUrl != null || sketchImageUrl != null)) {
                            convertedImageUrl ?: sketchImageUrl
                        } else {
                            painting.imageUrl
                        }

                        if (imageToSave != null) {
                            // Launch a coroutine to load the bitmap and save
                            val request = ImageRequest.Builder(context)
                                .data(imageToSave)
                                .allowHardware(false) // Important for Bitmap manipulation
                                .target { result ->
                                    val bitmap = (result as android.graphics.drawable.BitmapDrawable).bitmap
                                    photoViewModel.saveToPortfolio(
                                        bitmap = bitmap,
                                        title = painting.title ?: "Portfolio Item",
                                        description = if (showConvertedImage) "Saved from Painting Detail - Noir et blanc" else "Saved from Painting Detail"
                                    )
                                    Toast.makeText(context, "Saved to Portfolio", Toast.LENGTH_SHORT).show()
                                }
                                .build()
                            imageLoader.enqueue(request)
                        } else {
                            Toast.makeText(context, "No image to save", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save")
                }
=======
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
            }
        } else {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        }
    }
}
