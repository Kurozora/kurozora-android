package app.kurozora.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.window.core.layout.WindowSizeClass
import app.kurozora.core.settings.AccountManager
import app.kurozora.ui.screens.airseason.AirSeasonScreen
import app.kurozora.ui.screens.auth.AuthScreen
import app.kurozora.ui.screens.detail.CharacterDetailScreen
import app.kurozora.ui.screens.detail.EpisodeDetailScreen
import app.kurozora.ui.screens.detail.GameDetailScreen
import app.kurozora.ui.screens.detail.LiteratureDetailScreen
import app.kurozora.ui.screens.detail.PersonDetailScreen
import app.kurozora.ui.screens.detail.ShowDetailScreen
import app.kurozora.ui.screens.detail.SongDetailScreen
import app.kurozora.ui.screens.detail.StudioDetailScreen
import app.kurozora.ui.screens.detail.season.SeasonDetailScreen
import app.kurozora.ui.screens.explore.ExploreScreen
import app.kurozora.ui.screens.favorite.FavoriteScreen
import app.kurozora.ui.screens.feed.FeedScreen
import app.kurozora.ui.screens.library.LibraryScreen
import app.kurozora.ui.screens.list.ExploreCategoryScreen
import app.kurozora.ui.screens.list.character.*
import app.kurozora.ui.screens.list.game.*
import app.kurozora.ui.screens.list.literature.*
import app.kurozora.ui.screens.list.person.*
import app.kurozora.ui.screens.list.profile.*
import app.kurozora.ui.screens.list.show.*
import app.kurozora.ui.screens.list.song.SongAnimeListScreen
import app.kurozora.ui.screens.list.song.SongGameListScreen
import app.kurozora.ui.screens.list.studio.*
import app.kurozora.ui.screens.profile.ProfileScreen
import app.kurozora.ui.screens.profile.settings.SettingsScreen
import app.kurozora.ui.screens.reminder.ReminderScreen
import app.kurozora.ui.screens.schedule.ScheduleScreen
import app.kurozora.ui.screens.search.SearchScreen
import io.ktor.util.decodeBase64String
import kotlinx.serialization.json.Json
import kurozorakit.data.models.character.Character
import kurozorakit.data.models.episode.Episode
import kurozorakit.data.models.explore.ExploreCategory
import kurozorakit.data.models.game.Game
import kurozorakit.data.models.literature.Literature
import kurozorakit.data.models.person.Person
import kurozorakit.data.models.season.Season
import kurozorakit.data.models.show.Show
import kurozorakit.data.models.song.Song
import kurozorakit.data.models.studio.Studio
import kurozorakit.data.models.user.User
import org.koin.core.context.GlobalContext

