package com.example.androidapplication.ui.container

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidapplication.models.login.getSavedTokens
import com.example.androidapplication.ui.artiste.ArtistDetailScreen
import com.example.androidapplication.ui.artiste.ArtistListScreen
import com.example.androidapplication.ui.screen.forgotpassword.ForgotPasswordScreen
import com.example.androidapplication.ui.screen.forgotpassword.VerifyOtpScreen
import com.example.androidapplication.ui.screen.home.HomeScreen
import com.example.androidapplication.ui.screen.login.LoginScreen
import com.example.androidapplication.ui.screen.welcome.WelcomeScreen
import com.example.androidapplication.ui.screen.profile.ProfileScreen  // Import ProfileScreen
import com.example.androidapplication.ui.screen.profile.EditProfileScreen  // Import ProfileScreen
import com.example.androidapplication.ui.screen.notifications.NotificationsScreen
import com.example.androidapplication.ui.screen.home.PhotoDetailScreen
import com.example.androidapplication.ui.screen.resetpassword.ResetPasswordScreen
import com.example.androidapplication.ui.screen.registration.RegistrationScreen
import com.example.androidapplication.ui.screen.genrerai.GenreraiScreen
import com.example.androidapplication.ui.screen.studio.StudioScreen
import com.example.androidapplication.ui.screen.magicpaintbrush.MagicPaintbrushScreen
import com.example.androidapplication.ui.screen.portfolio.PortfolioScreen
import com.example.androidapplication.ui.screen.portfolio.PaintingDetailScreen
import com.example.androidapplication.ui.avatargenerator.AvatarGeneratorScreen
import com.example.androidapplication.models.artiste.ArtistViewModel
import com.example.androidapplication.ui.screen.sketch.SketchSearchScreen
import com.example.androidapplication.ui.sketch.SketchScreen
import com.example.androidapplication.models.story.StoryViewModel
import com.example.androidapplication.ui.screen.artcritic.ArtCriticScreen
import com.example.androidapplication.ui.screen.story.StoryViewerScreen
import java.net.URLDecoder


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScreenContainer() {
    val navHost = rememberNavController()
    val context = LocalContext.current
    val artistViewModel: ArtistViewModel = viewModel()
    val photoViewModel: com.example.androidapplication.models.PhotoViewModel = viewModel(key = "shared_photo_viewmodel")
    val storyViewModel: StoryViewModel = viewModel(key = "shared_story_viewmodel")

    val (accessToken, refreshToken) = getSavedTokens(context)
    val startDestination = if (!refreshToken.isNullOrEmpty()) {
        NavGraph.Home.route
    } else {
        NavGraph.Welcome.route
    }

    NavHost(
        navController = navHost,
        startDestination = startDestination
    ){
        composable(NavGraph.Welcome.route) {
            WelcomeScreen(
                onOpenLoginClicked = {
                    navHost.navigate(NavGraph.Login.route)
                },
                onAutoLogin = {
                    navHost.navigate(NavGraph.Home.route) {
                        popUpTo(NavGraph.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = NavGraph.Login.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            LoginScreen(
                onLoginClicked = {
                    navHost.navigate(NavGraph.Home.route) {
                        popUpTo(NavGraph.Login.route) { inclusive = true }
                    }
                },
                onRegistrationClicked = {
                    navHost.navigate(NavGraph.Registration.route)
                },
                onForgotPasswordClicked = { navHost.navigate(NavGraph.ForgotPassword.route) }
            )
        }
        composable(
            route = NavGraph.Registration.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            RegistrationScreen(
                onOpenLoginClicked = {
                    navHost.navigate(NavGraph.Login.route) {
                        popUpTo(NavGraph.Login.route) { inclusive = false }
                    }
                },
                onprofileClicked = {
                    navHost.navigate(NavGraph.Login.route) {
                        popUpTo(NavGraph.Registration.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = NavGraph.Home.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            HomeScreen(navController = navHost, photoViewModel = photoViewModel, storyViewModel = storyViewModel)
        }

        composable(
            route = NavGraph.Profile.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            ProfileScreen(
                navController = navHost,
                photoViewModel = photoViewModel,
                onEditClicked = {
                    navHost.navigate(NavGraph.Edit.route)
                }
            )
        }

        composable(
            route = NavGraph.Notifications.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            NotificationsScreen(navController = navHost)
        }

        composable(
            route = "${NavGraph.PhotoDetail.route}/{photoId}",
            arguments = listOf(
                navArgument("photoId") { type = NavType.StringType }
            ),
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            },
            popEnterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            }
        ) { backStackEntry ->
            val photoId = backStackEntry.arguments?.getString("photoId") ?: ""
            PhotoDetailScreen(
                photoId = photoId,
                navController = navHost,
                photoViewModel = photoViewModel
            )
        }
        composable(NavGraph.Edit.route) {
            EditProfileScreen(onprofileClicked = {
                navHost.navigate(NavGraph.Profile.route)
            })
        }
        composable(NavGraph.ForgotPassword.route) {
            ForgotPasswordScreen(
                navController = navHost,
                onRestPasswordClicked = { email ->
                    navHost.navigate(NavGraph.VerifyOtp.route + "?email=$email")
                },
                onBackToLoginClicked = {
                    navHost.navigate(NavGraph.Login.route) // 👈 navigate back to Login screen
                }
            )
        }


        composable(NavGraph.VerifyOtp.route) { backStackEntry ->
            VerifyOtpScreen(navHost = navHost)
        }

        composable(
            route = NavGraph.ResetPassword.route + "?resetToken={resetToken}",
            arguments = listOf(navArgument("resetToken") { defaultValue = "" })
        ) { backStackEntry ->
            val resetToken = backStackEntry.arguments?.getString("resetToken") ?: ""
            ResetPasswordScreen(navHost = navHost, resetToken = resetToken)
        }

        composable(
            route = NavGraph.ArtistList.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            // Pass the ViewModel instance to the ArtistListScreen
            ArtistListScreen(navController = navHost, artistViewModel = artistViewModel)
        }

        composable(
            route = "artistDetail/{artistName}",
            arguments = listOf(
                navArgument("artistName") { type = NavType.StringType }
            ),
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            },
            popEnterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            }
        ) { backStackEntry ->
            // Retrieve arguments from the backStackEntry
            val artistName = URLDecoder.decode(backStackEntry.arguments?.getString("artistName") ?: "", "UTF-8")

            ArtistDetailScreen(
                navController = navHost,
                artistViewModel = artistViewModel,
                artistName = artistName
            )
        }

        composable(
            route = NavGraph.Genrerai.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            GenreraiScreen(navController = navHost)
        }

        composable(
            route = NavGraph.Studio.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            StudioScreen(navController = navHost)
        }

        composable(
            route = NavGraph.Portfolio.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            PortfolioScreen(
                navController = navHost,
                photoViewModel = photoViewModel
            )
        }

        composable(
            route = "${NavGraph.PaintingDetail.route}/{paintingId}",
            arguments = listOf(
                navArgument("paintingId") { type = NavType.StringType }
            ),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) { backStackEntry ->
            val paintingId = backStackEntry.arguments?.getString("paintingId") ?: ""
            PaintingDetailScreen(
                navController = navHost, 
                paintingId = paintingId,
                photoViewModel = photoViewModel
            )
        }

        composable(
            route = "${NavGraph.MagicPaintbrush.route}?imageUrl={imageUrl}&photoId={photoId}",
            arguments = listOf(
                navArgument("imageUrl") { 
                    type = NavType.StringType 
                    nullable = true
                    defaultValue = null
                },
                navArgument("photoId") { 
                    type = NavType.StringType 
                    nullable = true
                    defaultValue = null
                }
            ),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("imageUrl")
            val photoId = backStackEntry.arguments?.getString("photoId")
            
            val imageUrl = if (encodedUrl != null) {
                try {
                    URLDecoder.decode(encodedUrl, "UTF-8")
                } catch (e: Exception) {
                    encodedUrl
                }
            } else {
                null
            }
            
            MagicPaintbrushScreen(
                navController = navHost, 
                photoViewModel = photoViewModel,
                initialImageUrl = imageUrl,
                photoId = photoId
            )
        }



        composable(
            route = NavGraph.AvatarGenerator.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
<<<<<<< HEAD
            AvatarGeneratorScreen(navController = navHost)
        }

        composable(
            route = NavGraph.SketchSearch.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            SketchSearchScreen(navController = navHost)
        }

        composable(
            route = NavGraph.PaintingProcess.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            com.example.androidapplication.ui.artiste.PaintingProcessScreen(navController = navHost)
        }

        composable(
            route = NavGraph.Sketch.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            SketchScreen(navController = navHost)
        }

        composable(
            route = NavGraph.StoryViewer.route,
            enterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            }
        ) {
            StoryViewerScreen(
                navController = navHost,
                storyViewModel = storyViewModel
            )
        }

        composable(
            route = NavGraph.ArtCritic.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            ArtCriticScreen(navController = navHost)
=======
            AvatarGeneratorScreen()
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
        }
    }

}
