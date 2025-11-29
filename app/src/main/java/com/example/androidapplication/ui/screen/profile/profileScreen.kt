package com.example.androidapplication.ui.screen.profile
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Folder
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.androidapplication.models.PhotoViewModel
import com.example.androidapplication.models.ProfileViewModel
import com.example.androidapplication.models.login.getAccessToken
import com.example.androidapplication.models.login.getRefreshToken
import com.example.androidapplication.models.logout.LogoutState
import com.example.androidapplication.models.logout.LogoutViewModel
import com.example.androidapplication.ui.components.BackButton
import com.example.androidapplication.ui.components.BottomNavigationBar
import com.example.androidapplication.ui.container.NavGraph
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import com.example.androidapplication.ui.theme.PrimaryYellowLight
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import com.example.androidapplication.models.story.StoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel(),
    photoViewModel: PhotoViewModel = viewModel(key = "shared_photo_viewmodel"),
    onEditClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    val token = getAccessToken(context)
    val logoutViewModel = remember { LogoutViewModel() }
    val logoutState by logoutViewModel.logoutState.collectAsState()
    var showProfileOptions by remember { mutableStateOf(false) }

    // Story ViewModel
    val storyViewModel: StoryViewModel = viewModel(key = "shared_story_viewmodel")

    // Profile data
    val userData by profileViewModel.userData.observeAsState()
    val profileError by profileViewModel.error.observeAsState()
    val uploadProfileImageSuccess by profileViewModel.uploadProfileImageSuccess.observeAsState(initial = false)

    // Photo data
    val myPhotos by photoViewModel.myPhotos.observeAsState(initial = emptyList())
    val isLoadingPhotos by photoViewModel.isLoading.observeAsState(initial = false)
    val uploadSuccess by photoViewModel.uploadSuccess.observeAsState(initial = false)
    val uploadError by photoViewModel.error.observeAsState()

    // Image picker state
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showUploadScreen by remember { mutableStateOf(false) }
    var isStoryMode by remember { mutableStateOf(false) } // true = story, false = photo normale

    // Image picker launcher pour photo normale ou story
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
                val inputStream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

            if (bitmap != null) {
                if (isStoryMode) {
                    // 🔥 Ajouter une story (loadStories est déjà appelé dans uploadStory)
                    storyViewModel.uploadStory(context, bitmap)
                } else {
                    // 🔥 Ajouter une photo normale - ouvrir le dialog d'upload
                    selectedBitmap = bitmap
                showUploadScreen = true
                }
            }
        }
    }

    // Image picker launcher pour photo de profil
    val profileImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {
                // 🔥 Modifier photo de profil
                profileViewModel.updateProfilePhoto(bitmap, context)
            }
        }
    }

    // Get current route to refresh when returning to profile
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    // Load profile and photos on first appearance
    LaunchedEffect(Unit) {
    if (!token.isNullOrEmpty()) {
            profileViewModel.fetchUserData()
        }
    }

    // Load photos when user data is available
    LaunchedEffect(userData?.id) {
        userData?.id?.let { userId ->
            photoViewModel.getMyPhotos(userId)
        }
    }

    // Refresh photos when returning to profile screen
    LaunchedEffect(currentRoute) {
        if (currentRoute == NavGraph.Profile.route && userData?.id != null) {
            userData?.id?.let { userId ->
                photoViewModel.getMyPhotos(userId)
            }
        }
    }

    // Handle upload success/error - close dialog
    LaunchedEffect(uploadSuccess, isLoadingPhotos) {
        if (uploadSuccess && !isLoadingPhotos) {
            // Upload completed successfully
            showUploadScreen = false
            selectedBitmap = null
            selectedImageUri = null
            kotlinx.coroutines.delay(2000)
            photoViewModel.clearUploadSuccess()
            userData?.id?.let { userId ->
                photoViewModel.getMyPhotos(userId)
            }
        } else if (!isLoadingPhotos && uploadError != null && uploadError!!.isNotEmpty() && showUploadScreen) {
            // Upload failed - keep dialog open to show error, but allow retry
            // Error will be shown in PhotoUploadScreen
        }
    }

    // Handle profile image upload success
    LaunchedEffect(uploadProfileImageSuccess) {
        if (uploadProfileImageSuccess) {
            kotlinx.coroutines.delay(2000)
            profileViewModel.clearUploadProfileImageSuccess()
        }
    }

    // Handle logout
    LaunchedEffect(logoutState) {
        if (logoutState is LogoutState.Success) {
            // Navigate to Welcome screen and clear entire back stack
            navController.navigate(NavGraph.Welcome.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Multiple animated gradients for dynamic background - animations plus lentes
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val gradientOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientOffset1"
    )
    val gradientOffset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientOffset2"
    )
    
    // Floating particles animation - plus lente
    val particleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleOffset"
    )
    
    // Pulsing effect for decorative elements - plus subtil
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    // Wave animation for dynamic background - plus lente
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )
    
    // Glow pulse animation - plus subtil
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )
    
    // Animation states
    var headerVisible by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(200)
        headerVisible = true
        delay(300)
        contentVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0F1E),
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        PrimaryYellowDark.copy(alpha = 0.3f + gradientOffset1 * 0.2f)
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        // Animated decorative elements
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top right animated circle - mouvement réduit
            val topCircleX = 50.dp + (gradientOffset1 * 15).dp
            val topCircleY = (-50).dp + (gradientOffset2 * 10).dp
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(200.dp + (gradientOffset1 * 15).dp)
                    .offset(x = topCircleX, y = topCircleY)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PrimaryYellowDark.copy(alpha = 0.25f + gradientOffset1 * 0.1f),
                                PrimaryYellowLight.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                    .graphicsLayer(alpha = 0.6f + gradientOffset1 * 0.1f)
            )
            
            // Bottom left animated circle - mouvement réduit
            val bottomCircleX = (-30).dp - (gradientOffset2 * 10).dp
            val bottomCircleY = 100.dp + (gradientOffset1 * 15).dp
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(160.dp + (gradientOffset2 * 20).dp)
                    .offset(x = bottomCircleX, y = bottomCircleY)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PrimaryYellowLight.copy(alpha = 0.2f + gradientOffset2 * 0.1f),
                                PrimaryYellowDark.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                    .graphicsLayer(alpha = 0.5f + gradientOffset2 * 0.15f)
            )
            
            // Enhanced floating particles effect with variety - réduit
            repeat(8) { index ->
                val angle = (particleOffset + index * 45f) * (kotlin.math.PI / 180f)
                val radius = 100.dp + (gradientOffset1 * 40).dp + (index * 8).dp
                val particleX = (cos(angle) * radius.value).dp
                val particleY = (sin(angle) * radius.value).dp
                val particleSize = (5.dp + (gradientOffset1 * 3).dp + ((index % 3) * 1.5).dp)
                val particleAlpha = (0.25f + glowPulse * 0.15f - (index * 0.02f)).coerceIn(0.15f, 0.5f)
                
                // Varying colors for particles
                val particleColor = when (index % 4) {
                    0 -> PrimaryYellowLight
                    1 -> PrimaryYellowDark
                    2 -> Color.White.copy(alpha = 0.6f)
                    else -> PrimaryYellowLight.copy(alpha = 0.8f)
                }
                
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(particleSize)
                        .offset(x = particleX, y = particleY)
                        .background(
                            particleColor.copy(alpha = particleAlpha),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                        .graphicsLayer(
                            alpha = particleAlpha,
                            scaleX = pulseScale,
                            scaleY = pulseScale
                        )
                        .shadow(
                            elevation = 2.dp,
                            shape = androidx.compose.foundation.shape.CircleShape,
                            spotColor = particleColor.copy(alpha = 0.3f)
                        )
                )
            }
            
            // Additional wave effect layers - plus subtiles
            repeat(2) { waveIndex ->
                val waveY = (waveOffset * 150f + waveIndex * 80f) % 300f
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.5.dp)
                        .offset(y = waveY.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    PrimaryYellowLight.copy(alpha = 0.15f - waveIndex * 0.05f),
                                    PrimaryYellowDark.copy(alpha = 0.1f - waveIndex * 0.03f),
                                    Color.Transparent
                                )
                            )
                        )
                        .graphicsLayer(alpha = 0.3f - waveIndex * 0.1f)
                )
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            // Animated Back button and Profile Header
            AnimatedVisibility(
                visible = headerVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(animationSpec = tween(800)),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Back button and Logout button at top
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 56.dp, bottom = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BackButton(navController = navController)
                        
                        // Logout Button - Circular with glow effect
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = false),
                                    onClick = {
                                        val refreshToken = getRefreshToken(context)
                                        if (refreshToken != null) {
                                            logoutViewModel.logout(refreshToken, context)
                                        }
                                    }
                                )
                                .background(
                                    PrimaryYellowDark.copy(alpha = 0.9f),
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                                .shadow(
                                    elevation = 8.dp,
                                    shape = androidx.compose.foundation.shape.CircleShape,
                                    spotColor = PrimaryYellowDark.copy(alpha = 0.4f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Modern Profile Header - sans cadre
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Profile Picture with modern style
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                PrimaryYellowDark.copy(alpha = 0.3f),
                                                PrimaryYellowLight.copy(alpha = 0.2f)
                                            )
                                        )
                                    )
                                    .shadow(8.dp, shape = CircleShape)
                            ) {
                                // Afficher la photo de profil si elle existe
                                if (!userData?.profileImageUrl.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = userData?.profileImageUrl?.replace("localhost", "10.0.2.2"),
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            PrimaryYellowDark.copy(alpha = 0.15f),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profile",
                                        modifier = Modifier.size(50.dp),
                                        tint = PrimaryYellowDark
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black)
                                        .clickable {
                                            showProfileOptions = true   // 👈 Ouvre le menu
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Options",
                                        tint = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = userData?.name ?: "Utilisateur",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier
                                    .graphicsLayer(
                                        alpha = 0.95f + gradientOffset1 * 0.05f
                                    )
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Stats Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${myPhotos.size}",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Publications",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "0",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Abonnés",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "0",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Abonnements",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Portfolio Button
                            Button(
                                onClick = { navController.navigate(NavGraph.Portfolio.route) },
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(50.dp)
                                    .shadow(8.dp, RoundedCornerShape(25.dp)),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF6C63FF), // Purple color for Portfolio
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(25.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Folder,
                                        contentDescription = "Portfolio",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Mon Portfolio",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Modern Photo Upload Section
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) +
                        slideInVertically(
                            initialOffsetY = { 20 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Selected Image Preview
                        selectedBitmap?.let { bitmap ->
                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .shadow(
                                        elevation = 8.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        spotColor = PrimaryYellowDark.copy(alpha = 0.3f)
                                    )
                                    .clip(RoundedCornerShape(16.dp))
                            ) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Selected photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        // Modern Upload Button
                        val buttonInteractionSource = remember { MutableInteractionSource() }
                        val isPressed by buttonInteractionSource.collectIsPressedAsState()
                        val buttonScale by animateFloatAsState(
                            targetValue = if (isPressed) 0.95f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessHigh
                            ),
                            label = "buttonScale"
                        )
                        
                        val buttonElevation by animateFloatAsState(
                            targetValue = if (isPressed) 4f else 12f,
                            animationSpec = tween(200),
                            label = "buttonElevation"
                        )
                        
                        Button(
                            onClick = {
                                if (selectedBitmap != null) {
                                    showUploadScreen = true
                                } else {
                                    isStoryMode = false  // Mode photo normale
                                    imagePickerLauncher.launch("image/*")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .scale(buttonScale)
                                .shadow(buttonElevation.dp, RoundedCornerShape(16.dp)),
                            interactionSource = buttonInteractionSource,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryYellowDark,
                                contentColor = Color.White
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (selectedBitmap == null)
                                        Icons.Default.Add
                                    else
                                        Icons.Default.Upload,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = if (selectedBitmap == null) "Ajouter une photo" else "Publier la photo",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Success Message
            AnimatedVisibility(
                visible = uploadSuccess,
                enter = fadeIn(animationSpec = tween(400)) + slideInVertically(),
                exit = fadeOut(animationSpec = tween(400))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF00C853).copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "Photo ajoutée avec succès !",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Success Message for profile image upload
            AnimatedVisibility(
                visible = uploadProfileImageSuccess,
                enter = fadeIn(animationSpec = tween(400)) + slideInVertically(),
                exit = fadeOut(animationSpec = tween(400))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF00C853).copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "Photo de profil mise à jour avec succès !",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // My Photos Grid with modern design
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) +
                        slideInVertically(
                            initialOffsetY = { 30 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Mes publications",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
                    )

                        if (isLoadingPhotos) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = PrimaryYellowDark
                                )
                            }
                        } else if (myPhotos.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Aucune publication",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(bottom = 100.dp)
                            ) {
                                items(
                                    items = myPhotos,
                                    key = { photo -> photo.id }
                                ) { photo ->
                                    Box(
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .shadow(
                                                elevation = 4.dp,
                                                shape = RoundedCornerShape(12.dp),
                                                spotColor = PrimaryYellowDark.copy(alpha = 0.3f)
                                            )
                                            .clip(RoundedCornerShape(12.dp))
                                    ) {
                                        AsyncImage(
                                            model = photo.imageUrl,
                                            contentDescription = photo.title ?: "Photo",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop,
                                            placeholder = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_gallery),
                                            error = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_report_image)
                                        )
                                    }
                                }
                            }
                        }
                }
            }

            // Bottom Navigation Bar - Fixed at bottom
            BottomNavigationBar(navController = navController)
        }
    }

    // Photo Upload Screen
    if (showUploadScreen && selectedBitmap != null) {
        PhotoUploadScreen(
            bitmap = selectedBitmap!!,
            isUploading = isLoadingPhotos,
            errorMessage = photoViewModel.error.value,
            onDismiss = {
                showUploadScreen = false
                selectedBitmap = null
                selectedImageUri = null
                photoViewModel.clearError()
            },
            onUploadComplete = { title, description ->
                photoViewModel.clearError()
                photoViewModel.uploadPhoto(selectedBitmap!!, title, description)
            }
        )
    }

    // ModalBottomSheet pour choisir entre modifier photo de profil ou ajouter une story
    if (showProfileOptions) {
        ModalBottomSheet(
            onDismissRequest = { showProfileOptions = false },
            containerColor = Color(0xFF1A1A1A),
            contentColor = Color.White
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Choisir une action",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(20.dp))

                // 📸 Modifier photo de profil
                Button(
                    onClick = {
                        profileImagePickerLauncher.launch("image/*")
                        showProfileOptions = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryYellowDark)
                ) {
                    Text("📸 Modifier photo de profil")
                }

                Spacer(Modifier.height(10.dp))

                // 🟡 Ajouter une Story
                Button(
                    onClick = {
                        isStoryMode = true
                        imagePickerLauncher.launch("image/*")
                        showProfileOptions = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryYellowLight)
                ) {
                    Text("🟡 Ajouter une Story")
                }

                Spacer(Modifier.height(30.dp))
            }
        }
    }
}

