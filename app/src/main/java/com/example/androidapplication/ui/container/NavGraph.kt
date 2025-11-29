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

    data object ArtistList: NavGraph(route = "meal_screen")

    data object ArtistDetail: NavGraph(route = "ArtistDetail")

    data object Notifications: NavGraph(route = "notifications_screen")
    
    data object PhotoDetail: NavGraph(route = "photo_detail_screen")
    
    data object Camera: NavGraph(route = "camera_screen")
    
    data object AIEditor: NavGraph(route = "ai_editor_screen")
    
    data object Album: NavGraph(route = "album_screen")

    data object Genrerai: NavGraph(route = "genrerai_screen")

    data object Studio: NavGraph(route = "studio_screen")

    data object MagicPaintbrush: NavGraph(route = "magic_paintbrush_screen")

    data object Portfolio: NavGraph(route = "portfolio_screen")

    data object PaintingDetail: NavGraph(route = "painting_detail_screen")

    data object AvatarGenerator: NavGraph(route = "avatar_generator")

    data object SketchSearch: NavGraph(route = "sketch_search_screen")
    
    data object PaintingProcess: NavGraph(route = "painting_process_screen")
    
    data object Sketch: NavGraph(route = "sketch_screen")
    
    data object StoryViewer: NavGraph(route = "story_viewer_screen")
    
    data object ArtCritic: NavGraph(route = "art_critic_screen")
}
