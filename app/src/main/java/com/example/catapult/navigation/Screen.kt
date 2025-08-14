package com.example.catapult.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object SignUp : Screen("sign_up")
    object List : Screen("breed_list")
    object Details : Screen("breed_details/{breedId}") {
        fun createRoute(id: String) = "breed_details/$id"
    }
    object Gallery : Screen("gallery/{breedId}") {
        fun createRoute(id: String) = "gallery/$id"
    }
    object ImagePager : Screen("image_pager/{breedId}/{imageId}") {
        fun createRoute(breedId: String, imageId: String) =
            "image_pager/$breedId/$imageId"
    }
    object Quiz : Screen("quiz")
    object Leaderboard : Screen("leaderboard")
    object Account : Screen("account")
}
