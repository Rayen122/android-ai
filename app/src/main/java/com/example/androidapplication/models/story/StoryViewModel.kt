package com.example.androidapplication.models.story

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapplication.models.login.getAccessToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoryViewModel : ViewModel() {

    private val repository = StoryRepository()

    private val _storyGroups = MutableStateFlow<List<StoryUserGroup>>(emptyList())
    val storyGroups: StateFlow<List<StoryUserGroup>> = _storyGroups.asStateFlow()

    private val _viewerStories = MutableStateFlow<List<Story>>(emptyList())
    val viewerStories: StateFlow<List<Story>> = _viewerStories.asStateFlow()

    fun loadStories(context: Context) {
        viewModelScope.launch {
            try {
                val token = getAccessToken(context) ?: return@launch
                saveTokenBackupIfNeeded(context, token)

                val stories = repository.getStories(token)

                val grouped = stories
                    .groupBy { it.userId._id }
                    .map { (userId, list) ->
                        StoryUserGroup(
                            userId = userId,
                            userName = list.first().userId.name,
                            coverImageUrl = list.first().imageUrl,
                            stories = list
                        )
                    }

                _storyGroups.value = grouped

            } catch (e: Exception) {
                _storyGroups.value = emptyList()
            }
        }
    }

    fun openStories(list: List<Story>) {
        _viewerStories.value = list
    }

    fun uploadStory(context: Context, bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                val token = getAccessToken(context) ?: return@launch
                repository.uploadStory(token, bitmap)
                loadStories(context)  // Refresh after upload
            } catch (_: Exception) {}
        }
    }
    private fun saveTokenBackupIfNeeded(context: Context, token: String) {
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val saved = prefs.getString("accessToken", null)

        if (saved == null) {
            prefs.edit().putString("accessToken", token).apply()
        }
    }

}

