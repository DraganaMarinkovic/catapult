package com.example.catapult.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.catapult.ui.details.BreedDetailsScreen
import com.example.catapult.ui.list.BreedListScreen
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.catapult.ui.account.AccountScreen
import com.example.catapult.viewmodel.account.SessionViewModel
import com.example.catapult.ui.account.SignUpScreen
import com.example.catapult.viewmodel.account.SignUpViewModel
import com.example.catapult.ui.gallery.GalleryScreen
import com.example.catapult.ui.gallery.ImagePagerScreen
import com.example.catapult.ui.leaderboard.LeaderboardScreen
import com.example.catapult.ui.quiz.QuizScreen
import kotlinx.coroutines.flow.collectLatest


@Composable
fun CatsNavHost(
    navController: NavHostController = rememberNavController()
) {

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        composable(Screen.Splash.route) {
            val sessionVm: SessionViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                sessionVm.hasAccount
                    .collectLatest { has ->
                        navController.popBackStack() //
                        val target = if (has) Screen.List.route else Screen.SignUp.route
                        navController.navigate(target) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
            }
             Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                 CircularProgressIndicator()
            }
        }

        composable(Screen.SignUp.route) {
            val signUpVm: SignUpViewModel = hiltViewModel()
            SignUpScreen(
                vm = signUpVm,
                onSignedUp = {
                    navController.navigate(Screen.List.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                        launchSingleTop = true
                        restoreState = false
                    }
                }
            )
        }

        composable(Screen.List.route) {
            BreedListScreen(
                onBreedClick = { breedId ->
                    navController.navigate(Screen.Details.createRoute(breedId))
                },
                onNavBarClick = {
                    navController.navigate(it) {
                        launchSingleTop = true
                        popUpTo(Screen.List.route) {
                            saveState = true
                        }
                        restoreState = true
                    }
                }
            )
        }

        composable(
            route = Screen.Details.route,
            arguments = listOf(navArgument("breedId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("breedId").orEmpty()
            BreedDetailsScreen(breedId = id, onGalleryClick = { navController.navigate(Screen.Gallery.createRoute(id)) }
            , onBack = {navController.popBackStack() })
        }

        composable(
            route = Screen.Gallery.route,
            arguments = listOf(navArgument("breedId"){
                type = NavType.StringType
            })
        ) { back ->
            val breedId = back.arguments!!.getString("breedId")!!
            GalleryScreen(
                breedId = breedId,
                onImageClick = { imageId ->
                    navController.navigate(Screen.ImagePager.createRoute(breedId, imageId))
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.ImagePager.route,
            arguments = listOf(
                navArgument("breedId"){ type = NavType.StringType },
                navArgument("imageId"){ type = NavType.StringType }
            )
        ) { back ->
            val breedId = back.arguments!!.getString("breedId")!!
            val imageId = back.arguments!!.getString("imageId")!!
            ImagePagerScreen(
                breedId = breedId,
                initialImageId = imageId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Quiz.route) {
            QuizScreen(navigateToLeaderboard = { navController.navigate(Screen.Leaderboard.route) {
                popUpTo(Screen.Quiz.route) {inclusive = true}
            } })
        }
        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(onNavBarClick = {
                navController.navigate(it) {
                    launchSingleTop = true
                    popUpTo(Screen.Leaderboard.route) {
                        saveState = true
                    }
                    restoreState = true
                }
            })
        }
        composable(Screen.Account.route) {
            AccountScreen(onNavBarClick = {
                navController.navigate(it) {
                    launchSingleTop = true
                    popUpTo(Screen.Account.route) {
                        saveState = true
                    }
                    restoreState = true
                }
            },
                onLogout = {
                    navController.popBackStack(
                        route = Screen.Account.route,
                        inclusive = true
                    )
                    navController.navigate(Screen.SignUp.route) {
                        launchSingleTop = true
                        restoreState = false
                    }
                })
        }
    }
}


