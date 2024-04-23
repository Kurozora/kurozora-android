package app.kurozora.tracker.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import app.kurozora.tracker.R

sealed class NavigationBarScreens(val route: String, @StringRes val resourceId: Int, @DrawableRes val image: Int, @DrawableRes val selectedImage: Int) {
    object Explore: NavigationBarScreens("Home", R.string.navigation_explore , R.drawable.ic_outline_home_24, R.drawable.ic_baseline_home_24)
    object Library: NavigationBarScreens("Library", R.string.navigation_library , R.drawable.ic_outline_rectangle_stack, R.drawable.ic_baseline_rectangle_stack)
    object Feed: NavigationBarScreens("Feed", R.string.navigation_feed , R.drawable.ic_outline_person_pin_24, R.drawable.ic_baseline_person_pin_24)
    object Notifications: NavigationBarScreens("Notifications", R.string.navigation_notifications , R.drawable.ic_outline_notifications_24, R.drawable.ic_baseline_notifications_24)
    object Search: NavigationBarScreens("Search", R.string.navigation_search , R.drawable.ic_outline_search_24, R.drawable.ic_baseline_search_24)
}
