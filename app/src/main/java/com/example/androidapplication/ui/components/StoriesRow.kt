package com.example.androidapplication.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidapplication.models.story.StoryUserGroup

@Composable
fun StoriesRow(
    groups: List<StoryUserGroup>,
    onStoryClick: (StoryUserGroup) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp), // meilleur espacement
        contentPadding = PaddingValues(horizontal = 8.dp)   // ajoute un vrai centrage visuel
    ) {
        items(groups) { group ->
            StoryCircleWithName(
                imageUrl = group.coverImageUrl,
                username = group.userName,   // ⚠️ assure-toi que ce champ existe dans StoryUserGroup
                onClick = { onStoryClick(group) },
                modifier = Modifier.width(70.dp)
            )
        }
    }
}


