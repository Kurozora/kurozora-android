package app.kurozora.tracker.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilePresent
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.ui.graphics.vector.ImageVector
import app.kurozora.tracker.R

sealed class BottomNavigationScreens(val route: String, @StringRes val resourceId: Int, val Icon : ImageVector) {
        object Explore : BottomNavigationScreens("Home", R.string.navigation_explore , Icons.Outlined.Home)
        object Library : BottomNavigationScreens("Library", R.string.navigation_library , Icons.Outlined.FilePresent)
        object Feed : BottomNavigationScreens("Feed", R.string.navigation_feed , Icons.Outlined.VerifiedUser)
        object Notifications : BottomNavigationScreens("Notifications", R.string.navigation_notifications , Icons.Outlined.Notifications)
        object Search : BottomNavigationScreens("Search", R.string.navigation_search , Icons.Outlined.Search)

}