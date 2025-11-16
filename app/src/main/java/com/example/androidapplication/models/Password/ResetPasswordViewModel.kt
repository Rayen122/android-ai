import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapplication.models.Password.ResetPasswordRequest
import com.example.androidapplication.remote.ApiService
import com.example.androidapplication.remote.RetrofitClient
import kotlinx.coroutines.launch

class ResetPasswordViewModel: ViewModel() {

    fun resetPassword(
        newPassword: String,
        resetToken: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("ResetPassword", "Sending token: $resetToken, newPassword: $newPassword")
                val response = RetrofitClient.instance.resetPassword(
                    ResetPasswordRequest(newPassword, resetToken)
                )
                Log.d("ResetPassword", "Response: $response")
                onSuccess(response.message)
            } catch (e: Exception) {
                Log.e("ResetPassword", "Exception", e)
                onError("Failed to reset password. Token may be invalid or expired.")
            }
        }
    }}
