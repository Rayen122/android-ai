package com.example.androidapplication.ui.screen.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Upload
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.androidapplication.models.PhotoViewModel
import com.example.androidapplication.models.ProfileViewModel
import com.example.androidapplication.models.UserDataResponse
import com.example.androidapplication.utils.getTokenFromPreferences

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = viewModel(),
    photoViewModel: PhotoViewModel = viewModel(),
    onEditClicked: () -> Unit = {},
    onPhotoUploadClick: (Bitmap) -> Unit = {}
) {
    val context = LocalContext.current
    val token = getTokenFromPreferences(context)

    // Profile data
    val userData by profileViewModel.userData.observeAsState()
    val profileError by profileViewModel.error.observeAsState()

    // Photo data
    val myPhotos by photoViewModel.myPhotos.observeAsState(initial = emptyList())
    val isLoadingPhotos by photoViewModel.isLoading.observeAsState(initial = false)
    val uploadSuccess by photoViewModel.uploadSuccess.observeAsState(initial = false)

    // Image picker state
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showUploadScreen by remember { mutableStateOf(false) }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                selectedBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                showUploadScreen = true
            } catch (e: Exception) {
                Log.e("ProfileScreen", "Error loading image", e)
            }
        }
    }

    // Load profile and photos on first appearance
    LaunchedEffect(Unit) {
    if (!token.isNullOrEmpty()) {
            profileViewModel.fetchUserData()
        }
    }

    // Load photos when user data is available
    LaunchedEffect(userData?.id) {
        userData?.id?.let { userId ->
            photoViewModel.getMyPhotos(userId)
        }
    }

    // Handle upload success message
    LaunchedEffect(uploadSuccess) {
        if (uploadSuccess) {
            showUploadScreen = false
            selectedBitmap = null
            selectedImageUri = null
            kotlinx.coroutines.delay(2000)
            photoViewModel.clearUploadSuccess()
            userData?.id?.let { userId ->
                photoViewModel.getMyPhotos(userId)
            }
        }
    }

    // Gradient background matching iOS
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFE6B3), // #FFE6B3 - light orange/yellow
                        Color(0xFFFCD48A), // #FCD48A - medium orange/yellow
                        Color(0xFFF2C14F)  // #F2C14F - darker orange/yellow
                )
            )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Profile Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacing(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Picture
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f))
                ) {
                    // You can add profile image here if available
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        tint = Color.White.copy(alpha = 0.9f)
                    )
                }

                // Stats
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacing(10.dp)
                ) {
                    Text(
                        text = userData?.name ?: "User",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacing(24.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${myPhotos.size}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Publications",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "0",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Abonnés",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "0",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                Text(
                                text = "Abonnements",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Photo Upload Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacing(24.dp)
            ) {
                // Selected Image Preview
                selectedBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Selected photo",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                // Upload Button
            Button(
                onClick = {
                        if (selectedBitmap != null) {
                            showUploadScreen = true
                        } else {
                            imagePickerLauncher.launch("image/*")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .padding(horizontal = 50.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFF2C14F), // #F2C14F
                                        Color(0xFFFCD48A)  // #FCD48A
                                    )
                                ),
                                shape = RoundedCornerShape(28.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacing(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (selectedBitmap == null)
                                    Icons.Default.Add
                                else
                                    Icons.Default.Upload,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Text(
                                text = if (selectedBitmap == null) "Ajouter une photo" else "Publier la photo",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Success Message
            if (uploadSuccess) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Green.copy(alpha = 0.6f)
                    )
                ) {
                    Text(
                        text = "Photo ajoutée avec succès !",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // My Photos Grid
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Mes publications",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (isLoadingPhotos) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        color = Color.White
                    )
                } else if (myPhotos.isEmpty()) {
                    Text(
                        text = "Aucune publication",
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacing(4.dp),
                        verticalArrangement = Arrangement.spacing(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(myPhotos) { photo ->
                            AsyncImage(
                                model = photo.imageUrl,
                                contentDescription = photo.title ?: "Photo",
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop
                            )
        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Photo Upload Screen
    if (showUploadScreen && selectedBitmap != null) {
        PhotoUploadScreen(
            bitmap = selectedBitmap!!,
            onDismiss = {
                showUploadScreen = false
                selectedBitmap = null
                selectedImageUri = null
            },
            onUploadComplete = { title, description ->
                photoViewModel.uploadPhoto(selectedBitmap!!, title, description)
            }
        )
    }
}
