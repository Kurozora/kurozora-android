package app.kurozora.tracker.api.response.game

import app.kurozora.tracker.api.response.media.Media
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GameAttributes(
    /**
     * The IGDB id of the game.
     */
    @SerializedName("igdbID")
    val igdbID: Int?,

    /**
     * The IGDB slug of the game.
     */
    @SerializedName("igdbSlug")
    val igdbSlug: String?,

    /**
     * The slug of the game.
     */
    @SerializedName("slug")
    val slug: String,

    /**
     * The media object of the poster of the game.
     */
    @SerializedName("poster")
    val poster: Media?,

    /**
     * The media object of the banner of the game.
     */
    @SerializedName("banner")
    val banner: Media?,

    /**
     * The media object of the logo of the game.
     */
    @SerializedName("logo")
    val logo: Media?,

    /**
     * The original title in the original language of the game.
     */
    @SerializedName("originalTitle")
    val originalTitle: String?,

    /**
     * The localized title of the game.
     */
    @SerializedName("title")
    val title: String,

    /**
     * The synonym titles of the game.
     */
    @SerializedName("synonymTitles")
    val synonymTitles: List<String>?,

    /**
     * The localized tagline of the game.
     */
    @SerializedName("tagline")
    val tagline: String?,

    /**
     * The localized synopsis of the game.
     */
    @SerializedName("synopsis")
    val synopsis: String?,

    /**
     * The genres of the game.
     */
    @SerializedName("genres")
    val genres: List<String>?,

    /**
     * The themes of the game.
     */
    @SerializedName("themes")
    val themes: List<String>?,

    /**
     * The studio of the game.
     */
    @SerializedName("studio")
    val studio: String?,

//    /**
//     * The languages of the game.
//     */
//    @SerializedName("languages")
//    val languages: List<Language>,
//
//    /**
//     * The tv rating of the game.
//     */
//    @SerializedName("tvRating")
//    val tvRating: TVRating,
//
//    /**
//     * The type of the game.
//     */
//    @SerializedName("type")
//    val type: MediaType,
//
//    /**
//     * The adaptation source of the game.
//     */
//    @SerializedName("source")
//    val source: AdaptationSource,
//
//    /**
//     * The airing status of the game.
//     */
//    @SerializedName("status")
//    val status: AiringStatus,

    /**
     * The number of editions in the game.
     */
    @SerializedName("editionCount")
    val editionCount: Int,

//    /**
//     * The stats of the game.
//     */
//    @SerializedName("stats")
//    val stats: MediaStat?,
//
//    /**
//     * The first publication date of the game.
//     */
//    @SerializedName("startedAt")
//    val startedAt: Date?,
//
//    /**
//     * The last publication date of the game.
//     */
//    @SerializedName("endedAt")
//    val endedAt: Date?,

    /**
     * The duration of the game.
     */
    @SerializedName("duration")
    val duration: String,

    /**
     * The calculated total duration of the game.
     */
    @SerializedName("durationTotal")
    val durationTotal: String,

    /**
     * The season the game has published in.
     */
    @SerializedName("publicationSeason")
    val publicationSeason: String?,

    /**
     * The time the game has published at in UTC.
     */
    @SerializedName("publicationTime")
    val publicationTime: String?,

    /**
     * The day the game has published on.
     */
    @SerializedName("publicationDay")
    val publicationDay: String?,

    /**
     * Whether the game is Not Safe For Work.
     */
    @SerializedName("isNSFW")
    val isNSFW: Boolean,

    /**
     * The copyright text of the game.
     */
    @SerializedName("copyright")
    val copyright: String?,

//    /**
//     * The library attributes of the game.
//     */
//    @SerializedName("library")
//    var library: LibraryAttributes?
) : Serializable
