package app.kurozora.tracker

import ResponsiveText
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.kurozora.tracker.ui.BottomNavigationScreens
import app.kurozora.tracker.ui.home.ExploreView
import app.kurozora.tracker.ui.theme.KurozoraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KurozoraTheme {


                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                val items = listOf(
                    BottomNavigationScreens.Explore,
                    BottomNavigationScreens.Library,
                    BottomNavigationScreens.Feed,
                    BottomNavigationScreens.Notifications,
                    BottomNavigationScreens.Search
                )
                Scaffold(bottomBar = {
                    BottomNavigation(
                    ) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        items.forEach { screen ->
                            BottomNavigationItem(icon = {
                                Icon(
                                    imageVector = screen.Icon,
                                    contentDescription = ""
                                )
                            },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                label = {
                                    ResponsiveText(
                                        text = stringResource(id = screen.resourceId),
                                        color = Color.Black
                                    )
                                },
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                })
                        }
                    }
                }, content = { paddin ->
                    KurozoraTheme() {
                        NavHost(
                            navController = navController,
                            startDestination = BottomNavigationScreens.Explore.route,
                            modifier = Modifier.padding(paddin)
                        ) {
                            composable(BottomNavigationScreens.Explore.route) { ExploreView() }

                        }
                    }
                })
            }
        }
    }
}