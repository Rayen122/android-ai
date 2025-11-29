package com.example.androidapplication.models.sketch

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.OutputStream

data class DrawingPath(
    val path: Path,
    val color: Color,
    val strokeWidth: Float,
    val isEraser: Boolean = false
)

sealed class SketchUiState {
    data object Initial : SketchUiState()
    data class PhotoSelected(
        val photoUri: Uri,
        val opacity: Float = 0.5f,
        val paths: List<DrawingPath> = emptyList(),
        val currentPath: DrawingPath? = null,
        val selectedColor: Color = Color.Black,
        val brushSize: Float = 10f,
        val isEraserMode: Boolean = false,
        val canUndo: Boolean = false,
        val canRedo: Boolean = false
    ) : SketchUiState()
    data class Saving(val photoUri: Uri) : SketchUiState()
    data class Saved(val savedUri: Uri) : SketchUiState()
    data class Error(val message: String) : SketchUiState()
}

class SketchViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<SketchUiState>(SketchUiState.Initial)
    val uiState: StateFlow<SketchUiState> = _uiState.asStateFlow()

    private val undoStack = mutableListOf<List<DrawingPath>>()
    private val redoStack = mutableListOf<List<DrawingPath>>()

    fun onPhotoSelected(uri: Uri) {
        _uiState.value = SketchUiState.PhotoSelected(photoUri = uri)
        undoStack.clear()
        redoStack.clear()
    }

    fun updateOpacity(opacity: Float) {
        val currentState = _uiState.value
        if (currentState is SketchUiState.PhotoSelected) {
            _uiState.value = currentState.copy(opacity = opacity)
        }
    }

    fun selectColor(color: Color) {
        val currentState = _uiState.value
        if (currentState is SketchUiState.PhotoSelected) {
            _uiState.value = currentState.copy(
                selectedColor = color,
                isEraserMode = false
            )
        }
    }

    fun setBrushSize(size: Float) {
        val currentState = _uiState.value
        if (currentState is SketchUiState.PhotoSelected) {
            _uiState.value = currentState.copy(brushSize = size)
        }
    }

    fun toggleEraser() {
        val currentState = _uiState.value
        if (currentState is SketchUiState.PhotoSelected) {
            _uiState.value = currentState.copy(isEraserMode = !currentState.isEraserMode)
        }
    }

    fun startDrawing(offset: Offset) {
        val currentState = _uiState.value
        if (currentState is SketchUiState.PhotoSelected) {
            val newPath = Path().apply { moveTo(offset.x, offset.y) }
            val drawingPath = DrawingPath(
                path = newPath,
                color = if (currentState.isEraserMode) Color.Transparent else currentState.selectedColor,
                strokeWidth = currentState.brushSize,
                isEraser = currentState.isEraserMode
            )
            _uiState.value = currentState.copy(currentPath = drawingPath)
        }
    }

    fun continueDrawing(offset: Offset) {
        val currentState = _uiState.value
        if (currentState is SketchUiState.PhotoSelected && currentState.currentPath != null) {
            currentState.currentPath.path.lineTo(offset.x, offset.y)
            _uiState.value = currentState.copy(currentPath = currentState.currentPath)
        }
    }

    fun endDrawing() {
        val currentState = _uiState.value
        if (currentState is SketchUiState.PhotoSelected && currentState.currentPath != null) {
            // Save current state to undo stack
            undoStack.add(currentState.paths)
            redoStack.clear()
            
            val newPaths = currentState.paths + currentState.currentPath
            _uiState.value = currentState.copy(
                paths = newPaths,
                currentPath = null,
                canUndo = true,
                canRedo = false
            )
        }
    }

    fun undo() {
        val currentState = _uiState.value
        if (currentState is SketchUiState.PhotoSelected && undoStack.isNotEmpty()) {
            redoStack.add(currentState.paths)
            val previousPaths = undoStack.removeAt(undoStack.size - 1)
            _uiState.value = currentState.copy(
                paths = previousPaths,
                canUndo = undoStack.isNotEmpty(),
                canRedo = true
            )
        }
    }

    fun redo() {
        val currentState = _uiState.value
        if (currentState is SketchUiState.PhotoSelected && redoStack.isNotEmpty()) {
            undoStack.add(currentState.paths)
            val nextPaths = redoStack.removeAt(redoStack.size - 1)
            _uiState.value = currentState.copy(
                paths = nextPaths,
                canUndo = true,
                canRedo = redoStack.isNotEmpty()
            )
        }
    }

    fun clearCanvas() {
        val currentState = _uiState.value
        if (currentState is SketchUiState.PhotoSelected) {
            undoStack.add(currentState.paths)
            redoStack.clear()
            _uiState.value = currentState.copy(
                paths = emptyList(),
                canUndo = true,
                canRedo = false
            )
        }
    }

    fun canUndo(): Boolean {
        return undoStack.isNotEmpty()
    }

    fun canRedo(): Boolean {
        return redoStack.isNotEmpty()
    }

    fun saveDrawing(context: Context, bitmap: Bitmap) {
        val currentState = _uiState.value
        if (currentState is SketchUiState.PhotoSelected) {
            _uiState.value = SketchUiState.Saving(currentState.photoUri)
            
            try {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "sketch_${System.currentTimeMillis()}.png")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Sketches")
                }

                val uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                uri?.let {
                    val outputStream: OutputStream? = context.contentResolver.openOutputStream(it)
                    outputStream?.use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    }
                    _uiState.value = SketchUiState.Saved(it)
                } ?: run {
                    _uiState.value = SketchUiState.Error("Failed to save image")
                }
            } catch (e: Exception) {
                _uiState.value = SketchUiState.Error("Error saving: ${e.message}")
            }
        }
    }

    fun reset() {
        _uiState.value = SketchUiState.Initial
        undoStack.clear()
        redoStack.clear()
    }
}
