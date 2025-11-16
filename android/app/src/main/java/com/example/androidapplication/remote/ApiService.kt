package com.example.androidapplication.remote
import com.example.androidapplication.models.Password.ForgotPassword
import com.example.androidapplication.models.Password.ResetPasswordRequest
import com.example.androidapplication.models.Password.ResetPasswordResponse
import com.example.androidapplication.models.Password.VerifyOtpRequest
import com.example.androidapplication.models.Password.VerifyOtpResponse
import com.example.androidapplication.models.login.LoginRequest
import com.example.androidapplication.models.login.LoginResponse
import com.example.androidapplication.models.register.RegisterResponse
import com.example.androidapplication.models.UserDataResponse
import com.example.androidapplication.models.logout.LogoutRequest
import com.example.androidapplication.models.logout.LogoutResponse
import com.example.androidapplication.models.Photo
import com.example.androidapplication.models.UploadPhotoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

interface ApiService {
    @POST("signup") // Ensure this matches your backend's endpoint
    suspend fun registerUser(@Body request: RegisterRequest): RegisterResponse

    @POST("login") // Replace with your actual endpoint for login
    suspend fun loginUser(@Body request: LoginRequest): LoginResponse
    @GET("profile")
    suspend fun getUserProfile(@Header("Authorization") token: String): Response<UserDataResponse>

    @POST("logout")
    suspend fun logout(@Body logoutRequest: LogoutRequest): Response<LogoutResponse>

    @POST("forgot-password")
    suspend fun forgotPassword(@Body forgotPassword: ForgotPassword): Response<Unit>
    @PUT("reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): ResetPasswordResponse

    @POST("verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): VerifyOtpResponse

}

interface PhotoApiService {
    @GET("photos")
    suspend fun getAllPhotos(@Header("Authorization") token: String): Response<List<Photo>>

    @Multipart
    @POST("photos/upload")
    suspend fun uploadPhoto(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Part("title") title: RequestBody?,
        @Part("description") description: RequestBody?
    ): Response<UploadPhotoResponse>
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3000/auth/" // Change to your backend URL
    private const val PHOTOS_BASE_URL = "http://10.0.2.2:3000/" // Photos endpoint is at root level

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val photoInstance: PhotoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(PHOTOS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PhotoApiService::class.java)
    }
}

