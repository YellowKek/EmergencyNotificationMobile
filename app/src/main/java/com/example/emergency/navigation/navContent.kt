package com.example.emergency.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.emergency.MainViewModel
import com.example.emergency.models.User
import com.example.emergency.pages.Main
import com.example.emergency.pages.Profile
import com.example.emergency.pages.RegistrationScreen
import com.example.emergency.util.ApiService

@Composable
fun NavContent(
    navController: NavHostController,
    user: User,
    mvm: MainViewModel,
    apiService: ApiService,
    ) {
    NavHost(
        navController = navController,
        startDestination = Page.MAIN.route,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable(Page.MAIN.route) {
            Main(
                modifier = Modifier.fillMaxSize(),
                navController = navController,
                apiService = apiService,
                user = user,
                mvm = mvm,

            )
        }

        composable(Page.PROFILE.route) {
            val userFromFile = mvm.getUser()
            Profile(
                user = userFromFile,
                mvm = mvm,
                apiService = apiService,
            )
        }

        composable(Page.REGISTRATION.route) {
            RegistrationScreen(
                navController = navController,
                user = user,
                mvm = mvm,
                apiService = apiService,

            )
        }
    }
}