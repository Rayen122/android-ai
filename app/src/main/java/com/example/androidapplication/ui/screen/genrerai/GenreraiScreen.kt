package com.example.androidapplication.ui.screen.genrerai
import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.CachePolicy
import coil.size.Size
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import com.example.androidapplication.models.GenreraiViewModel
import com.example.androidapplication.models.GeneratedImage
import com.example.androidapplication.ui.components.BottomNavigationBar
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import com.example.androidapplication.ui.theme.PrimaryYellowLight
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreraiScreen(navController: NavController) {
    val viewModel: GenreraiViewModel = viewModel()
    
    val generatedImages by viewModel.generatedImages.observeAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val error by viewModel.error.observeAsState(initial = "")
    
    var promptText by remember { mutableStateOf("") }

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
                            Color(0xFF16213E)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Title
                Text(
                    text = "Images de Référence",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Subtitle
                Text(
                    text = "Trouvez des images de référence pour vous inspirer",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Prompt input section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1A2E)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Décrivez ce que vous cherchez",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        OutlinedTextField(
                            value = promptText,
                            onValueChange = { promptText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            placeholder = { Text("Ex: Un chat mignon dans un jardin fleuri") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = PrimaryYellowLight,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                                unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f)
                            ),
                            maxLines = 4,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                android.util.Log.d("GenreraiScreen", "Rechercher button clicked with prompt: ${promptText.take(50)}")
                                try {
                                    if (promptText.isNotBlank()) {
                                        android.util.Log.d("GenreraiScreen", "Calling viewModel.generateImage")
                                        viewModel.generateImage(promptText)
                                        android.util.Log.d("GenreraiScreen", "viewModel.generateImage called successfully")
                                    } else {
                                        android.util.Log.w("GenreraiScreen", "Prompt is blank")
                                    }
                                } catch (e: Exception) {
                                    android.util.Log.e("GenreraiScreen", "Error on button click", e)
                                    e.printStackTrace()
                                } catch (e: Throwable) {
                                    android.util.Log.e("GenreraiScreen", "Critical error on button click: ${e.javaClass.simpleName}", e)
                                    e.printStackTrace()
                                }
                            },
                            enabled = !isLoading && promptText.isNotBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryYellowDark,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Génération en cours...")
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Rechercher")
                            }
                        }
                    }
                }
                // Error message
                if (error.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    LaunchedEffect(error) {
                        kotlinx.coroutines.delay(5000)
                        viewModel.clearError()
                    }
                }

                // Reference images grid with stages
                if (generatedImages.isNotEmpty()) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Processus de création étape par étape",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        
                        Text(
                            text = "${generatedImages.size} images générées pour illustrer chaque étape de création",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        // Indicateur de progression
                        val progressValue = remember(generatedImages.size) {
                            (generatedImages.size / 6f).coerceIn(0f, 1f)
                        }
                        LinearProgressIndicator(
                            progress = progressValue,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = PrimaryYellowLight,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }

                // Images grid
                if (generatedImages.isEmpty() && !isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.White.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Aucune image de référence",
                                color = Color.White.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "Entrez une description et cliquez sur Rechercher",
                                color = Color.White.copy(alpha = 0.3f),
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(
                            items = generatedImages,
                            key = { image -> image.imageUrl ?: image.base64Image ?: image.prompt }
                        ) { image ->
                            val stageInfo = getStageInfo(generatedImages.indexOf(image))
                            ImageCardWithStage(
                                image = image, 
                                stageLabel = stageInfo.label,
                                stageDescription = stageInfo.description
                            )
                        }
                    }
                }
            }
        }
    }
}

// ImageLoader personnalisé avec des timeouts plus longs pour Pollinations
@Composable
private fun rememberImageLoader(context: android.content.Context): ImageLoader {
    return remember(context) {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS) // 2 minutes pour la génération d'images
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
        
        ImageLoader.Builder(context)
            .okHttpClient(okHttpClient)
            .build()
    }
}

