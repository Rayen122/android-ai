package com.example.androidapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun StoryCircleWithName(
    imageUrl: String?,
    username: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(70.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Cercle (image)
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .clickable { onClick() }
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Story",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Nom centré
        Text(
            text = if (username.length > 10) username.take(10) + "..." else username,
            color = Color.White,
            fontSize = 12.sp,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Medium
        )
    }
}

