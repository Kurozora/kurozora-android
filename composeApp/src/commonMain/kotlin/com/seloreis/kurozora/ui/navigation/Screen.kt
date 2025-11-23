package com.seloreis.kurozora.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import io.ktor.util.encodeBase64
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

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    object Explore : Screen("explore", "Explore", Icons.Filled.Explore, Icons.Outlined.Explore)
    object Library : Screen("library", "Library", Icons.AutoMirrored.Filled.LibraryBooks, Icons.AutoMirrored.Outlined.LibraryBooks)
    object Search : Screen("search", "Search", Icons.Filled.Search, Icons.Outlined.Search)
    object Feed : Screen("feed", "Feed", Icons.Filled.DynamicFeed, Icons.Outlined.DynamicFeed)
    object Profile : Screen("userProfile", "Profile", Icons.Filled.Person, Icons.Outlined.Person) {
        fun createRoute(user: User): String {
            val json = Json.encodeToString(user)
            val encoded = json.encodeBase64()
            return "userProfile?userJson=$encoded"
        }
    }

    object Notification : Screen("notifications", "Notifications", Icons.Filled.Notifications, Icons.Outlined.Notifications)

    object Login : Screen("login", "Login")

    object Favorite : Screen("favorite", "Favorite")
    object Reminder : Screen("reminder", "Reminder")

    object AirSeason: Screen("airseason", "Air Season")
    object Schedule : Screen("schedule", "Schedule")

    object Settings : Screen("settings", "Settings")


    object AnimeDetail : Screen("animeDetail", "Anime Detail") {
        fun createRoute(show: Show): String {
            val json = Json.encodeToString(show)
            val encoded = json.encodeBase64()
            return "animeDetail?showJson=$encoded"
        }
    }

    object MangaDetail : Screen("mangaDetail", "Manga Detail") {
        fun createRoute(lit: Literature): String {
            val json = Json.encodeToString(lit)
            val encoded = json.encodeBase64()
            return "mangaDetail?litJson=$encoded"
        }
    }

    object GameDetail : Screen("gameDetail", "Game Detail") {
        fun createRoute(game: Game): String {
            val json = Json.encodeToString(game)
            val encoded = json.encodeBase64()
            return "gameDetail?gameJson=$encoded"
        }
    }

    object CharacterDetail : Screen("characterDetail", "Character Detail") {
        fun createRoute(character: Character): String {
            val json = Json.encodeToString(character)
            val encoded = json.encodeBase64()
            return "characterDetail?characterJson=$encoded"
        }
    }

    object EpisodeDetail : Screen("EpisodeDetail", "Episode Detail") {
        fun createRoute(episode: Episode): String {
            val json = Json.encodeToString(episode)
            val encoded = json.encodeBase64()
            return "episodeDetail?episodeJson=$encoded"
        }
    }

    object PersonDetail : Screen("personDetail", "Person Detail") {
        fun createRoute(person: Person): String {
            val json = Json.encodeToString(person)
            val encoded = json.encodeBase64()
            return "personDetail?personJson=$encoded"
        }
    }

    object StudioDetail : Screen("studioDetail", "Studio Detail") {
        fun createRoute(studio: Studio): String {
            val json = Json.encodeToString(studio)
            val encoded = json.encodeBase64()
            return "studioDetail?studioJson=$encoded"
        }
    }

    object SongDetail : Screen("songDetail", "Song Detail") {
        fun createRoute(song: Song): String {
            val json = Json.encodeToString(song)
            val encoded = json.encodeBase64()
            return "songDetail?songJson=$encoded"
        }
    }

    object SeasonDetail : Screen("seasonDetail", "Season Detail") {
        fun createRoute(season: Season): String {
            val json = Json.encodeToString(season)
            val encoded = json.encodeBase64()
            return "seasonDetail?seasonJson=$encoded"
        }
    }

    object ExploreCategoryDetail : Screen("exploreCategory", "Explore Category") {
        fun createRoute(category: ExploreCategory): String {
            val json = Json.encodeToString(category)
            val encoded = json.encodeBase64()
            return "exploreCategory?exploreCategoryJson=$encoded"
        }
    }

    object ShowSeasonList : Screen("showSeasonList", "Show Season List") {
        fun createRoute(showId: String): String {
            return "showSeasonList?showId=$showId"
        }
    }

    object ShowCastList : Screen("showCastList", "Show Cast List") {
        fun createRoute(showId: String): String {
            return "showCastList?showId=$showId"
        }
    }

    object ShowStaffList : Screen("showStaffList", "Show Staff List") {
        fun createRoute(showId: String): String {
            return "showStaffList?showId=$showId"
        }
    }

    object ShowSongList : Screen("showSongList", "Show Song List") {
        fun createRoute(showId: String): String {
            return "showSongList?showId=$showId"
        }
    }

    object ShowStudioList : Screen("showStudioList", "Show Studio List") {
        fun createRoute(showId: String): String {
            return "showStudioList?showId=$showId"
        }
    }

    object ShowMoreByStudioList : Screen("showMoreByStudioList", "Show MoreByStudio List") {
        fun createRoute(showId: String): String {
            return "showMoreByStudioList?showId=$showId"
        }
    }

    object ShowRelatedShowList : Screen("showRelatedShowList", "Show Related Show List") {
        fun createRoute(showId: String): String {
            return "showRelatedShowList?showId=$showId"
        }
    }

    object ShowRelatedLiteratureList : Screen("showRelatedLiteratureList", "Show Related Literature List") {
        fun createRoute(showId: String): String {
            return "showRelatedLiteratureList?showId=$showId"
        }
    }
    object ShowRelatedGameList : Screen("showRelatedGameList", "Show Related Game List") {
        fun createRoute(showId: String): String {
            return "showRelatedGameList?showId=$showId"
        }
    }

    object LiteraturePeopleList : Screen("literaturePeopleList", "Literature People List") {
        fun createRoute(litId: String): String {
            return "literaturePeopleList?litId=$litId"
        }
    }

    object LiteratureCastList : Screen("literatureCastList", "Literature Cast List") {
        fun createRoute(litId: String): String {
            return "literatureCastList?litId=$litId"
        }
    }

    object LiteratureStaffList : Screen("literatureStaffList", "Literature Staff List") {
        fun createRoute(litId: String): String {
            return "literatureStaffList?litId=$litId"
        }
    }

    object LiteratureCharacterList : Screen("literatureCharacterList", "Literature Character List") {
        fun createRoute(litId: String): String {
            return "literatureCharacterList?litId=$litId"
        }
    }

    object LiteratureStudioList : Screen("literatureStudioList", "Literature Studio List") {
        fun createRoute(litId: String): String {
            return "literatureStudioList?litId=$litId"
        }
    }

    object LiteratureMoreByStudioList : Screen("literatureMoreByStudioList", "Literature More By Studio List") {
        fun createRoute(litId: String): String {
            return "literatureMoreByStudioList?litId=$litId"
        }
    }

    object LiteratureRelatedShowList : Screen("literatureRelatedShowList", "Literature Related Show List") {
        fun createRoute(litId: String): String {
            return "literatureRelatedShowList?litId=$litId"
        }
    }

    object LiteratureRelatedLiteratureList : Screen("literatureRelatedLiteratureList", "Literature Related Literature List") {
        fun createRoute(litId: String): String {
            return "literatureRelatedLiteratureList?litId=$litId"
        }
    }

    object LiteratureRelatedGameList : Screen("literatureRelatedGameList", "Literature Related Game List") {
        fun createRoute(litId: String): String {
            return "literatureRelatedGameList?litId=$litId"
        }
    }

    object GamePeopleList : Screen("gamePeopleList", "Game People List") {
        fun createRoute(gameId: String): String {
            return "gamePeopleList?gameId=$gameId"
        }
    }

    object GameCastList : Screen("gameCastList", "Game Cast List") {
        fun createRoute(gameId: String): String {
            return "gameCastList?gameId=$gameId"
        }
    }

    object GameStaffList : Screen("gameStaffList", "Game Staff List") {
        fun createRoute(gameId: String): String {
            return "gameStaffList?gameId=$gameId"
        }
    }

    object GameCharacterList : Screen("gameCharacterList", "Game Character List") {
        fun createRoute(gameId: String): String {
            return "gameCharacterList?gameId=$gameId"
        }
    }

    object GameStudioList : Screen("gameStudioList", "Game Studio List") {
        fun createRoute(gameId: String): String {
            return "gameStudioList?gameId=$gameId"
        }
    }

    object GameMoreByStudioList : Screen("gameMoreByStudioList", "Game More By Studio List") {
        fun createRoute(gameId: String): String {
            return "gameMoreByStudioList?gameId=$gameId"
        }
    }

    object GameRelatedShowList : Screen("gameRelatedShowList", "Game Related Show List") {
        fun createRoute(gameId: String): String {
            return "gameRelatedShowList?gameId=$gameId"
        }
    }

    object GameRelatedLiteratureList : Screen("gameRelatedLiteratureList", "Game Related Literature List") {
        fun createRoute(gameId: String): String {
            return "gameRelatedLiteratureList?gameId=$gameId"
        }
    }

    object GameRelatedGameList : Screen("gameRelatedGameList", "Game Related Game List") {
        fun createRoute(gameId: String): String {
            return "gameRelatedGameList?gameId=$gameId"
        }
    }

    object ProfileAnimeLibraryList : Screen("profileAnimeLibraryList", "Profile Anime Library List") {
        fun createRoute(userId: String): String {
            return "profileAnimeLibraryList?userId=$userId"
        }
    }

    object ProfileMangaLibraryList : Screen("profileMangaLibraryList", "Profile Manga Library List") {
        fun createRoute(userId: String): String {
            return "profileMangaLibraryList?userId=$userId"
        }
    }

    object ProfileGameLibraryList : Screen("profileGameLibraryList", "Profile Game Library List") {
        fun createRoute(userId: String): String {
            return "profileGameLibraryList?userId=$userId"
        }
    }

    object ProfileFavoriteAnimeList : Screen("profileFavoriteAnimeList", "Profile Favorite Anime List") {
        fun createRoute(userId: String): String {
            return "profileFavoriteAnimeList?userId=$userId"
        }
    }

    object ProfileFavoriteMangaList : Screen("profileFavoriteMangaList", "Profile Favorite Manga List") {
        fun createRoute(userId: String): String {
            return "profileFavoriteMangaList?userId=$userId"
        }
    }

    object ProfileFavoriteGameList : Screen("profileFavoriteGameList", "Profile Favorite Game List") {
        fun createRoute(userId: String): String {
            return "profileFavoriteGameList?userId=$userId"
        }
    }

    object ProfileFollowingsList : Screen("profileFollowingsList", "Profile Followings List") {
        fun createRoute(userId: String): String {
            return "profileFollowingsList?userId=$userId"
        }
    }

    object ProfileFollowersList : Screen("profileFollowersList", "Profile Followers List") {
        fun createRoute(userId: String): String {
            return "profileFollowersList?userId=$userId"
        }
    }


}