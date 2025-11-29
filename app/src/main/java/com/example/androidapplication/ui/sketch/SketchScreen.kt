package com.example.androidapplication.ui.sketch

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.graphics.asAndroidPath
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.applyCanvas
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.androidapplication.models.sketch.SketchUiState
import com.example.androidapplication.models.sketch.SketchViewModel
import com.example.androidapplication.models.ProfileViewModel
import com.example.androidapplication.models.login.getAccessToken
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import com.example.androidapplication.ui.theme.PrimaryYellowLight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SketchScreen(
    navController: NavController,
    viewModel: SketchViewModel = viewModel(),
    photoViewModel: com.example.androidapplication.models.PhotoViewModel = viewModel(key = "shared_photo_viewmodel"),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val token = getAccessToken(context)
    var showClearDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var saveTitle by remember { mutableStateOf("") }
    var saveDescription by remember { mutableStateOf("") }
    
    val userData by profileViewModel.userData.observeAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onPhotoSelected(it) }
    }

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

    // Clear confirmation dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Canvas") },
            text = { Text("Are you sure you want to clear all drawings? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearCanvas()
                        showClearDialog = false
                    }
                ) {
                    Text("Clear", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    var includeBackground by remember { mutableStateOf(false) }

    // Save dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save to Portfolio") },
            text = {
                Column {
                    Text("Give your sketch a title and description:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = saveTitle,
                        onValueChange = { saveTitle = it },
                        label = { Text("Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = saveDescription,
                        onValueChange = { saveDescription = it },
                        label = { Text("Description (optional)") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { includeBackground = !includeBackground }
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = includeBackground,
                            onCheckedChange = { includeBackground = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = PrimaryYellowDark,
                                checkmarkColor = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Include background photo")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (saveTitle.isNotBlank()) {
                            // Capture canvas and save in coroutine
                            val state = uiState as? SketchUiState.PhotoSelected
                            state?.let { sketchState ->
                                Toast.makeText(context, "Sauvegarde du design en cours...", Toast.LENGTH_SHORT).show()
                                coroutineScope.launch(Dispatchers.Main) {
                                    try {
                                        val bitmap = createBitmapFromCanvas(
                                            context = context,
                                            photoUri = sketchState.photoUri,
                                            opacity = sketchState.opacity,
                                            paths = sketchState.paths,
                                            width = 1080,
                                            height = 1920,
                                            includeBackground = includeBackground
                                        )
                                        
                                        // Save to portfolio
                                        photoViewModel.saveToPortfolio(
                                            bitmap = bitmap,
                                            title = saveTitle,
                                            description = saveDescription
                                        )
                                        showSaveDialog = false
                                        // Navigation will be handled by observing uploadSuccess
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error saving: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = saveTitle.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showSaveDialog = false
                    saveTitle = ""
                    saveDescription = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Load user data if needed
    LaunchedEffect(Unit) {
        if (!token.isNullOrEmpty() && userData == null) {
            profileViewModel.fetchUserData()
        }
    }
    
    // Observe upload success
    val uploadSuccess by photoViewModel.uploadSuccess.observeAsState()
    val uploadError by photoViewModel.error.observeAsState()
    val isUploading by photoViewModel.isLoading.observeAsState(false)

    LaunchedEffect(uploadSuccess) {
        if (uploadSuccess == true) {
            // Détecter si c'est un design en noir (vérifier les couleurs utilisées)
            val state = uiState as? SketchUiState.PhotoSelected
            val isBlackDesign = state?.let { sketchState ->
                // Vérifier si toutes les couleurs sont noires ou grises
                sketchState.paths.all { path ->
                    val color = path.color
                    val r = (color.red * 255).toInt()
                    val g = (color.green * 255).toInt()
                    val b = (color.blue * 255).toInt()
                    // Noir ou gris foncé (seuil à 50)
                    (r < 50 && g < 50 && b < 50)
                } && sketchState.paths.isNotEmpty()
            } ?: false
            
            val message = if (isBlackDesign) {
                "Design en noir sauvegardé dans le portfolio !"
            } else {
                "Design sauvegardé dans le portfolio !"
            }
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            
            // Rafraîchir le portfolio
            val userId = userData?.id
            userId?.let {
                photoViewModel.getPortfolioPhotos(it)
            }
            
            photoViewModel.clearUploadSuccess() // Reset state
            navController.navigate(com.example.androidapplication.ui.container.NavGraph.Portfolio.route) {
                popUpTo(com.example.androidapplication.ui.container.NavGraph.Sketch.route) { inclusive = true }
            }
        }
    }

    LaunchedEffect(uploadError) {
        if (!uploadError.isNullOrEmpty()) {
            Toast.makeText(context, uploadError, Toast.LENGTH_LONG).show()
            photoViewModel.clearError()
        }
    }
    
    // Show loading overlay
    if (isUploading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = false) {}, // Block interactions
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PrimaryYellowDark)
        }
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
                    )
                )
            )
    ) {
        when (val state = uiState) {
            is SketchUiState.Initial -> {
                // Initial state - show photo picker
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "Sketch & Trace",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Select a photo to start sketching",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.LightGray,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    Button(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryYellowDark
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Select Photo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            is SketchUiState.PhotoSelected -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Top toolbar
                        TopToolbar(
                            onBackClick = { navController.popBackStack() },
                            onUndoClick = { viewModel.undo() },
                            onRedoClick = { viewModel.redo() },
                            onClearClick = { showClearDialog = true },
                            canUndo = state.canUndo,  // ✅ Utiliser l'état UI directement
                            canRedo = state.canRedo    // ✅ Utiliser l'état UI directement
                        )

                        // Drawing canvas
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            DrawingCanvas(
                                photoUri = state.photoUri,
                                opacity = state.opacity,
                                paths = state.paths,
                                currentPath = state.currentPath,
                                onDrawStart = { offset -> viewModel.startDrawing(offset) },
                                onDrawMove = { offset -> viewModel.continueDrawing(offset) },
                                onDrawEnd = { viewModel.endDrawing() }
                            )
                        }

                        /* Bottom toolbar */
                        BottomToolbar(
                            opacity = state.opacity,
                            onOpacityChange = { viewModel.updateOpacity(it) },
                            selectedColor = state.selectedColor,
                            onColorSelect = { viewModel.selectColor(it) },
                            brushSize = state.brushSize,
                            onBrushSizeChange = { viewModel.setBrushSize(it) },
                            isEraserMode = state.isEraserMode,
                            onToggleEraser = { viewModel.toggleEraser() },
                            onChangePhoto = { launcher.launch("image/*") },
                            onSaveClick = { showSaveDialog = true }  // ✅ AJOUT
                        )
                    }
                }
            }

            is SketchUiState.Saving -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PrimaryYellowDark)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Saving to portfolio...", color = Color.White)
                    }
                }
            }

            is SketchUiState.Saved -> {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, "Sketch saved to portfolio!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            }

            is SketchUiState.Error -> {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                    viewModel.reset()
                }
            }
        }
    }
}

