package com.example.androidapplication.ui.sketch

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.androidapplication.models.sketch.DrawingPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun createBitmapFromCanvas(
    context: Context,
    photoUri: Uri,
    opacity: Float,
    paths: List<DrawingPath>,
    width: Int = 1080,
    height: Int = 1920,
    includeBackground: Boolean = true
): Bitmap = withContext(Dispatchers.IO) {
    // Create bitmap
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    
    // Load background photo only if requested
    if (includeBackground) {
        try {
            val imageLoader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(photoUri)
                .size(width, height)
                .build()
            
            val result = (imageLoader.execute(request) as? SuccessResult)?.drawable
            result?.let { drawable ->
                val photoBitmap = drawable.toBitmap(width, height, Bitmap.Config.ARGB_8888)
                
                // Draw photo with opacity
                val paint = Paint().apply {
                    alpha = (opacity * 255).toInt()
                }
                canvas.drawBitmap(photoBitmap, 0f, 0f, paint)
            }
        } catch (e: Exception) {
            // If photo loading fails, just draw on white background
            canvas.drawColor(android.graphics.Color.WHITE)
        }
    } else {
        // If background is not included, draw white background so black sketches are visible
        canvas.drawColor(android.graphics.Color.WHITE)
    }
    
    // Draw all paths
    paths.forEach { drawingPath ->
        val paint = Paint().apply {
            color = drawingPath.color.toArgb()
            strokeWidth = drawingPath.strokeWidth
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isAntiAlias = true
            
            if (drawingPath.isEraser) {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            }
        }
        
        canvas.drawPath(drawingPath.path.asAndroidPath(), paint)
    }
    
    bitmap
}
