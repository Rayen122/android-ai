package com.example.androidapplication.models

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapplication.models.login.getAccessToken
import com.example.androidapplication.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CommentViewModel : ViewModel() {
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    private val _addCommentSuccess = MutableStateFlow<Boolean>(false)
    val addCommentSuccess: StateFlow<Boolean> = _addCommentSuccess

    fun getComments(photoId: String, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Le GET comments n'a pas besoin d'authentification selon le backend
                // Mais on envoie quand même le token au cas où
                val token = getAccessToken(context)
                val authHeader = if (token != null) "Bearer $token" else ""
                Log.d("CommentViewModel", "Getting comments for photo: $photoId")
                Log.d("CommentViewModel", "Full URL would be: http://10.0.2.2:3000/comments/$photoId")
                if (token != null) {
                    Log.d("CommentViewModel", "Token: ${token.take(20)}...")
                } else {
                    Log.w("CommentViewModel", "No token, trying without auth")
                }
                try {
                    val response = RetrofitClient.commentInstance.getComments(authHeader, photoId)
                    Log.d("CommentViewModel", "Response code: ${response.code()}, message: ${response.message()}")
                    if (response.isSuccessful) {
                        val commentsList = response.body() ?: emptyList()
                        Log.d("CommentViewModel", "Comments loaded: ${commentsList.size}")
                        _comments.value = commentsList
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("CommentViewModel", "Failed to load comments: ${response.code()} - ${response.message()}")
                        Log.e("CommentViewModel", "Error body: $errorBody")
                        _error.value = "Failed to load comments: ${response.code()}"
                    }
                } catch (e: Exception) {
                    Log.e("CommentViewModel", "Exception getting comments", e)
                    _error.value = "Error: ${e.message}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("CommentViewModel", "Error getting comments", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addComment(photoId: String, text: String, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = getAccessToken(context)
                if (token == null) {
                    Log.e("CommentViewModel", "Token is null")
                    _error.value = "Authentication required"
                    return@launch
                }
                Log.d("CommentViewModel", "Adding comment to photo: $photoId, text: $text")
                Log.d("CommentViewModel", "Full URL would be: http://10.0.2.2:3000/comments/$photoId")
                Log.d("CommentViewModel", "Token: ${token.take(20)}...")
                val request = CommentRequest(text)
                Log.d("CommentViewModel", "Request body: CommentRequest(text=$text)")
                try {
                    val response = RetrofitClient.commentInstance.addComment("Bearer $token", photoId, request)
                    Log.d("CommentViewModel", "Response code: ${response.code()}, message: ${response.message()}")
                    if (response.isSuccessful) {
                        val comment = response.body()
                        Log.d("CommentViewModel", "Comment added successfully: $comment")
                        _addCommentSuccess.value = true
                        // Refresh comments pour afficher le nouveau commentaire
                        getComments(photoId, context)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("CommentViewModel", "Failed to add comment: ${response.code()} - ${response.message()}")
                        Log.e("CommentViewModel", "Error body: $errorBody")
                        _error.value = "Failed to add comment: ${response.code()} - ${response.message()}"
                    }
                } catch (e: Exception) {
                    Log.e("CommentViewModel", "Exception adding comment", e)
                    _error.value = "Error: ${e.message}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("CommentViewModel", "Error adding comment", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearAddCommentSuccess() {
        _addCommentSuccess.value = false
    }

    fun deleteComment(commentId: String, photoId: String, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = getAccessToken(context) ?: return@launch
                val response = RetrofitClient.commentInstance.deleteComment("Bearer $token", commentId)
                if (response.isSuccessful) {
                    // Refresh comments
                    getComments(photoId, context)
                } else {
                    _error.value = "Failed to delete comment"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("CommentViewModel", "Error deleting comment", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

