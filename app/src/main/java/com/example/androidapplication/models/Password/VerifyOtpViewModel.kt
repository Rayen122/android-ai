import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapplication.models.Password.VerifyOtpRequest
import com.example.androidapplication.remote.ApiService
import com.example.androidapplication.remote.RetrofitClient
import kotlinx.coroutines.launch

class VerifyOtpViewModel: ViewModel() {

    fun verifyOtp(
        otp: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.verifyOtp(VerifyOtpRequest(recoveryCode = otp))
                onSuccess(response.resetToken)
            } catch (e: Exception) {
                onError("Invalid or expired OTP")
            }
        }
    }
}
