package com.example.androidapplication.ui.screen.magicpaintbrush

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.View
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.androidapplication.models.PhotoViewModel
import com.example.androidapplication.ui.components.BackButton
import com.example.androidapplication.ui.container.NavGraph
import kotlinx.coroutines.launch
import java.util.UUID

import androidx.compose.ui.layout.ContentScale
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.draw.alpha
import com.example.androidapplication.models.MagicUpgradeViewModel

data class DrawingPath(
    val path: Path,
    val color: Color,
    val strokeWidth: Float,
    val alpha: Float = 1f
)

enum class DrawingTool {
    PENCIL, MARKER, PEN, ERASER, RULER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MagicPaintbrushScreen(
    navController: NavController,
    photoViewModel: PhotoViewModel = viewModel(),
    initialImageUrl: String? = null,
    photoId: String? = null
) {
    var paths by remember { mutableStateOf(listOf<DrawingPath>()) }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var currentColor by remember { mutableStateOf(Color.Black) }
    var currentStrokeWidth by remember { mutableStateOf(5f) }
    var currentTool by remember { mutableStateOf(DrawingTool.PENCIL) }
    var showColorPicker by remember { mutableStateOf(false) }
    
    // Background image state
    var backgroundBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var isLoadingImage by remember { mutableStateOf(false) }

    // Ruler specific state
    var rulerStart by remember { mutableStateOf<Offset?>(null) }
    var rulerEnd by remember { mutableStateOf<Offset?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val view = LocalView.current
    
    // Get user ID for update
    val profileViewModel: com.example.androidapplication.models.ProfileViewModel = viewModel()
    val userData by profileViewModel.userData.observeAsState()
    val magicUpgradeViewModel: MagicUpgradeViewModel = viewModel()
    val isMagicUpgrading by magicUpgradeViewModel.isMagicUpgrading.observeAsState(false)
    val magicError by magicUpgradeViewModel.magicError.observeAsState("")

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserData()
    }

    LaunchedEffect(magicError) {
        if (magicError.isNotEmpty()) {
            Toast.makeText(context, magicError, Toast.LENGTH_LONG).show()
        }
    }

    // Load background image as bitmap for saving
    LaunchedEffect(initialImageUrl) {
        if (initialImageUrl != null) {
            isLoadingImage = true
            launch {
                try {
                    val loader = ImageLoader(context)
                    // Add error logging and use a simpler request for debugging
                    val request = ImageRequest.Builder(context)
                        .data(initialImageUrl)
                        .allowHardware(false) // Required for Canvas drawing
                        .listener(
                            onError = { _, result ->
                                println("Image loading error: ${result.throwable.message}")
                                result.throwable.printStackTrace()
                            },
                            onSuccess = { _, _ ->
                                println("Image loaded successfully")
                            }
                        )
                        .build()
                    
                    val result = loader.execute(request)
                    if (result.drawable != null && result.drawable is BitmapDrawable) {
                        backgroundBitmap = (result.drawable as BitmapDrawable).bitmap.asImageBitmap()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("Exception loading image: ${e.message}")
                } finally {
                    isLoadingImage = false
                }
            }
        }
    }

    fun createBitmapFromPaths(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)
        
        // Draw background image first if it exists
        if (backgroundBitmap != null) {
            val androidBitmap = backgroundBitmap!!.asAndroidBitmap()
            // Scale bitmap to fit canvas while maintaining aspect ratio
            val scale = Math.min(
                width.toFloat() / androidBitmap.width,
                height.toFloat() / androidBitmap.height
            )
            val x = (width - androidBitmap.width * scale) / 2
            val y = (height - androidBitmap.height * scale) / 2
            
            val matrix = android.graphics.Matrix()
            matrix.postScale(scale, scale)
            matrix.postTranslate(x, y)
            
            val paint = android.graphics.Paint().apply {
                isFilterBitmap = true
            }
            canvas.drawBitmap(androidBitmap, matrix, paint)
        }
        
        val paint = android.graphics.Paint().apply {
            isAntiAlias = true
            style = android.graphics.Paint.Style.STROKE
            strokeJoin = android.graphics.Paint.Join.ROUND
            strokeCap = android.graphics.Paint.Cap.ROUND
        }

        paths.forEach { drawingPath ->
            paint.color = drawingPath.color.toArgb()
            paint.strokeWidth = drawingPath.strokeWidth
            paint.alpha = (drawingPath.alpha * 255).toInt()
            canvas.drawPath(drawingPath.path.asAndroidPath(), paint)
        }
        
        return bitmap
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 16.dp), // Added top padding
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButton(navController = navController)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Studio",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Palette (Color Picker)
                    IconButton(
                        onClick = { showColorPicker = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFF3E5F5), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Colors",
                            tint = Color(0xFF9C27B0)
                        )
                    }

