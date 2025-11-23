package com.example.androidapplication.remote

import com.google.gson.annotations.SerializedName
import com.example.androidapplication.models.Password.ForgotPassword
import com.example.androidapplication.models.Password.ResetPasswordRequest
import com.example.androidapplication.models.Password.ResetPasswordResponse
import com.example.androidapplication.models.Password.VerifyOtpRequest
import com.example.androidapplication.models.Password.VerifyOtpResponse
import com.example.androidapplication.models.login.LoginRequest
import com.example.androidapplication.models.login.LoginResponse
import com.example.androidapplication.models.register.RegisterResponse
import com.example.androidapplication.models.UserDataResponse
import com.example.androidapplication.models.artiste.ArtistListResponse
import com.example.androidapplication.models.logout.LogoutRequest
import com.example.androidapplication.models.logout.LogoutResponse
import com.example.androidapplication.models.Notification
import com.example.androidapplication.models.Photo
import com.example.androidapplication.models.UnreadCountResponse
import com.example.androidapplication.models.UploadPhotoResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
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
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

interface ApiService {
    @POST("signup") // Ensure this matches your backend's endpoint
    suspend fun registerUser(@Body request: RegisterRequest): RegisterResponse

    @POST("login")
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
    @GET("artists")
    suspend fun getArtists(@Query("theme") theme: String): ArtistListResponse
}

data class ConvertToSketchResponse(
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("sketch_url")
    val sketch_url: String? = null,
    @SerializedName("error")
    val error: String? = null
)

interface PhotoApiService {
    @GET("photos")
    suspend fun getAllPhotos(@Header("Authorization") token: String): Response<List<Photo>>

    @Multipart
    @POST("photos/upload")
    suspend fun uploadPhoto(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Part("title") title: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("isPortfolio") isPortfolio: RequestBody? = null
    ): Response<UploadPhotoResponse>

    @POST("photos/{id}/convert-to-sketch")
    suspend fun convertToSketch(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("id") photoId: String
    ): Response<ConvertToSketchResponse>

    @POST("photos/{id}/convert")
    suspend fun convertImage(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("id") photoId: String,
        @Query("style") style: String
    ): Response<ConvertToSketchResponse>

    @retrofit2.http.DELETE("photos/{id}")
    suspend fun deletePhoto(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("id") id: String
    ): Response<Unit>
}

interface NotificationApiService {
    @GET("notifications/me")
    suspend fun getMyNotifications(@Header("Authorization") token: String): Response<List<Notification>>

    @GET("notifications/unread-count")
    suspend fun getUnreadCount(@Header("Authorization") token: String): Response<UnreadCountResponse>

    @PUT("notifications/{id}/read")
    suspend fun markAsRead(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("id") id: String
    ): Response<Unit>

    @PUT("notifications/read-all")
    suspend fun markAllAsRead(@Header("Authorization") token: String): Response<Unit>
}

// Models for Stable Diffusion API
data class StableDiffusionRequest(
    val prompt: String,
    val negative_prompt: String? = null,
    val num_inference_steps: Int = 20,
    val guidance_scale: Float = 7.5f,
    val width: Int = 512,
    val height: Int = 512,
    val model: String = "stable-diffusion-v1-5" // or "stable-diffusion-xl"
)

data class StableDiffusionResponse(
    @SerializedName("images")
    val images: List<String>? = null, // Base64 encoded images
    @SerializedName("image_urls")
    val image_urls: List<String>? = null, // URLs if using external service
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("error")
    val error: String? = null
)

data class GenerateVideoRequest(
    val image: String // Base64 encoded image
)

data class GenerateAvatarRequest(
    val image: String, // Base64
    val style: String
)

data class GenerateAvatarResponse(
    val imageUrl: String,
    val status: String
)

data class MagicUpgradeRequest(
    val image: String // Base64
)

data class MagicUpgradeResponse(
    val imageUrl: String,
    val status: String
)

interface StableDiffusionApiService {
    @POST("generate")
    suspend fun generateImage(
        @Header("Authorization") token: String?,
        @Body request: StableDiffusionRequest
    ): Response<StableDiffusionResponse>

    @POST("generate-avatar")
    suspend fun generateAvatar(
        @Header("Authorization") token: String?,
        @Body request: GenerateAvatarRequest
    ): Response<GenerateAvatarResponse>

    @POST("magic-upgrade")
    suspend fun magicUpgrade(
        @Header("Authorization") token: String?,
        @Body request: MagicUpgradeRequest
    ): Response<MagicUpgradeResponse>
}

interface PortfolioApiService {
    @retrofit2.http.DELETE("portfolio/{id}")
    suspend fun deletePortfolioItem(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("id") id: String
    ): Response<Unit>
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3000/auth/" // Change to your backend URL
    private const val PHOTOS_BASE_URL = "http://10.0.2.2:3000/" // Photos endpoint is at root level
    private const val STABLE_DIFFUSION_BASE_URL = "http://10.0.2.2:3000/ai/" // Stable Diffusion endpoint

    // Create OkHttpClient with increased timeouts
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .build()
    }

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val photoInstance: PhotoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(PHOTOS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PhotoApiService::class.java)
    }

    val notificationInstance: NotificationApiService by lazy {
        Retrofit.Builder()
            .baseUrl(PHOTOS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotificationApiService::class.java)
    }

    val stableDiffusionInstance: StableDiffusionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(STABLE_DIFFUSION_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StableDiffusionApiService::class.java)
    }

    val portfolioInstance: PortfolioApiService by lazy {
        Retrofit.Builder()
            .baseUrl(PHOTOS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PortfolioApiService::class.java)
    }
}