fun NavController.navigateToItemDetail(item: Any) {
    when (item) {
        is Show -> navigate(Screen.AnimeDetail.createRoute(item))
        is Literature -> navigate(Screen.MangaDetail.createRoute(item))
        is Game -> navigate(Screen.GameDetail.createRoute(item))
        is Character -> navigate(Screen.CharacterDetail.createRoute(item))
        is Episode -> navigate(Screen.EpisodeDetail.createRoute(item))
        is Person -> navigate(Screen.PersonDetail.createRoute(item))
        is Studio -> navigate(Screen.StudioDetail.createRoute(item))
        is Song -> navigate(Screen.SongDetail.createRoute(item))
        is Season -> navigate(Screen.SeasonDetail.createRoute(item))
        is User -> navigate(Screen.Profile.createRoute(item))
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    windowSize: WindowSizeClass,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Explore.route,
        modifier = modifier
    ) {
        val accountManager = GlobalContext.get().get<AccountManager>()
        val isLoggedIn = accountManager.activeAccount.value != null
        // Explore Screen
        composable(Screen.Explore.route) {
            //PlaceholderScreen(screenName = "Explore")
            ExploreScreen(
                isLoggedIn = isLoggedIn,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
                onNavigateToCategoryDetails = { category ->
                    navController.navigate(Screen.ExploreCategoryDetail.createRoute(category))
                },
                onNavigateToSchedule = {
                    navController.navigate(Screen.Schedule.route)
                },
            )
        }
        // Library Screen
        composable(Screen.Library.route) {
            LibraryScreen(
                windowWidth = windowSize.windowWidthSizeClass,
                isLoggedIn = isLoggedIn,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
                onNavigateToFavoriteScreen = {
                    navController.navigate(Screen.Favorite.route)
                },
                onNavigateToReminderScreen = {
                    navController.navigate(Screen.Reminder.route)
                }
            )
            //PlaceholderScreen(screenName = "Library")
        }

        composable(Screen.Favorite.route) {
            FavoriteScreen(
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(Screen.Reminder.route) {
            ReminderScreen(
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(Screen.Search.route) {
            //PlaceholderScreen(screenName = "Search")
            SearchScreen(
                windowWidth = windowSize.windowWidthSizeClass,
                isLoggedIn = isLoggedIn,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
                onNavigateToAirSeason = {
                    navController.navigate(Screen.AirSeason.route)
                }
            )
        }
        composable(Screen.AirSeason.route) {
            AirSeasonScreen(
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(Screen.Feed.route) {
            val activeAccount = accountManager.activeAccount.collectAsState().value
            val user = activeAccount?.userJson?.let { string -> Json.decodeFromString<User>(string) }
            FeedScreen(
                isLoggedIn = isLoggedIn,
                currentUser = user,
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
//            PlaceholderScreen(screenName = "Feed")
        }

        composable(Screen.Schedule.route) {
            ScheduleScreen(
                windowWidth = windowSize.windowWidthSizeClass,
                isLoggedIn = isLoggedIn,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(
            route = "${Screen.Profile.route}?userJson={userJson}",
            arguments = listOf(
                navArgument("userJson") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val activeAccount = accountManager.activeAccount.collectAsState().value
            if (activeAccount == null) {
                AuthScreen(
                    windowWidth = windowSize.windowWidthSizeClass,
                    onNavigateBack = { navController.popBackStack() },
                    onAuthSuccess = {
                        navController.navigate(Screen.Explore.route)
                    },
                )
            } else {
                val encoded = NavType.StringType.get(backStackEntry.arguments!!, "userJson")
                val userJson = encoded?.decodeBase64String() ?: activeAccount.userJson
                val userItem = Json.decodeFromString<User>(userJson)
                ProfileScreen(
                    user = userItem,
                    isCurrentUser = userItem.id == activeAccount.id,
                    windowWidth = windowSize.windowWidthSizeClass,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToItemDetail = { item ->
                        navController.navigateToItemDetail(item)
                    },
                    onNavigateToAnimeLibraryList = { id ->
                        navController.navigate(Screen.ProfileAnimeLibraryList.createRoute(id))
                    },
                    onNavigateToMangaLibraryList = { id ->
                        navController.navigate(Screen.ProfileMangaLibraryList.createRoute(id))
                    },
                    onNavigateToGameLibraryList = { id ->
                        navController.navigate(Screen.ProfileGameLibraryList.createRoute(id))
                    },
                    onNavigateToFavoriteAnimeList = { id ->
                        navController.navigate(Screen.ProfileFavoriteAnimeList.createRoute(id))
                    },
                    onNavigateToFavoriteMangaList = { id ->
                        navController.navigate(Screen.ProfileFavoriteMangaList.createRoute(id))
                    },
                    onNavigateToFavoriteGameList = { id ->
                        navController.navigate(Screen.ProfileFavoriteGameList.createRoute(id))
                    },
                    onNavigateToFollowersList = { id ->
                        navController.navigate(Screen.ProfileFollowersList.createRoute(id))
                    },
                    onNavigateToFollowingsList = { id ->
                        navController.navigate(Screen.ProfileFollowingsList.createRoute(id))
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    }
                )
            }
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLoginScreen = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(Screen.Notification.route) {
            //NotificationScreen()
            PlaceholderScreen(screenName = "Notifications")
        }

        composable(Screen.Login.route) {
            AuthScreen(
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateBack = { navController.popBackStack() },
                onAuthSuccess = {
                    navController.navigate(Screen.Explore.route)
                }
            )
        }
//        composable("upcoming-shows") {
//            UpcomingShowsScreen(
//                onNavigateBack = { navController.popBackStack() },
//            )
//        }
        // Show Detail
        composable(
            route = "${Screen.AnimeDetail.route}?showJson={showJson}",
            arguments = listOf(
                navArgument("showJson") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val encoded = NavType.StringType.get(backStackEntry.arguments!!, "showJson")
            val showJson = encoded?.decodeBase64String()

            if (showJson != null) {
                val showItem = Json.decodeFromString<Show>(showJson)
                ShowDetailScreen(
                    show = showItem,
                    windowWidth = windowSize.windowWidthSizeClass,
                    isLoggedIn = isLoggedIn,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToItemDetail = { item ->
                        navController.navigateToItemDetail(item)
                    },
                    onNavigateToSeasonList = { id ->
                        navController.navigate(Screen.ShowSeasonList.createRoute(id))
                    },
                    onNavigateToCastList = { id ->
                        navController.navigate(Screen.ShowCastList.createRoute(id))
                    },
                    onNavigateToStaffList = { id ->
                        navController.navigate(Screen.ShowStaffList.createRoute(id))
                    },
                    onNavigateToSongList = { id ->
                        navController.navigate(Screen.ShowSongList.createRoute(id))
                    },
                    onNavigateToStudioList = { id ->
                        navController.navigate(Screen.ShowStudioList.createRoute(id))
                    },
                    onNavigateToMoreByStudioList = { id ->
                        navController.navigate(Screen.ShowMoreByStudioList.createRoute(id))
                    },
                    onNavigateToRelatedShowList = { id ->
                        navController.navigate(Screen.ShowRelatedShowList.createRoute(id))
                    },
                    onNavigateToRelatedLiteratureList = { id ->
                        navController.navigate(Screen.ShowRelatedLiteratureList.createRoute(id))
                    },
                    onNavigateToRelatedGameList = { id ->
                        navController.navigate(Screen.ShowRelatedGameList.createRoute(id))
                    },
                )
            }
        }
        // Literature Detail
        composable(
            route = "${Screen.MangaDetail.route}?litJson={litJson}",
            arguments = listOf(
                navArgument("litJson") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val encoded = NavType.StringType.get(backStackEntry.arguments!!, "litJson")
            val litJson = encoded?.decodeBase64String()

            if (litJson != null) {
                val litItem = Json.decodeFromString<Literature>(litJson)
                LiteratureDetailScreen(
                    literature = litItem,
                    windowWidth = windowSize.windowWidthSizeClass,
                    isLoggedIn = isLoggedIn,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToItemDetail = { item ->
                        navController.navigateToItemDetail(item)
                    },
                    onNavigateToPeopleList = { id ->
                        navController.navigate(Screen.LiteraturePeopleList.createRoute(id))
                    },
                    onNavigateToCastList = { id ->
                        navController.navigate(Screen.LiteratureCastList.createRoute(id))
                    },
                    onNavigateToStaffList = { id ->
                        navController.navigate(Screen.LiteratureStaffList.createRoute(id))
                    },
                    onNavigateToCharacterList = { id ->
                        navController.navigate(Screen.LiteratureCharacterList.createRoute(id))
                    },
                    onNavigateToStudioList = { id ->
                        navController.navigate(Screen.LiteratureStudioList.createRoute(id))
                    },
                    onNavigateToMoreByStudioList = { id ->
                        navController.navigate(Screen.LiteratureMoreByStudioList.createRoute(id))
                    },
                    onNavigateToRelatedShowList = { id ->
                        navController.navigate(Screen.LiteratureRelatedShowList.createRoute(id))
                    },
                    onNavigateToRelatedLiteratureList = { id ->
                        navController.navigate(Screen.LiteratureRelatedLiteratureList.createRoute(id))
                    },
                    onNavigateToRelatedGameList = { id ->
                        navController.navigate(Screen.LiteratureRelatedGameList.createRoute(id))
                    },
                )
            }
        }
        // Game Detail
        composable(
            route = "${Screen.GameDetail.route}?gameJson={gameJson}",
            arguments = listOf(
                navArgument("gameJson") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val encoded = NavType.StringType.get(backStackEntry.arguments!!, "gameJson")
            val gameJson = encoded?.decodeBase64String()

            if (gameJson != null) {
                val gameItem = Json.decodeFromString<Game>(gameJson)
                GameDetailScreen(
                    game = gameItem,
                    windowWidth = windowSize.windowWidthSizeClass,
                    isLoggedIn = isLoggedIn,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToItemDetail = { item ->
                        navController.navigateToItemDetail(item)
                    },
                    onNavigateToPeopleList = { id ->
                        navController.navigate(Screen.GamePeopleList.createRoute(id))
                    },
                    onNavigateToCastList = { id ->
                        navController.navigate(Screen.GameCastList.createRoute(id))
                    },
                    onNavigateToStaffList = { id ->
                        navController.navigate(Screen.GameStaffList.createRoute(id))
                    },
                    onNavigateToCharacterList = { id ->
                        navController.navigate(Screen.GameCharacterList.createRoute(id))
                    },
                    onNavigateToStudioList = { id ->
                        navController.navigate(Screen.GameStudioList.createRoute(id))
                    },
                    onNavigateToMoreByStudioList = { id ->
                        navController.navigate(Screen.GameMoreByStudioList.createRoute(id))
                    },
                    onNavigateToRelatedShowList = { id ->
                        navController.navigate(Screen.GameRelatedShowList.createRoute(id))
                    },
                    onNavigateToRelatedLiteratureList = { id ->
                        navController.navigate(Screen.GameRelatedLiteratureList.createRoute(id))
                    },
                    onNavigateToRelatedGameList = { id ->
                        navController.navigate(Screen.GameRelatedGameList.createRoute(id))
                    },
                )
            }
        }
        // Character Detail
        composable(
            route = "${Screen.CharacterDetail.route}?characterJson={characterJson}",
            arguments = listOf(
                navArgument("characterJson") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val encoded = NavType.StringType.get(backStackEntry.arguments!!, "characterJson")
            val characterJson = encoded?.decodeBase64String()

            if (characterJson != null) {
                val characterItem = Json.decodeFromString<Character>(characterJson)
                CharacterDetailScreen(
                    character = characterItem,
                    windowWidth = windowSize.windowWidthSizeClass,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToItemDetail = { item ->
                        navController.navigateToItemDetail(item)
                    },
                    onNavigateToAnimeList = { id ->
                        navController.navigate(Screen.CharacterAnimeList.createRoute(id))
                    },
                    onNavigateToMangaList = { id ->
                        navController.navigate(Screen.CharacterMangaList.createRoute(id))
                    },
                    onNavigateToGameList = { id ->
                        navController.navigate(Screen.CharacterGameList.createRoute(id))
                    },
                    onNavigateToPeopleList = { id ->
                        navController.navigate(Screen.CharacterPeopleList.createRoute(id))
                    },
                )
            }
        }
        // Episode Detail
        composable(
            route = "${Screen.EpisodeDetail.route}?episodeJson={episodeJson}",
            arguments = listOf(
                navArgument("episodeJson") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val encoded = NavType.StringType.get(backStackEntry.arguments!!, "episodeJson")
            val episodeJson = encoded?.decodeBase64String()

            if (episodeJson != null) {
                val episodeItem = Json.decodeFromString<Episode>(episodeJson)
                EpisodeDetailScreen(
                    episode = episodeItem,
                    windowWidth = windowSize.windowWidthSizeClass,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToItemDetail = { item ->
                        navController.navigateToItemDetail(item)
                    },
                )
            }
        }
        // Person Detail
        composable(
            route = "${Screen.PersonDetail.route}?personJson={personJson}",
            arguments = listOf(
                navArgument("personJson") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val encoded = NavType.StringType.get(backStackEntry.arguments!!, "personJson")
            val personJson = encoded?.decodeBase64String()

            if (personJson != null) {
                val personItem = Json.decodeFromString<Person>(personJson)
                PersonDetailScreen(
                    person = personItem,
                    windowWidth = windowSize.windowWidthSizeClass,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToItemDetail = { item ->
                        navController.navigateToItemDetail(item)
                    },
                    onNavigateToAnimeList = { id ->
                        navController.navigate(Screen.PersonAnimeList.createRoute(id))
                    },
                    onNavigateToMangaList = { id ->
                        navController.navigate(Screen.PersonMangaList.createRoute(id))
                    },
                    onNavigateToGameList = { id ->
                        navController.navigate(Screen.PersonGameList.createRoute(id))
                    },
                    onNavigateToCharacterList = { id ->
                        navController.navigate(Screen.PersonCharacterList.createRoute(id))
                    },
                )
            }
        }
        // Studio Detail
        composable(
            route = "${Screen.StudioDetail.route}?studioJson={studioJson}",
            arguments = listOf(
                navArgument("studioJson") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val encoded = NavType.StringType.get(backStackEntry.arguments!!, "studioJson")
            val studioJson = encoded?.decodeBase64String()

            if (studioJson != null) {
                val studioItem = Json.decodeFromString<Studio>(studioJson)
                StudioDetailScreen(
                    studio = studioItem,
                    windowWidth = windowSize.windowWidthSizeClass,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToItemDetail = { item ->
                        navController.navigateToItemDetail(item)
                    },
                    onNavigateToAnimeList = { id ->
                        navController.navigate(Screen.StudioAnimeList.createRoute(id))
                    },
                    onNavigateToMangaList = { id ->
                        navController.navigate(Screen.StudioMangaList.createRoute(id))
                    },
                    onNavigateToGameList = { id ->
                        navController.navigate(Screen.StudioGameList.createRoute(id))
                    },
                )
            }
        }
        // Song Detail
        composable(
            route = "${Screen.SongDetail.route}?songJson={songJson}",
            arguments = listOf(
                navArgument("songJson") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val encoded = NavType.StringType.get(backStackEntry.arguments!!, "songJson")
            val songJson = encoded?.decodeBase64String()

            if (songJson != null) {
                val songItem = Json.decodeFromString<Song>(songJson)
                SongDetailScreen(
                    song = songItem,
                    windowWidth = windowSize.windowWidthSizeClass,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToItemDetail = { item ->
                        navController.navigateToItemDetail(item)
                    },
                    onNavigateToAnimeList = { id ->
                        navController.navigate(Screen.SongAnimeList.createRoute(id))
                    },
                    onNavigateToGameList = { id ->
                        navController.navigate(Screen.SongGameList.createRoute(id))
                    },
                )
            }
        }
        // Season Detail
        composable(
            route = "${Screen.SeasonDetail.route}?seasonJson={seasonJson}",
            arguments = listOf(
                navArgument("seasonJson") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val encoded = NavType.StringType.get(backStackEntry.arguments!!, "seasonJson")
            val seasonJson = encoded?.decodeBase64String()

            if (seasonJson != null) {
                val seasonItem = Json.decodeFromString<Season>(seasonJson)
                SeasonDetailScreen(
                    season = seasonItem,
                    windowWidth = windowSize.windowWidthSizeClass,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEpisodeDetails = { episode ->
                        navController.navigate(Screen.EpisodeDetail.createRoute(episode))
                    },
                )
            }
        }
        // Explore Category
        composable(
            route = "${Screen.ExploreCategoryDetail.route}?exploreCategoryJson={exploreCategoryJson}",
            arguments = listOf(
                navArgument("exploreCategoryJson") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val encoded = NavType.StringType.get(backStackEntry.arguments!!, "exploreCategoryJson")
            val exploreCategoryJson = encoded?.decodeBase64String()

            if (exploreCategoryJson != null) {
                val categoryItem = Json.decodeFromString<ExploreCategory>(exploreCategoryJson)
                ExploreCategoryScreen(
                    category = categoryItem,
                    onNavigateBack = { navController.popBackStack() },
                    windowWidth = windowSize.windowWidthSizeClass,
                    onNavigateToItemDetail = { item ->
                        navController.navigateToItemDetail(item)
                    },
                )
            }
        }
        // ShowSeasonList
        composable(
            route = "${Screen.ShowSeasonList.route}?showId={showId}",
            arguments = listOf(
                navArgument("showId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val showId = NavType.StringType.get(backStackEntry.arguments!!, "showId")
            ShowSeasonListScreen(
                showId = showId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // ShowCastList
        composable(
            route = "${Screen.ShowCastList.route}?showId={showId}",
            arguments = listOf(
                navArgument("showId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val showId = NavType.StringType.get(backStackEntry.arguments!!, "showId")
            ShowCastListScreen(
                showId = showId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // ShowStaffList
        composable(
            route = "${Screen.ShowStaffList.route}?showId={showId}",
            arguments = listOf(
                navArgument("showId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val showId = NavType.StringType.get(backStackEntry.arguments!!, "showId")
            ShowStaffListScreen(
                showId = showId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // ShowSongList
        composable(
            route = "${Screen.ShowSongList.route}?showId={showId}",
            arguments = listOf(
                navArgument("showId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val showId = NavType.StringType.get(backStackEntry.arguments!!, "showId")
            ShowSongListScreen(
                showId = showId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // ShowStudioList
        composable(
            route = "${Screen.ShowStudioList.route}?showId={showId}",
            arguments = listOf(
                navArgument("showId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val showId = NavType.StringType.get(backStackEntry.arguments!!, "showId")
            ShowStudioListScreen(
                showId = showId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // ShowMoreByStudioList
        composable(
            route = "${Screen.ShowMoreByStudioList.route}?showId={showId}",
            arguments = listOf(
                navArgument("showId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val showId = NavType.StringType.get(backStackEntry.arguments!!, "showId")
            ShowMoreByStudioListScreen(
                showId = showId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // ShowRelatedShowList
        composable(
            route = "${Screen.ShowRelatedShowList.route}?showId={showId}",
            arguments = listOf(
                navArgument("showId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val showId = NavType.StringType.get(backStackEntry.arguments!!, "showId")
            ShowRelatedShowListScreen(
                showId = showId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // ShowRelatedLiteratureList
        composable(
            route = "${Screen.ShowRelatedLiteratureList.route}?showId={showId}",
            arguments = listOf(
                navArgument("showId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val showId = NavType.StringType.get(backStackEntry.arguments!!, "showId")
            ShowRelatedLiteratureListScreen(
                showId = showId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // ShowRelatedGameList
        composable(
            route = "${Screen.ShowRelatedGameList.route}?showId={showId}",
            arguments = listOf(
                navArgument("showId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val showId = NavType.StringType.get(backStackEntry.arguments!!, "showId")
            ShowRelatedGameListScreen(
                showId = showId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // LiteraturePeopleList
        composable(
            route = "${Screen.LiteraturePeopleList.route}?litId={litId}",
            arguments = listOf(
                navArgument("litId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val litId = NavType.StringType.get(backStackEntry.arguments!!, "litId")
            LiteraturePeopleListScreen(
                litId = litId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // LiteratureCastList
        composable(
            route = "${Screen.LiteratureCastList.route}?litId={litId}",
            arguments = listOf(
                navArgument("litId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val litId = NavType.StringType.get(backStackEntry.arguments!!, "litId")
            LiteratureCastListScreen(
                litId = litId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // LiteratureStaffList
        composable(
            route = "${Screen.LiteratureStaffList.route}?litId={litId}",
            arguments = listOf(
                navArgument("litId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val litId = NavType.StringType.get(backStackEntry.arguments!!, "litId")
            LiteratureStaffListScreen(
                litId = litId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // LiteratureCharacterList
        composable(
            route = "${Screen.LiteratureCharacterList.route}?litId={litId}",
            arguments = listOf(
                navArgument("litId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val litId = NavType.StringType.get(backStackEntry.arguments!!, "litId")
            LiteratureCharacterListScreen(
                litId = litId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // LiteratureStudioList
        composable(
            route = "${Screen.LiteratureStudioList.route}?litId={litId}",
            arguments = listOf(
                navArgument("litId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val litId = NavType.StringType.get(backStackEntry.arguments!!, "litId")
            LiteratureStudioListScreen(
                litId = litId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // LiteratureMoreByStudioList
        composable(
            route = "${Screen.LiteratureMoreByStudioList.route}?litId={litId}",
            arguments = listOf(
                navArgument("litId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val litId = NavType.StringType.get(backStackEntry.arguments!!, "litId")
            LiteratureMoreByStudioListScreen(
                litId = litId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // LiteratureRelatedShowList
        composable(
            route = "${Screen.LiteratureRelatedShowList.route}?litId={litId}",
            arguments = listOf(
                navArgument("litId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val litId = NavType.StringType.get(backStackEntry.arguments!!, "litId")
            LiteratureRelatedShowListScreen(
                litId = litId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // LiteratureRelatedLiteratureList
        composable(
            route = "${Screen.LiteratureRelatedLiteratureList.route}?litId={litId}",
            arguments = listOf(
                navArgument("litId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val litId = NavType.StringType.get(backStackEntry.arguments!!, "litId")
            LiteratureRelatedLiteratureListScreen(
                litId = litId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // LiteratureRelatedGameList
        composable(
            route = "${Screen.LiteratureRelatedGameList.route}?litId={litId}",
            arguments = listOf(
                navArgument("litId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val litId = NavType.StringType.get(backStackEntry.arguments!!, "litId")
            LiteratureRelatedGameListScreen(
                litId = litId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // GamePeopleList
        composable(
            route = "${Screen.GamePeopleList.route}?gameId={gameId}",
            arguments = listOf(
                navArgument("gameId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val gameId = NavType.StringType.get(backStackEntry.arguments!!, "gameId")
            GamePeopleListScreen(
                gameId = gameId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // GameCastList
        composable(
            route = "${Screen.GameCastList.route}?gameId={gameId}",
            arguments = listOf(
                navArgument("gameId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val gameId = NavType.StringType.get(backStackEntry.arguments!!, "gameId")
            GameCastListScreen(
                gameId = gameId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
// GameStaffList
        composable(
            route = "${Screen.GameStaffList.route}?gameId={gameId}",
            arguments = listOf(
                navArgument("gameId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val gameId = NavType.StringType.get(backStackEntry.arguments!!, "gameId")
            GameStaffListScreen(
                gameId = gameId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
// GameCharacterList
        composable(
            route = "${Screen.GameCharacterList.route}?gameId={gameId}",
            arguments = listOf(
                navArgument("gameId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val gameId = NavType.StringType.get(backStackEntry.arguments!!, "gameId")
            GameCharacterListScreen(
                gameId = gameId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
// GameStudioList
        composable(
            route = "${Screen.GameStudioList.route}?gameId={gameId}",
            arguments = listOf(
                navArgument("gameId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val gameId = NavType.StringType.get(backStackEntry.arguments!!, "gameId")
            GameStudioListScreen(
                gameId = gameId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
// GameMoreByStudioList
        composable(
            route = "${Screen.GameMoreByStudioList.route}?gameId={gameId}",
            arguments = listOf(
                navArgument("gameId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val gameId = NavType.StringType.get(backStackEntry.arguments!!, "gameId")
            GameMoreByStudioListScreen(
                gameId = gameId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
// GameRelatedShowList
        composable(
            route = "${Screen.GameRelatedShowList.route}?gameId={gameId}",
            arguments = listOf(
                navArgument("gameId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val gameId = NavType.StringType.get(backStackEntry.arguments!!, "gameId")
            GameRelatedShowListScreen(
                gameId = gameId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // GameRelatedLiteratureList
        composable(
            route = "${Screen.GameRelatedLiteratureList.route}?gameId={gameId}",
            arguments = listOf(
                navArgument("gameId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val gameId = NavType.StringType.get(backStackEntry.arguments!!, "gameId")
            GameRelatedLiteratureListScreen(
                gameId = gameId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // GameRelatedGameList
        composable(
            route = "${Screen.GameRelatedGameList.route}?gameId={gameId}",
            arguments = listOf(
                navArgument("gameId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val gameId = NavType.StringType.get(backStackEntry.arguments!!, "gameId")
            GameRelatedGameListScreen(
                gameId = gameId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // ProfileAnimeLibraryList
        composable(
            route = "${Screen.ProfileAnimeLibraryList.route}?userId={userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val userId = NavType.StringType.get(backStackEntry.arguments!!, "userId")
            ProfileAnimeLibraryListScreen(
                userId = userId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // ProfileMangaLibraryList
        composable(
            route = "${Screen.ProfileMangaLibraryList.route}?userId={userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val userId = NavType.StringType.get(backStackEntry.arguments!!, "userId")
            ProfileMangaLibraryListScreen(
                userId = userId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // ProfileGameLibraryList
        composable(
            "${Screen.ProfileGameLibraryList.route}?userId={userId}", listOf(
                navArgument("userId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val userId = NavType.StringType.get(backStackEntry.arguments!!, "userId")
            ProfileGameLibraryListScreen(
                userId = userId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // ProfileFavoriteAnimeList
        composable(
            route = "${Screen.ProfileFavoriteAnimeList.route}?userId={userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val userId = NavType.StringType.get(backStackEntry.arguments!!, "userId")
            ProfileFavoriteAnimeListScreen(
                userId = userId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // ProfileFavoriteMangaList
        composable(
            route = "${Screen.ProfileFavoriteMangaList.route}?userId={userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val userId = NavType.StringType.get(backStackEntry.arguments!!, "userId")
            ProfileFavoriteMangaListScreen(
                userId = userId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // ProfileFavoriteGameList
        composable(
            route = "${Screen.ProfileFavoriteGameList.route}?userId={userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val userId = NavType.StringType.get(backStackEntry.arguments!!, "userId")
            ProfileFavoriteGameListScreen(
                userId = userId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // ProfileFollowersList
        composable(
            route = "${Screen.ProfileFollowersList.route}?userId={userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val userId = NavType.StringType.get(backStackEntry.arguments!!, "userId")
            ProfileFollowersListScreen(
                userId = userId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }
        // ProfileFollowingsList
        composable(
            route = "${Screen.ProfileFollowingsList.route}?userId={userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val userId = NavType.StringType.get(backStackEntry.arguments!!, "userId")
            ProfileFollowingListScreen(
                userId = userId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }

        // CharacterAnimeList
        composable(
            route = "${Screen.CharacterAnimeList.route}?characterId={characterId}",
            arguments = listOf(
                navArgument("characterId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val characterId = NavType.StringType.get(backStackEntry.arguments!!, "characterId")
            CharacterAnimeListScreen(
                characterId = characterId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }

        // CharacterMangaList
        composable(
            route = "${Screen.CharacterMangaList.route}?characterId={characterId}",
            arguments = listOf(
                navArgument("characterId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val characterId = NavType.StringType.get(backStackEntry.arguments!!, "characterId")
            CharacterMangaListScreen(
                characterId = characterId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }

        // CharacterGameList
        composable(
            route = "${Screen.CharacterGameList.route}?characterId={characterId}",
            arguments = listOf(
                navArgument("characterId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val characterId = NavType.StringType.get(backStackEntry.arguments!!, "characterId")
            CharacterGameListScreen(
                characterId = characterId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }

        // CharacterPeopleList
        composable(
            route = "${Screen.CharacterPeopleList.route}?characterId={characterId}",
            arguments = listOf(
                navArgument("characterId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val characterId = NavType.StringType.get(backStackEntry.arguments!!, "characterId")
            CharacterPeopleListScreen(
                characterId = characterId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }

        // PersonAnimeList
        composable(
            route = "${Screen.PersonAnimeList.route}?personId={personId}",
            arguments = listOf(
                navArgument("personId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val personId = NavType.StringType.get(backStackEntry.arguments!!, "personId")
            PersonAnimeListScreen(
                personId = personId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }

        // PersonMangaList
        composable(
            route = "${Screen.PersonMangaList.route}?personId={personId}",
            arguments = listOf(
                navArgument("personId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val personId = NavType.StringType.get(backStackEntry.arguments!!, "personId")
            PersonMangaListScreen(
                personId = personId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }

        // PersonGameList
        composable(
            route = "${Screen.PersonGameList.route}?personId={personId}",
            arguments = listOf(
                navArgument("personId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val personId = NavType.StringType.get(backStackEntry.arguments!!, "personId")
            PersonGameListScreen(
                personId = personId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }

        // PersonCharacterList
        composable(
            route = "${Screen.PersonCharacterList.route}?personId={personId}",
            arguments = listOf(
                navArgument("personId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val personId = NavType.StringType.get(backStackEntry.arguments!!, "personId")
            PersonCharacterListScreen(
                personId = personId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }

        // SongAnimeList
        composable(
            route = "${Screen.SongAnimeList.route}?songId={songId}",
            arguments = listOf(
                navArgument("songId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val songId = NavType.StringType.get(backStackEntry.arguments!!, "songId")
            SongAnimeListScreen(
                songId = songId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }

        // SongGameList
        composable(
            route = "${Screen.SongGameList.route}?songId={songId}",
            arguments = listOf(
                navArgument("songId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val songId = NavType.StringType.get(backStackEntry.arguments!!, "songId")
            SongGameListScreen(
                songId = songId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }

        // StudioAnimeList
        composable(
            route = "${Screen.StudioAnimeList.route}?studioId={studioId}",
            arguments = listOf(
                navArgument("studioId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val studioId = NavType.StringType.get(backStackEntry.arguments!!, "studioId")
            StudioAnimeListScreen(
                studioId = studioId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }

        // StudioMangaList
        composable(
            route = "${Screen.StudioMangaList.route}?studioId={studioId}",
            arguments = listOf(
                navArgument("studioId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val studioId = NavType.StringType.get(backStackEntry.arguments!!, "studioId")
            StudioMangaListScreen(
                studioId = studioId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }

        // StudioGameList
        composable(
            route = "${Screen.StudioGameList.route}?studioId={studioId}",
            arguments = listOf(
                navArgument("studioId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val studioId = NavType.StringType.get(backStackEntry.arguments!!, "studioId")
            StudioGameListScreen(
                studioId = studioId.orEmpty(),
                onNavigateBack = { navController.popBackStack() },
                windowWidth = windowSize.windowWidthSizeClass,
                onNavigateToItemDetail = { item ->
                    navController.navigateToItemDetail(item)
                },
            )
        }

    }
}

@Composable
fun PlaceholderScreen(screenName: String) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = screenName,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