                    // Trash (Clear)
                    IconButton(
                        onClick = { paths = emptyList() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFFFEBEE), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear",
                            tint = Color(0xFFF44336)
                        )
                    }

                    // Magic (Generate)
                    IconButton(
                        onClick = {
                            println("MagicPaintbrushScreen: Magic button clicked")
                            if (isMagicUpgrading) {
                                println("MagicPaintbrushScreen: Already upgrading, ignoring click")
                                return@IconButton
                            }
                            
                            if (userData?.id == null) {
                                println("MagicPaintbrushScreen: User ID is null, cannot upgrade")
                                Toast.makeText(context, "Veuillez vous connecter pour utiliser la magie ✨", Toast.LENGTH_SHORT).show()
                                return@IconButton
                            }

                            val bitmap = createBitmapFromPaths(view.width, view.height)
                            println("MagicPaintbrushScreen: Bitmap created, calling viewModel.magicUpgrade")
                            
                            magicUpgradeViewModel.magicUpgrade(
                                photoId ?: "",
                                bitmap,
                                context,
                                userData!!.id
                            ) { newBitmap ->
                                println("MagicPaintbrushScreen: Success callback triggered")
                                backgroundBitmap = newBitmap.asImageBitmap()
                                paths = emptyList()
                                println("MagicPaintbrushScreen: Background updated and paths cleared")
                                Toast.makeText(context, "Magic applied!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !isMagicUpgrading,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFE1F5FE), CircleShape)
                    ) {
                        if (isMagicUpgrading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoFixHigh,
                                contentDescription = "Magic",
                                tint = Color(0xFF0288D1)
                            )
                        }
                    }

                    // Save (Upload)
                    IconButton(
                        onClick = {
                            val bitmap = createBitmapFromPaths(view.width, view.height) // Approximate
                            if (photoId != null && userData?.id != null) {
                                // Update existing
                                magicUpgradeViewModel.updatePhoto(
                                    photoId, 
                                    bitmap, 
                                    "Updated Drawing", 
                                    "Created with Magic Paintbrush",
                                    userData!!.id
                                )
                            } else {
                                // Create new
                                photoViewModel.uploadPhoto(
                                    bitmap, 
                                    "My Drawing ${UUID.randomUUID().toString().take(5)}", 
                                    "Created with Magic Paintbrush",
                                    isPortfolio = true
                                )
                            }
                             // Navigate back or show success
                             // navController.popBackStack() 
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFE3F2FD), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SaveAlt,
                            contentDescription = "Save",
                            tint = Color(0xFF2196F3)
                        )
                    }

                    // Portfolio (Folder)
                    IconButton(
                        onClick = { navController.navigate(NavGraph.Portfolio.route) },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFFFF3E0), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = "Portfolio",
                            tint = Color(0xFFFF9800)
                        )
                    }
                }
            }
        },
        bottomBar = {
             Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ToolButton(
                    icon = Icons.Default.Edit, // Pencil
                    label = "Pencil",
                    isSelected = currentTool == DrawingTool.PENCIL,
                    onClick = { 
                        currentTool = DrawingTool.PENCIL 
                        currentStrokeWidth = 3f
                        currentColor = currentColor.copy(alpha = 1f)
                    }
                )
                ToolButton(
                    icon = Icons.Default.Brush, // Marker
                    label = "Marker",
                    isSelected = currentTool == DrawingTool.MARKER,
                    onClick = { 
                        currentTool = DrawingTool.MARKER 
                        currentStrokeWidth = 15f
                        currentColor = currentColor.copy(alpha = 0.7f)
                    }
                )
                ToolButton(
                    icon = Icons.Default.Create, // Pen
                    label = "Pen",
                    isSelected = currentTool == DrawingTool.PEN,
                    onClick = { 
                        currentTool = DrawingTool.PEN 
                        currentStrokeWidth = 5f
                        currentColor = currentColor.copy(alpha = 1f)
                    }
                )
                 ToolButton(
                    icon = Icons.Default.Straighten, // Ruler
                    label = "Ruler",
                    isSelected = currentTool == DrawingTool.RULER,
                    onClick = { 
                        currentTool = DrawingTool.RULER 
                        currentStrokeWidth = 5f
                         currentColor = currentColor.copy(alpha = 1f)
                    }
                )
                ToolButton(
                    icon = Icons.Default.AutoFixNormal, // Eraser (using icon as proxy)
                    label = "Eraser",
                    isSelected = currentTool == DrawingTool.ERASER,
                    onClick = { 
                        currentTool = DrawingTool.ERASER 
                        currentStrokeWidth = 20f
                    }
                )
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            if (currentTool == DrawingTool.RULER) {
                                rulerStart = offset
                                rulerEnd = offset
                            } else {
                                currentPath = Path().apply {
                                    moveTo(offset.x, offset.y)
                                }
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                             if (currentTool == DrawingTool.RULER) {
                                rulerEnd = change.position
                            } else {
                                currentPath?.lineTo(change.position.x, change.position.y)
                            }
                        },
                        onDragEnd = {
                            if (currentTool == DrawingTool.RULER) {
                                rulerStart?.let { start ->
                                    rulerEnd?.let { end ->
                                        val p = Path().apply {
                                            moveTo(start.x, start.y)
                                            lineTo(end.x, end.y)
                                        }
                                         paths = paths + DrawingPath(
                                            path = p,
                                            color = currentColor,
                                            strokeWidth = currentStrokeWidth,
                                            alpha = if (currentTool == DrawingTool.MARKER) 0.5f else 1f
                                        )
                                    }
                                }
                                rulerStart = null
                                rulerEnd = null
                            } else {
                                currentPath?.let {
                                    paths = paths + DrawingPath(
                                        path = it,
                                        color = if (currentTool == DrawingTool.ERASER) Color.White else currentColor,
                                        strokeWidth = currentStrokeWidth,
                                        alpha = if (currentTool == DrawingTool.MARKER) 0.5f else 1f
                                    )
                                }
                                currentPath = null
                            }
                        }
                    )
                }
        ) {
            // Display background image (either loaded from initial or generated)
            if (backgroundBitmap != null) {
                androidx.compose.foundation.Image(
                    bitmap = backgroundBitmap!!,
                    contentDescription = "Background Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (paths.isNotEmpty()) 0.8f else 1f),
                    contentScale = ContentScale.Fit
                )
            } else if (initialImageUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(initialImageUrl)
                        .crossfade(true)
                        .listener(
                            onError = { _, result ->
                                println("AsyncImage error: ${result.throwable.message}")
                            }
                        )
                        .build(),
                    contentDescription = "Background Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (paths.isNotEmpty()) 0.8f else 1f),
                    contentScale = ContentScale.Fit
                )
            }

            if (isLoadingImage) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                paths.forEach { drawingPath ->
                    drawPath(
                        path = drawingPath.path,
                        color = drawingPath.color,
                        alpha = drawingPath.alpha,
                        style = Stroke(
                            width = drawingPath.strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
                
                // Draw current path being dragged
                currentPath?.let {
                    drawPath(
                        path = it,
                        color = if (currentTool == DrawingTool.ERASER) Color.White else currentColor,
                        alpha = if (currentTool == DrawingTool.MARKER) 0.5f else 1f,
                        style = Stroke(
                            width = currentStrokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                // Draw ruler line being dragged
                if (currentTool == DrawingTool.RULER && rulerStart != null && rulerEnd != null) {
                     drawLine(
                        color = currentColor,
                        start = rulerStart!!,
                        end = rulerEnd!!,
                        strokeWidth = currentStrokeWidth,
                        cap = StrokeCap.Round,
                        alpha = if (currentTool == DrawingTool.MARKER) 0.5f else 1f
                    )
                }
            }
        }
    }

    if (showColorPicker) {
        SimpleColorPicker(
            onDismiss = { showColorPicker = false },
            onColorSelected = { color ->
                currentColor = color
                showColorPicker = false
            }
        )
    }
}

@Composable
fun ToolButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFFFDA858) else Color(0xFFF5F5F5))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.White else Color.Gray,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isSelected) Color(0xFFFDA858) else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun SimpleColorPicker(onDismiss: () -> Unit, onColorSelected: (Color) -> Unit) {
    val colors = listOf(
        Color.Black, Color.Red, Color.Blue, Color.Green, Color.Yellow, 
        Color.Magenta, Color.Cyan, Color.Gray, Color(0xFFFFA500), Color(0xFF800080)
    )
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Select Color", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Simplified grid or row
                     colors.take(5).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { onColorSelected(color) }
                                .border(1.dp, Color.Gray, CircleShape)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                 Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                     colors.drop(5).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { onColorSelected(color) }
                                .border(1.dp, Color.Gray, CircleShape)
                        )
                    }
                }
            }
        }
    }
}
