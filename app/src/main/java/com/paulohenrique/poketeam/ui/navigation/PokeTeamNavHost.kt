package com.paulohenrique.poketeam.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.paulohenrique.poketeam.di.AppContainer
import com.paulohenrique.poketeam.ui.auth.AuthViewModel
import com.paulohenrique.poketeam.ui.auth.ForgotPasswordScreen
import com.paulohenrique.poketeam.ui.auth.LoginScreen
import com.paulohenrique.poketeam.ui.auth.RegisterScreen
import com.paulohenrique.poketeam.ui.detail.DetailScreen
import com.paulohenrique.poketeam.ui.detail.DetailViewModel
import com.paulohenrique.poketeam.ui.favorites.FavoritesScreen
import com.paulohenrique.poketeam.ui.favorites.FavoritesViewModel
import com.paulohenrique.poketeam.ui.pokedex.PokedexScreen
import com.paulohenrique.poketeam.ui.pokedex.PokedexViewModel
import com.paulohenrique.poketeam.ui.team.TeamScreen
import com.paulohenrique.poketeam.ui.team.TeamViewModel
import com.paulohenrique.poketeam.util.factoryOf

private data class TabItem(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    TabItem(Routes.POKEDEX, "Pokédex", Icons.Filled.CatchingPokemon),
    TabItem(Routes.FAVORITES, "Favoritos", Icons.Filled.Favorite),
    TabItem(Routes.TEAM, "Meu time", Icons.Filled.Groups)
)

@Composable
fun PokeTeamNavHost(container: AppContainer) {
    val nav = rememberNavController()
    val authVm: AuthViewModel = viewModel(factory = factoryOf { AuthViewModel(container.authRepository) })
    val auth by authVm.state.collectAsStateWithLifecycle()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route

    // A tela inicial depende de a pessoa já estar logada (sessão persistida pelo Firebase).
    val startDestination = remember { if (authVm.state.value.isLoggedIn) Routes.POKEDEX else Routes.LOGIN }

    // Reage a login/logout: leva para a Pokédex ou volta para o login, limpando o back stack.
    LaunchedEffect(auth.isLoggedIn) {
        val current = nav.currentDestination?.route
        if (auth.isLoggedIn && current in Routes.AUTH) {
            nav.navigate(Routes.POKEDEX) { popUpTo(nav.graph.id) { inclusive = true } }
        } else if (!auth.isLoggedIn && current != null && current !in Routes.AUTH) {
            nav.navigate(Routes.LOGIN) { popUpTo(nav.graph.id) { inclusive = true } }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (route in Routes.TABS) BottomBar(route, nav)
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    state = auth,
                    onLogin = authVm::login,
                    onGoRegister = { authVm.clearFeedback(); nav.navigate(Routes.REGISTER) },
                    onGoForgot = { authVm.clearFeedback(); nav.navigate(Routes.FORGOT) }
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    state = auth,
                    onRegister = authVm::register,
                    onBack = { authVm.clearFeedback(); nav.popBackStack() }
                )
            }
            composable(Routes.FORGOT) {
                ForgotPasswordScreen(
                    state = auth,
                    onSend = authVm::resetPassword,
                    onBack = { authVm.clearFeedback(); nav.popBackStack() }
                )
            }
            composable(Routes.POKEDEX) {
                val vm: PokedexViewModel = viewModel(factory = factoryOf { PokedexViewModel(container.pokemonRepository) })
                PokedexScreen(
                    vm = vm,
                    onOpenDetail = { nav.navigate(Routes.detail(it)) },
                    onLogout = authVm::logout
                )
            }
            composable(Routes.FAVORITES) {
                val vm: FavoritesViewModel = viewModel(factory = factoryOf { FavoritesViewModel(container.pokemonRepository) })
                FavoritesScreen(vm = vm, onOpenDetail = { nav.navigate(Routes.detail(it)) })
            }
            composable(Routes.TEAM) {
                val vm: TeamViewModel = viewModel(factory = factoryOf { TeamViewModel(container.pokemonRepository) })
                TeamScreen(vm = vm, onOpenDetail = { nav.navigate(Routes.detail(it)) })
            }
            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                val vm: DetailViewModel = viewModel(factory = factoryOf { DetailViewModel(id, container.pokemonRepository) })
                DetailScreen(vm = vm, onBack = { nav.popBackStack() })
            }
        }
    }
}

@Composable
private fun BottomBar(currentRoute: String?, nav: NavController) {
    NavigationBar {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = {
                    nav.navigate(tab.route) {
                        popUpTo(Routes.POKEDEX) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) }
            )
        }
    }
}
