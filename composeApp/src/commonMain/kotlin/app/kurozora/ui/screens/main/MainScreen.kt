package app.kurozora.ui.screens.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowWidthSizeClass
import app.kurozora.core.settings.AccountManager
import app.kurozora.ui.navigation.AppNavHost
import app.kurozora.ui.navigation.Screen
import app.kurozora.ui.screens.welcome.WelcomeScreen
import app.kurozora.ui.theme.KurozoraTheme
import app.kurozora.ui.theme.ThemeController
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel(),
) {
//    val accountManager: AccountManager = koinInject()
//    val settings = accountManager.getScopedSettings()
//    settings?.let { ThemeController.initFromSettings(it) }
//    val seenOnboarding by viewModel.seenOnboarding.collectAsState()
//
//    if (!seenOnboarding) {
//        // Onboarding ekranı
//        WelcomeScreen(
//            onGetStarted = { viewModel.markOnboardingSeen() }
//        )
//    } else {
//        // Normal ana ekran
//        MainContent()
//    }
    MainContent()
}

@Composable
fun MainContent() {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass

    KurozoraTheme {
        val navHostController = rememberNavController()
        val topLevelDestinations = listOf(
            Screen.Explore,
            Screen.Library,
            Screen.Search,
            Screen.Feed,
            Screen.Profile
        )
        val navBackStackEntry by navHostController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        fun isTopLevel(route: String?): Boolean {
            if (route == null) return false
            val baseRoute = route.substringBefore("?") // parametreleri yok say
            return topLevelDestinations.any { it.route == baseRoute }
        }

        val navigationLayout = if (!isTopLevel(currentRoute)) {
            NavigationSuiteType.None
        } else if (windowSize.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
            NavigationSuiteType.NavigationBar
        } else {
            NavigationSuiteType.NavigationRail
        }

        NavigationSuiteScaffold(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            navigationSuiteItems = {
                topLevelDestinations.iterator().forEach { screen ->
                    val isSelected = currentRoute?.substringBefore("?") == screen.route
                    item(
                        icon = {
                            val iconImage = if (isSelected) screen.selectedIcon else screen.unselectedIcon
                            iconImage?.let { Icon(it, contentDescription = screen.title) }
                        },
                        label = { Text(text = screen.title) },
                        alwaysShowLabel = isSelected,
                        selected = isSelected,
                        onClick = {
                            navHostController.navigate(screen.route) {
                                popUpTo(Screen.Explore.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = .85f),
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            layoutType = navigationLayout
        ) {
            AppNavHost(navController = navHostController, windowSize = windowSize)
        }
    }
}