@Composable
fun TopToolbar(
    onBackClick: () -> Unit,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onClearClick: () -> Unit,
    canUndo: Boolean,
    canRedo: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A2E).copy(alpha = 0.9f))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(
                onClick = onUndoClick,
                enabled = canUndo
            ) {
                Icon(
                    imageVector = Icons.Default.Undo,
                    contentDescription = "Undo",
                    tint = if (canUndo) Color.White else Color.Gray
                )
            }

            IconButton(
                onClick = onRedoClick,
                enabled = canRedo
            ) {
                Icon(
                    imageVector = Icons.Default.Redo,
                    contentDescription = "Redo",
                    tint = if (canRedo) Color.White else Color.Gray
                )
            }

            IconButton(onClick = onClearClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun BottomToolbar(
    opacity: Float,
    onOpacityChange: (Float) -> Unit,
    selectedColor: Color,
    onColorSelect: (Color) -> Unit,
    brushSize: Float,
    onBrushSizeChange: (Float) -> Unit,
    isEraserMode: Boolean,
    onToggleEraser: () -> Unit,
    onChangePhoto: () -> Unit,
    onSaveClick: () -> Unit  // ✅ AJOUT
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0F1E).copy(alpha = 0.95f),
                        Color(0xFF1A1A2E).copy(alpha = 0.98f)
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Opacity slider with better design
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Opacity,
                        contentDescription = "Opacity",
                        tint = PrimaryYellowLight,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Opacity",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = "${(opacity * 100).toInt()}%",
                    color = PrimaryYellowDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = opacity,
                onValueChange = onOpacityChange,
                valueRange = 0f..1f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryYellowDark,
                    activeTrackColor = PrimaryYellowDark,
                    inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Brush size selector with better design
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Brush Size:",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BrushSizeButton(
                    size = 5f,
                    isSelected = brushSize == 5f,
                    onClick = { onBrushSizeChange(5f) },
                    label = "S"
                )
                BrushSizeButton(
                    size = 10f,
                    isSelected = brushSize == 10f,
                    onClick = { onBrushSizeChange(10f) },
                    label = "M"
                )
                BrushSizeButton(
                    size = 20f,
                    isSelected = brushSize == 20f,
                    onClick = { onBrushSizeChange(20f) },
                    label = "L"
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Color palette and tools with improved layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color palette with larger circles
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val colors = listOf(
                    Color(0xFFFFA500), // Orange
                    Color.White,
                    Color.Red,
                    Color.Blue,
                    Color.Green,
                    Color.Yellow,
                    Color.Magenta,
                    Color.Cyan
                )
                colors.forEach { color ->
                    ColorButton(
                        color = color,
                        isSelected = selectedColor == color && !isEraserMode,
                        onClick = { onColorSelect(color) }
                    )
                }
            }

            // Tools with better spacing
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Eraser button
                IconButton(
                    onClick = onToggleEraser,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (isEraserMode) PrimaryYellowDark else Color(0xFF2A2A3E),
                            CircleShape
                        )
                        .border(
                            width = 2.dp,
                            color = if (isEraserMode) PrimaryYellowDark else Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.CleaningServices,
                        contentDescription = "Eraser",
                        tint = if (isEraserMode) Color.Black else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Change photo button
                IconButton(
                    onClick = onChangePhoto,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF2A2A3E), CircleShape)
                        .border(
                            width = 2.dp,
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Change Photo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save button - bien visible et accessible
        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryYellowDark
            ),
            shape = RoundedCornerShape(28.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 4.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = "Save",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sauvegarder",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun ColorButton(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 4.dp else 2.dp,
                color = if (isSelected) PrimaryYellowDark else Color.White.copy(alpha = 0.4f),
                shape = CircleShape
            )
            .clickable(onClick = onClick)
            .graphicsLayer(
                shadowElevation = if (isSelected) 8f else 2f
            )
    )
}

@Composable
fun BrushSizeButton(
    size: Float,
    isSelected: Boolean,
    onClick: () -> Unit,
    label: String
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) PrimaryYellowDark else Color(0xFF2A2A3E)
            )
            .border(
                width = 2.dp,
                color = if (isSelected) PrimaryYellowDark else Color.White.copy(alpha = 0.2f),
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
