package com.example.androidapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.androidapplication.ui.container.NavGraph

@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentRoute = navController.currentDestination?.route ?: NavGraph.Home.route
    val isHomeSelected = currentRoute == NavGraph.Home.route
    val isProfileSelected = currentRoute == NavGraph.Profile.route
    val isArtistsSelected = currentRoute == NavGraph.ArtistList.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.Black.copy(alpha = 0.25f),
                shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
            )
            .padding(vertical = 16.dp, horizontal = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(),
                        onClick = {
                            if (currentRoute != NavGraph.Home.route) {
                                navController.navigate(NavGraph.Home.route) {
                                    popUpTo(NavGraph.Home.route) { inclusive = true }
                                }
                            }
                        }
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (isHomeSelected) Color.White else Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Home",
                    fontSize = 12.sp,
                    color = if (isHomeSelected) Color.White else Color.White.copy(alpha = 0.6f)
                )
            }

            // Profile
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(),
                        onClick = {
                            navController.navigate(NavGraph.Profile.route) {
                                popUpTo(NavGraph.Home.route)
                            }
                        }
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = if (isProfileSelected) Color.White else Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Profile",
                    fontSize = 12.sp,
                    color = if (isProfileSelected) Color.White else Color.White.copy(alpha = 0.6f)
                )
            }

            // Artists
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(),
                        onClick = {
                            navController.navigate(NavGraph.ArtistList.route) {
                                popUpTo(NavGraph.Home.route)
                            }
                        }
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Artists",
                    tint = if (isArtistsSelected) Color.White else Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Artists",
                    fontSize = 12.sp,
                    color = if (isArtistsSelected) Color.White else Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

