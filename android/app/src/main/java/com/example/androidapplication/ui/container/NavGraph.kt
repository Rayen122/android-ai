package com.example.androidapplication.ui.container

sealed class NavGraph(val route: String) {
    data object Welcome: NavGraph(route = "welcome_screen")
    data object Login: NavGraph(route = "login_screen")
    data object Registration: NavGraph(route = "registration_screen")
    data object Home: NavGraph(route = "home_screen")
    data object Profile: NavGraph(route = "profile_screen")
    data object Edit: NavGraph(route = "edit_screen")
    data object ForgotPassword: NavGraph(route = "ForgotPassword_screen")
    data object ResetPassword: NavGraph(route = "resetpassword_screen")
    data object Gender: NavGraph(route = "Gender_screen")

    data object VerifyOtp: NavGraph(route = "VerifyOtp")


    data object Meal: NavGraph(route = "meal_screen")


}