package com.example.androidapplication.ui.sketch

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.androidapplication.models.sketch.DrawingPath

@Composable
fun DrawingCanvas(
    photoUri: Uri,
    opacity: Float,
    paths: List<DrawingPath>,
    currentPath: DrawingPath?,
    onDrawStart: (Offset) -> Unit,
    onDrawMove: (Offset) -> Unit,
    onDrawEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Background photo with opacity
    AsyncImage(
        model = photoUri,
        contentDescription = "Reference Photo",
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(alpha = opacity),
        contentScale = ContentScale.Fit
    )

    // Drawing canvas
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        onDrawStart(offset)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        onDrawMove(change.position)
                    },
                    onDragEnd = {
                        onDrawEnd()
                    }
                )
            }
    ) {
        // Draw all completed paths
        paths.forEach { drawingPath ->
            try {
                if (drawingPath.isEraser) {
                    drawPath(
                        path = drawingPath.path,
                        color = Color.Transparent,
                        style = Stroke(
                            width = drawingPath.strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        ),
                        blendMode = BlendMode.Clear
                    )
                } else {
                    drawPath(
                        path = drawingPath.path,
                        color = drawingPath.color,
                        style = Stroke(
                            width = drawingPath.strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            } catch (e: Exception) {
                // Prevent crash if path drawing fails
                e.printStackTrace()
            }
        }

        // Draw current path being drawn
        currentPath?.let { drawingPath ->
            try {
                if (drawingPath.isEraser) {
                    drawPath(
                        path = drawingPath.path,
                        color = Color.Transparent,
                        style = Stroke(
                            width = drawingPath.strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        ),
                        blendMode = BlendMode.Clear
                    )
                } else {
                    drawPath(
                        path = drawingPath.path,
                        color = drawingPath.color,
                        style = Stroke(
                            width = drawingPath.strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            } catch (e: Exception) {
                // Prevent crash if path drawing fails
                e.printStackTrace()
            }
        }
    }
}
