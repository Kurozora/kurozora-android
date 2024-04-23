package app.kurozora.tracker

import ResponsiveText
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.kurozora.tracker.ui.NavigationBarScreens
import app.kurozora.tracker.ui.feed.FeedView
import app.kurozora.tracker.ui.home.ExploreView
import app.kurozora.tracker.ui.library.LibraryView
import app.kurozora.tracker.ui.notifications.NotificationsView
import app.kurozora.tracker.ui.search.SearchView
import app.kurozora.tracker.ui.theme.Global
import app.kurozora.tracker.ui.theme.KurozoraTheme

class MainActivity: ComponentActivity() {
    // initializing the default selected item
    private var navigationSelectedItem: Int = 0

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KurozoraTheme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                val items = listOf(
                    NavigationBarScreens.Explore,
                    NavigationBarScreens.Library,
                    NavigationBarScreens.Feed,
                    NavigationBarScreens.Notifications,
                    NavigationBarScreens.Search
                )

                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                titleContentColor = Global.shared.barTitleTextColor
                            ),
                            title = {
                                Text(text = stringResource(id = items[navigationSelectedItem].resourceId))
                            }
                        )
                    },
                    bottomBar = {
                    NavigationBar {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination

                        items.forEachIndexed { index, screen ->
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (navigationSelectedItem == index) ImageVector.vectorResource(screen.selectedImage) else ImageVector.vectorResource(screen.image),
                                        contentDescription = null,
                                        tint = if (navigationSelectedItem == index) Global.shared.textColor else Global.shared.subTextColor
                                    )
                                },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                label = {
                                    ResponsiveText(
                                        text = stringResource(id = screen.resourceId),
                                        color = if (navigationSelectedItem == index) Global.shared.textColor else Global.shared.subTextColor
                                    )
                                },
                                onClick = {
                                    navigationSelectedItem = index

                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }

                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }, content = { padding ->
                    KurozoraTheme {
                        NavHost(
                            navController = navController,
                            startDestination = NavigationBarScreens.Explore.route,
                            modifier = Modifier.padding(padding)
                        ) {
                            composable(NavigationBarScreens.Explore.route) { ExploreView() }
                            composable(NavigationBarScreens.Library.route) { LibraryView() }
                            composable(NavigationBarScreens.Feed.route) { FeedView() }
                            composable(NavigationBarScreens.Notifications.route) { NotificationsView() }
                            composable(NavigationBarScreens.Search.route) { SearchView() }
                        }
                    }
                })
            }
        }
    }
}
