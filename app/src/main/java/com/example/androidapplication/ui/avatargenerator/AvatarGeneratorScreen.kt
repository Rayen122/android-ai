package com.example.androidapplication.ui.avatargenerator

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage

@Composable
fun AvatarGeneratorScreen(
    viewModel: AvatarGeneratorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onImageSelected(it) }
    }

    val styles = listOf("Cyberpunk", "Anime", "3D Render", "Oil Painting", "Realistic", "LinkedIn Professional")
    var selectedStyle by remember { mutableStateOf(styles[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "AI Avatar Generator",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        when (val state = uiState) {
            is AvatarUiState.Initial -> {
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Upload Selfie")
                }
            }
            is AvatarUiState.ImageSelected -> {
                AsyncImage(
                    model = state.imageUri,
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(bottom = 16.dp)
                )
                
                Text("Choose Style:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    items(styles) { style ->
                        FilterChip(
                            selected = selectedStyle == style,
                            onClick = { selectedStyle = style },
                            label = { Text(style) }
                        )
                    }
                }

                Button(
                    onClick = { viewModel.generateAvatar(context, state.imageUri, selectedStyle) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Generate $selectedStyle Avatar")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { launcher.launch("image/*") }) {
                    Text("Pick different photo")
                }
            }
            is AvatarUiState.Success -> {
                // Custom ImageLoader with longer timeout
                val context = LocalContext.current
                val imageLoader = remember {
                    val okHttpClient = okhttp3.OkHttpClient.Builder()
                        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                        .build()

                    coil.ImageLoader.Builder(context)
                        .okHttpClient(okHttpClient)
                        .build()
                }

                Text("Your AI Avatar:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                
                var imageError by remember { mutableStateOf<String?>(null) }
                
                // Use local proxy to bypass emulator DNS issues
                val originalUrl = state.imageUrl
                val encodedOriginalUrl = java.net.URLEncoder.encode(originalUrl, "UTF-8")
                val proxyUrl = "http://10.0.2.2:3000/ai/proxy-image?url=$encodedOriginalUrl"
                
                AsyncImage(
                    model = proxyUrl,
                    imageLoader = imageLoader,
                    contentDescription = "Generated Avatar",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .background(Color.LightGray),
                    onLoading = { 
                        Log.d("AvatarGenerator", "Image loading started via proxy: $proxyUrl") 
                        imageError = null
                    },
                    onSuccess = { 
                        Log.d("AvatarGenerator", "Image loaded successfully") 
                        imageError = null
                    },
                    onError = { result -> 
                        Log.e("AvatarGenerator", "Image load failed: ${result.result.throwable.message}")
                        imageError = result.result.throwable.message ?: "Unknown error"
                        result.result.throwable.printStackTrace()
                    },
                    error = painterResource(android.R.drawable.ic_menu_report_image),
                    placeholder = painterResource(android.R.drawable.ic_menu_gallery)
                )
                
                if (imageError != null) {
                    Text(
                        text = "Image Error: $imageError",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                val decodedUrl = java.net.URLDecoder.decode(state.imageUrl, "UTF-8")
                Text(
                    text = "URL: $decodedUrl",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.reset() }) {
                    Text("Create Another")
                }
            }
            is AvatarUiState.Loading -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.reset() }) {
                    Text("Create Another")
                }
            }
            is AvatarUiState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.reset() }) {
                    Text("Try Again")
                }
            }
        }
    }
}