// Fonction pour obtenir les informations détaillées de chaque étape
private fun getStageInfo(index: Int): StageInfo {
    val stages = listOf(
        StageInfo(
            label = "Étape 1: Croquis initial",
            description = "Premier croquis au crayon, esquisse de base avec les lignes principales et la composition générale"
        ),
        StageInfo(
            label = "Étape 2: Application des couleurs",
            description = "Ajout des couleurs de base, colorisation plate pour établir la palette et les zones principales"
        ),
        StageInfo(
            label = "Étape 3: Ajout des détails",
            description = "Raffinement avec ajout de détails, textures et éléments spécifiques pour enrichir l'œuvre"
        ),
        StageInfo(
            label = "Étape 4: Finitions finales",
            description = "Travail final sur les ombres, lumières et détails pour polir et finaliser l'œuvre complète"
        ),
        StageInfo(
            label = "Référence artistique",
            description = "Exemple de style artistique et d'inspiration pour guider votre propre création"
        ),
        StageInfo(
            label = "Technique artistique",
            description = "Illustration des méthodes et techniques utilisées dans le processus de création"
        )
    )
    return if (index < stages.size) stages[index] else StageInfo(
        label = "Étape ${index + 1}",
        description = "Image de référence supplémentaire"
    )
}

data class StageInfo(
    val label: String,
    val description: String
)

@Composable
fun ImageCardWithStage(image: GeneratedImage, stageLabel: String, stageDescription: String) {
    val context = LocalContext.current
    val imageLoader = rememberImageLoader(context)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A2E)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                image.base64Image != null -> {
                    // Decode base64 image
                    val bitmap = remember(image.base64Image) {
                        try {
                            val imageBytes = Base64.decode(image.base64Image, Base64.DEFAULT)
                            android.graphics.BitmapFactory.decodeByteArray(
                                imageBytes,
                                0,
                                imageBytes.size
                            )
                        } catch (e: Exception) {
                            android.util.Log.e("ImageCard", "Error decoding base64 image", e)
                            null
                        }
                    }
                    
                    bitmap?.let {
                        androidx.compose.foundation.Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = image.prompt,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } ?: Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                image.imageUrl != null && image.imageUrl.isNotBlank() -> {
                    // Utiliser remember pour éviter les recréations et valider l'URL
                    val imageUrl = remember(image.imageUrl) { 
                        val url = image.imageUrl?.trim()
                        if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                            android.util.Log.d("ImageCard", "Loading image from URL: ${url.take(100)}...")
                            url
                        } else {
                            android.util.Log.e("ImageCard", "Invalid URL format: ${image.imageUrl}")
                            null
                        }
                    }
                    
                    if (imageUrl != null) {
                        // Utiliser ImageRequest pour une meilleure gestion d'erreur
                        val imageRequest = remember(imageUrl) {
                            ImageRequest.Builder(context)
                                .data(imageUrl)
                                .size(Size.ORIGINAL)
                                .crossfade(true)
                                .allowHardware(false) // Permet de charger des images plus grandes
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .networkCachePolicy(CachePolicy.ENABLED)
                                .setHeader("User-Agent", "Mozilla/5.0 (Android) Coil/2.5.0")
                                .setHeader("Accept", "image/*")
                                .listener(
                                    onStart = { 
                                        android.util.Log.d("ImageCard", "Image loading started: ${imageUrl.take(100)}...")
                                    },
                                    onSuccess = { _, _ -> 
                                        android.util.Log.d("ImageCard", "Image loaded successfully: ${imageUrl.take(100)}...")
                                    },
                                    onError = { _, result -> 
                                        android.util.Log.e("ImageCard", "Image loading failed: ${imageUrl.take(100)}...")
                                        val throwable = result.throwable
                                        if (throwable != null) {
                                            android.util.Log.e("ImageCard", "Error message: ${throwable.message}")
                                            android.util.Log.e("ImageCard", "Error cause: ${throwable.cause}")
                                        }
                                        android.util.Log.e("ImageCard", "Full URL: $imageUrl")
                                    }
                                )
                                .build()
                        }
                        
                        AsyncImage(
                            model = imageRequest,
                            imageLoader = imageLoader,
                            contentDescription = image.prompt,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                            error = painterResource(android.R.drawable.ic_menu_report_image)
                        )
                    } else {
                        // Afficher un placeholder si l'URL est invalide
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                    tint = Color.White.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "URL invalide",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            
            // Stage label and description overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.0f),
                                Color.Black.copy(alpha = 0.7f),
                                Color.Black.copy(alpha = 0.9f)
                            )
                        )
                    )
            ) {
                Text(
                    text = stageLabel,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
                Text(
                    text = stageDescription,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 0.dp, bottom = 8.dp)
                )
            }
        }
    }
}


