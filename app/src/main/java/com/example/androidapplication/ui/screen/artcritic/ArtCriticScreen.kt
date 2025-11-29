package com.example.androidapplication.ui.screen.artcritic

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidapplication.models.artcritic.ArtCriticViewModel
import com.example.androidapplication.ui.components.BackButton
import com.example.androidapplication.ui.theme.PrimaryYellowDark
import com.example.androidapplication.ui.theme.PrimaryYellowLight
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtCriticScreen(
    navController: NavController,
    viewModel: ArtCriticViewModel = viewModel()
) {
    val context = LocalContext.current

    // 🔥 ViewModel values
    val analysisResult by viewModel.analysisResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var question by remember { mutableStateOf("") }

    // Image Picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val stream: InputStream? = context.contentResolver.openInputStream(it)
            selectedBitmap = BitmapFactory.decodeStream(stream)
        }
    }

    // Animated Background
    val infiniteTransition = rememberInfiniteTransition()
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F0F1E),
                        Color(0xFF1A1A2E),
                        PrimaryYellowDark.copy(alpha = 0.35f + gradientOffset * 0.15f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // 🔙 Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                BackButton(navController)
                Spacer(Modifier.width(12.dp))
                Text(
                    "Art Critique",
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(24.dp))

            // IMAGE PREVIEW
            if (selectedBitmap == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.White.copy(alpha = 0.06f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Sélectionnez une image",
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            } else {
                Image(
                    bitmap = selectedBitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(22.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(20.dp))

            // 🔘 BUTTON — Choose image
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryYellowLight,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Choisir une image", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(20.dp))

            // 📝 QUESTION FIELD
            Text(
                "Décris ce que tu veux améliorer",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )

            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                placeholder = {
                    Text("Composition, couleurs, lumière, proportions…", color = Color.White.copy(0.5f))
                },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = PrimaryYellowLight,
                    unfocusedBorderColor = Color.White.copy(0.3f),
                    cursorColor = PrimaryYellowLight,
                    focusedLabelColor = PrimaryYellowLight
                )
            )

            Spacer(Modifier.height(20.dp))

            // 🔘 BUTTON — Analyze
            Button(
                onClick = {
                    if (selectedBitmap != null && question.isNotEmpty()) {
                        viewModel.sendToArtCritic(
                            bitmap = selectedBitmap!!,
                            question = question
                        )
                    }
                },
                enabled = selectedBitmap != null && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryYellowDark,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Analyser le dessin", fontWeight = FontWeight.Bold)
                }
            }


            Spacer(Modifier.height(24.dp))

            // 🔥 ERROR
            errorMessage?.let {
                Text(it, color = Color.Red, fontWeight = FontWeight.SemiBold)
            }

            // 🔥 RESULT
            analysisResult?.reply?.let { reply ->
                Spacer(Modifier.height(20.dp))
                ResultCard("Résultat de l'analyse", reply)
            }
        }
    }
}

@Composable
fun ResultCard(title: String, text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.06f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Text(
                text = title,
                color = PrimaryYellowLight,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

