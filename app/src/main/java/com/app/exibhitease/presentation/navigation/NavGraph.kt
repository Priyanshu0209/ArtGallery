package com.app.exibhitease.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.app.exibhitease.presentation.ar_screen.ArScreen
import com.app.exibhitease.presentation.home_Screen.FavouriteScreen
import com.app.exibhitease.presentation.home_Screen.HomeScreen
import com.app.exibhitease.presentation.onboarding_screen.OnBoardingScreen
import com.app.exibhitease.presentation.settings_screen.SettingsEvent
import com.app.exibhitease.presentation.settings_screen.SettingsViewModel
import com.app.exibhitease.presentation.viewmodel.SharedViewModel

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String,
    finishActivity: () -> Unit,
    settingsViewModel: SettingsViewModel
) {

    val actions = remember(navController) { MainActions(navController) }
    val viewModel: SharedViewModel = viewModel()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {


        composable(route = Route.OnBoardingScreen.route) { backStackEntry: NavBackStackEntry ->
            BackHandler {
                finishActivity()
            }
            OnBoardingScreen {
                when (it) {
                    SettingsEvent.SetFirstLaunch -> settingsViewModel.onEvent(it)
                    else -> Unit
                }
                actions.onBoardingComplete(backStackEntry)
            }

        }

        navigation(
            route = Route.HomeScreen.route,
            startDestination = ExibhiteaseTabs.Home.route
        ) {
            composable(route = ExibhiteaseTabs.Home.route) {
                BackHandler {
                    finishActivity()
                }
                HomeScreen(
                    onclick = {
                        viewModel.selectedArt(it)
                        navController.navigate(Route.ArScreen.route)
                    }
                )
            }
            composable(route = ExibhiteaseTabs.Favourites.route) {
                FavouriteScreen()
            }
        }

        composable(route = Route.ArScreen.route) {
            ArScreen(
                viewModel = viewModel
            )
        }

    }

}

class MainActions(navController: NavController) {
    val onBoardingComplete: (NavBackStackEntry) -> Unit = { from ->
        if (from.lifecycleIsResumed()) {
            navController.navigate(Route.HomeScreen.route)
        }
    }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED