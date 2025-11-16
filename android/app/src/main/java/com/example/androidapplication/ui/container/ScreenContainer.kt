package com.example.androidapplication.ui.container

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidapplication.ui.screen.forgotpassword.ForgotPasswordScreen
import com.example.androidapplication.ui.screen.forgotpassword.VerifyOtpScreen
import com.example.androidapplication.ui.screen.home.HomeScreen
import com.example.androidapplication.ui.screen.login.LoginScreen
import com.example.androidapplication.ui.screen.registration.RegistrationScreen
import com.example.androidapplication.ui.screen.welcome.WelcomeScreen
import com.example.androidapplication.ui.screen.profile.ProfileScreen  // Import ProfileScreen
import com.example.androidapplication.ui.screen.profile.EditProfileScreen  // Import ProfileScreen
import com.example.androidapplication.ui.screen.resetpassword.ResetPasswordScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScreenContainer() {
    val navHost = rememberNavController()

    NavHost(
        navController = navHost,
        startDestination = NavGraph.Welcome.route//destination
    ) {
        composable(NavGraph.Welcome.route) {
            WelcomeScreen(
                onOpenLoginClicked = {
                    navHost.navigate(NavGraph.Login.route)
                }
            )
        }
        composable(NavGraph.Login.route) {
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
        composable(NavGraph.Registration.route) {
            RegistrationScreen(
                onprofileClicked = {
                    navHost.navigate(NavGraph.Profile.route)
                },
                onOpenLoginClicked = {
                    navHost.navigate(NavGraph.Home.route)
                }
            )
        }
        composable(NavGraph.Home.route) {

            HomeScreen(

            )
        }
        composable(NavGraph.Profile.route) {
            ProfileScreen(onEditClicked = {
                navHost.navigate(NavGraph.Edit.route)
            })
        }
        composable(NavGraph.Edit.route) {
            EditProfileScreen(onprofileClicked = {
                navHost.navigate(NavGraph.Profile.route)
            })
        }
        composable(NavGraph.ForgotPassword.route) {
            ForgotPasswordScreen(
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

    }}