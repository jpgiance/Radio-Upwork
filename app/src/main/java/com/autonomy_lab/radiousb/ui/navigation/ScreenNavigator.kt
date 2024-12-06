package com.autonomy_lab.radiousb.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.autonomy_lab.radiousb.ui.MainViewModel
import com.autonomy_lab.radiousb.ui.main.MainScreen
import com.autonomy_lab.radiousb.ui.tech.TechScreen

@Composable
fun ScreenNavigator(viewModel: MainViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = MainScreenRoute) {
        composable<MainScreenRoute> {
            MainScreen(navigateToTechScreen = { navController.navigate(TechScreenRoute) }, viewModel = viewModel)
        }

        composable<TechScreenRoute> {
            TechScreen(navigateToMainScreen =  { navController.popBackStack() }, viewModel = viewModel)
        }

    }
}