package com.example.androidapplication.ui.artiste

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.androidapplication.ui.components.BottomNavigationBar
import com.example.androidapplication.ui.theme.PrimaryYellow
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import com.example.androidapplication.remote.RetrofitClient
import retrofit2.http.Body
import retrofit2.http.POST
import com.google.gson.annotations.SerializedName

// Define the API interface locally or use existing if appropriate. 
// For speed, I'll define a data class and interface here or assume RetrofitClient usage.
// Since I can't easily modify RetrofitClient without seeing it, I'll use a direct call approach or assume a ViewModel handles it.
// I'll create a local ViewModel for this screen to handle the API call.

import com.example.androidapplication.remote.PaintingProcessRequest
import com.example.androidapplication.remote.PaintingProcessStep
import com.example.androidapplication.remote.PaintingProcessApi

@Composable
fun PaintingProcessScreen(navController: NavController) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedStyle by remember { mutableStateOf<String?>(null) }
    var generatedSteps by remember { mutableStateOf<List<PaintingProcessStep>?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var currentStepIndex by remember { mutableStateOf(0) }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val styles = listOf("Van Gogh", "Monet", "Da Vinci", "Picasso")

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    fun generateProcess() {
        if (selectedImageUri == null || selectedStyle == null) return
        
        isLoading = true
        scope.launch {
            try {
                // Convert Uri to Base64
                val inputStream = context.contentResolver.openInputStream(selectedImageUri!!)
                val bytes = inputStream?.readBytes()
                val base64 = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                val dataUrl = "data:image/jpeg;base64,$base64" // Assuming jpeg
                
                // Call API
                val api = RetrofitClient.paintingProcessInstance
                val result = api.generatePaintingProcess(PaintingProcessRequest(dataUrl, selectedStyle!!))
                
                generatedSteps = result
                isLoading = false
            } catch (e: Exception) {
                e.printStackTrace()
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F1E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "Painting Process",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // Image Selection
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A1A2E))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Add Photo",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap to upload photo",
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Style Selection
            Text(
                text = "Choose a Style",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(styles) { style ->
                    val isSelected = style == selectedStyle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) PrimaryYellow else Color(0xFF2A2A3E))
                            .clickable { selectedStyle = style }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = style,
                            color = if (isSelected) Color.Black else Color.White,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Generate Button
            Button(
                onClick = { generateProcess() },
                enabled = selectedImageUri != null && selectedStyle != null && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryYellow,
                    disabledContainerColor = PrimaryYellow.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate Process", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // Results
            if (generatedSteps != null) {
                Text(
                    text = "The Process",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Display current step
                val currentStep = generatedSteps!![currentStepIndex]
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black)
                ) {
                    var imageLoadError by remember(currentStep.url) { mutableStateOf<String?>(null) }
                    var isImageLoading by remember(currentStep.url) { mutableStateOf(true) }
                    var retryTrigger by remember(currentStep.url) { mutableStateOf(0) }

                    key(retryTrigger) {
                        AsyncImage(
                            model = coil.request.ImageRequest.Builder(LocalContext.current)
                                .data(currentStep.url)
                                .crossfade(true)
                                .listener(
                                    onStart = { isImageLoading = true },
                                    onSuccess = { _, _ -> isImageLoading = false },
                                    onError = { _, result -> 
                                        isImageLoading = false
                                        imageLoadError = result.throwable.message 
                                    }
                                )
                                .build(),
                            contentDescription = currentStep.step,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    if (isImageLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = PrimaryYellow
                        )
                    }
                    
                    if (imageLoadError != null) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = Color.Red,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = imageLoadError ?: "Failed to load",
                                color = Color.Red,
                                fontSize = 10.sp,
                                lineHeight = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Retry Button
                            Button(
                                onClick = { 
                                    imageLoadError = null
                                    isImageLoading = true
                                    retryTrigger++
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Retry", fontSize = 10.sp, color = Color.White)
                            }
                        }
                    }
                    
                    // Step Label
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "${currentStepIndex + 1}. ${currentStep.step}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Step Navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    generatedSteps!!.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(10.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (index == currentStepIndex) PrimaryYellow else Color.Gray)
                                .clickable { currentStepIndex = index }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
        
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavigationBar(navController = navController)
        }
    }
}
