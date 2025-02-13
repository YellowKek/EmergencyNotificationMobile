package com.example.emergency.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.emergency.models.User
import com.example.emergency.pages.Main
import com.example.emergency.pages.Profile

@Composable
fun NavContent(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Page.MAIN.route,
        modifier = Modifier.fillMaxSize(),

        ) {
        composable(Page.MAIN.route) {
            Main(
                modifier = Modifier.fillMaxSize(),
                navController = navController
            )
        }

        composable(Page.PROFILE.route) {
            Profile(
                user = User(
                    id = 1,
                    name = "Дамир",
                    surname = "Гарифуллин",
                    email = "damirgarifullin7@gmail.com",
                    password = ""
                )
            )
        }
    }
}