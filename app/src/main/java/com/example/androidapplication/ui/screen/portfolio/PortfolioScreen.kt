package com.example.androidapplication.ui.screen.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.androidapplication.models.MagicUpgradeViewModel
import com.example.androidapplication.models.PhotoViewModel
import com.example.androidapplication.models.ProfileViewModel
import com.example.androidapplication.models.login.getAccessToken
import com.example.androidapplication.ui.components.BackButton
import com.example.androidapplication.ui.container.NavGraph

@Composable
fun PortfolioScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel<ProfileViewModel>(),
    photoViewModel: PhotoViewModel = viewModel<PhotoViewModel>(key = "shared_photo_viewmodel")
) {
    val context = LocalContext.current
    val token = getAccessToken(context)

    val userData by profileViewModel.userData.observeAsState()
    val myPhotos by photoViewModel.myPhotos.observeAsState(initial = emptyList())
    val isLoading by photoViewModel.isLoading.observeAsState(initial = false)

    val magicUpgradeViewModel: MagicUpgradeViewModel = viewModel()

    // Filter for drawings only (created with Magic Paintbrush)
    val myDrawings = remember(myPhotos) {
        myPhotos.filter { photo ->
            photo.description?.contains("Created with Magic Paintbrush") == true
        }
    }

    // Load data
    LaunchedEffect(Unit) {
        if (!token.isNullOrEmpty()) {
            profileViewModel.fetchUserData()
        }
    }

    LaunchedEffect(userData?.id) {
        userData?.id?.let { userId ->
            photoViewModel.getMyPhotos(userId)
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(navController = navController)

                Text(
                    text = "My Portfolio",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                // Empty box to balance the back button for centering title
                Spacer(modifier = Modifier.size(44.dp))
            }
        },
        containerColor = Color(0xFFF5F5F5) // Light background color
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Black
                )
            } else if (myDrawings.isEmpty()) {
                Text(
                    text = "No drawings yet",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items = myDrawings, key = { it.id }) { photo ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .clickable {
                                    navController.navigate("${NavGraph.PaintingDetail.route}/${photo.id}")
                                }
                        ) {
                            AsyncImage(
                                model = photo.imageUrl,
                                contentDescription = "Drawing",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Delete button (X icon)
                            IconButton(
                                onClick = {
                                    userData?.id?.let { userId ->
                                        magicUpgradeViewModel.deletePhoto(photo.id, userId)
                                        // Note: This won't automatically refresh the list in PhotoViewModel unless we trigger it.
                                        // Ideally, we should have a shared event or callback.
                                        // For now, we can manually trigger a refresh on photoViewModel if needed, 
                                        // but since we are moving logic away, maybe the user intends to handle refresh differently.
                                        // However, to keep UI in sync:
                                        photoViewModel.getMyPhotos(userId)
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
