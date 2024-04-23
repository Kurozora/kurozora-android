package app.kurozora.tracker.api.response.show

import app.kurozora.tracker.api.response.media.Media
import java.io.Serializable

data class ShowAttributes(
    /**
     * The AniDB id of the show.
     */
    val anidbID: Int?,
    /**
     * The AniList id of the show.
     */
    val anilistID: Int?,
    /**
     * The Anime-Planet id of the show.
     */
    val animePlanetID: String?,
    /**
     * The AniSearch id of the show.
     */
    val anisearchID: Int?,
    /**
     * The IMDB id of the show.
     */
    val imdbID: String?,
    /**
     * The Kitsu id of the show.
     */
    val kitsuID: Int?,
    /**
     * The MyAnimeList id of the show.
     */
    val malID: Int?,
    /**
     * The Notify id of the show.
     */
    val notifyID: String?,
    /**
     * The Syoboi id of the show.
     */
    val syoboiID: Int?,
    /**
     * The Trakt id of the show.
     */
    val traktID: Int?,
    /**
     * The TVDB id of the show.
     */
    val tvdbID: Int?,
    /**
     * The slug of the show.
     */
    val slug: String,
    /**
     * The video url of the show.
     */
    val videoUrl: String?,
    /**
     * The media object of the poster of the show.
     */
    val poster: Media?,
    /**
     * The media object of the banner of the show.
     */
    val banner: Media?,
    /**
     * The media object of the logo of the show.
     */
    val logo: Media?,
    /**
     * The original title in the original language of the show.
     */
    val originalTitle: String?,
    /**
     * The localized title of the show.
     */
    val title: String,
    /**
     * The synonym titles of the show.
     */
    val synonymTitles: List<String>?,

    /**
     * The localized tagline of the show.
     */
    val tagline: String?,

    /**
     * The localized synopsis of the show.
     */
    val synopsis: String?,

    /**
     * The genres of the show.
     */
    val genres: List<String>?,

    /**
     * The themes of the show.
     */
    val themes: List<String>?,

    /**
     * The studio of the show.
     */
    val studio: String?,

//    /**
//     * The languages of the show.
//     */
//    val languages: List<Language>,
//
//    /**
//     * The tv rating of the show.
//     */
//    val tvRating: TVRating,
//
//    /**
//     * The type of the show.
//     */
//    val type: MediaType,
//
//    /**
//     * The adaptation source of the show.
//     */
//    val source: AdaptationSource,
//
//    /**
//     * The airing status of the show.
//     */
//    val status: AiringStatus,

    /**
     * The number of episodes in the show.
     */
    val episodeCount: Int,

    /**
     * The number of seasons in the show.
     */
    val seasonCount: Int,

//    /**
//     * The stats of the show.
//     */
//    val stats: MediaStat?,
//
//    /**
//     * The first air date of the show.
//     */
//    val startedAt: Date?,
//
//    /**
//     * The last air date of the show.
//     */
//    val endedAt: Date?,

    /**
     * The duration of the show.
     */
    val duration: String,

    /**
     * The calculated total duration of the show.
     */
    val durationTotal: String,

    /**
     * The season the show has aired in.
     */
    val airSeason: String?,

    /**
     * The time the show has aired at in UTC.
     */
    val airTime: String?,

    /**
     * The day the show has aired on.
     */
    val airDay: String?,

    /**
     * Whether the show is Not Safe For Work.
     */
    val isNSFW: Boolean,

    /**
     * The copyright text of the show.
     */
    val copyright: String?,
) : Serializable