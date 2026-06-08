package app.kurozora.ui.screens.library.imports

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import kotlinx.serialization.Serializable
import kotlinx.serialization.ExperimentalSerializationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import java.nio.charset.Charset

/**
 * MALImport - Tek object, tüm MAL import/export işlemleri
 * Kullanım:
 *   val result = MALImport.parseAnime(xmlString)
 *   MALImport.UI.AnimeListScreen(result.animeList) { updatedList -> }
 */
object MALImport {

    // ==================== ANIME MODELLERİ ====================
    @Serializable
    data class AnimeInfo(
        val user_id: Int,
        val user_name: String,
        val user_export_type: Int,
        val user_total_anime: Int,
        val user_total_watching: Int,
        val user_total_completed: Int,
        val user_total_onhold: Int,
        val user_total_dropped: Int,
        val user_total_plantowatch: Int
    )

    @Serializable
    @XmlSerialName("anime")
    data class RawAnime(
        @XmlElement(true) val series_animedb_id: Int = 0,
        @XmlElement(true) val series_title: String = "",
        @XmlElement(true) val series_type: String = "",
        @XmlElement(true) val series_episodes: Int = 0,
        @XmlElement(true) val my_id: Int = 0,
        @XmlElement(true) val my_watched_episodes: Int = 0,
        @XmlElement(true) val my_start_date: String = "",
        @XmlElement(true) val my_finish_date: String = "",
        @XmlElement(true) val my_rated: String = "",
        @XmlElement(true) val my_score: Int = 0,
        @XmlElement(true) val my_storage: String = "",
        @XmlElement(true) val my_storage_value: Double = 0.0,
        @XmlElement(true) val my_status: String = "",
        @XmlElement(true) val my_comments: String = "",
        @XmlElement(true) val my_times_watched: Int = 0,
        @XmlElement(true) val my_rewatch_value: String = "",
        @XmlElement(true) val my_priority: String = "",
        @XmlElement(true) val my_tags: String = "",
        @XmlElement(true) val my_rewatching: Int = 0,
        @XmlElement(true) val my_rewatching_ep: Int = 0,
        @XmlElement(true) val my_discuss: Int = 0,
        @XmlElement(true) val my_sns: String = "",
        @XmlElement(true) val update_on_import: Int = 0
    )

    @Serializable
    data class AnimeItem(
        val raw: RawAnime,
        var isSelected: Boolean = true,
        val displayTitle: String = raw.series_title,
        val displayStatus: String = raw.my_status,
        val displayScore: String = if (raw.my_score > 0) "${raw.my_score}/10" else "Not Scored",
        val progress: String = "${raw.my_watched_episodes}/${raw.series_episodes}",
        val progressPercentage: Float = if (raw.series_episodes > 0) raw.my_watched_episodes.toFloat() / raw.series_episodes.toFloat() else 0f
    )

    @Serializable
    data class AnimeResult(
        val myinfo: AnimeInfo,
        val animeList: List<AnimeItem>
    )

    // ==================== MANGA MODELLERİ ====================
    @Serializable
    data class MangaInfo(
        val user_id: Int,
        val user_name: String,
        val user_export_type: Int,
        val user_total_manga: Int,
        val user_total_reading: Int,
        val user_total_completed: Int,
        val user_total_onhold: Int,
        val user_total_dropped: Int,
        val user_total_plantoread: Int
    )

    @Serializable
    @XmlSerialName("manga")
    data class RawManga(
        val manga_mangadb_id: Int,
        val manga_title: String,
        val manga_volumes: Int,
        val manga_chapters: Int,
        val my_id: Int,
        val my_read_volumes: Int,
        val my_read_chapters: Int,
        val my_start_date: String,
        val my_finish_date: String,
        val my_scanalation_group: String,
        val my_score: Int,
        val my_storage: String,
        val my_retail_volumes: Int,
        val my_status: String,
        val my_comments: String,
        val my_times_read: Int,
        val my_tags: String,
        val my_priority: String,
        val my_reread_value: String,
        val my_rereading: String,
        val my_discuss: String,
        val my_sns: String,
        val update_on_import: Int
    )

    @Serializable
    data class MangaItem(
        val raw: RawManga,
        var isSelected: Boolean = true,
        val displayTitle: String = raw.manga_title,
        val displayStatus: String = raw.my_status,
        val displayScore: String = if (raw.my_score > 0) "${raw.my_score}/10" else "Not Scored",
        val progress: String = "${raw.my_read_chapters}/${raw.manga_chapters}",
        val progressPercentage: Float = if (raw.manga_chapters > 0) raw.my_read_chapters.toFloat() / raw.manga_chapters.toFloat() else 0f
    )

    @Serializable
    data class MangaResult(
        val myinfo: MangaInfo,
        val mangaList: List<MangaItem>
    )

    // ==================== XML MODELLER ====================
    @Serializable
    @XmlSerialName("myanimelist")
    private data class RawMyAnimeList(
        @XmlElement val myinfo: RawAnimeInfo,
        @XmlElement val anime: List<RawAnime> = emptyList()
    )

    @Serializable
    @XmlSerialName("myinfo")
    private data class RawAnimeInfo(
        @XmlElement val user_id: Int,
        @XmlElement val user_name: String,
        @XmlElement val user_export_type: Int,
        @XmlElement val user_total_anime: Int,
        @XmlElement val user_total_watching: Int,
        @XmlElement val user_total_completed: Int,
        @XmlElement val user_total_onhold: Int,
        @XmlElement val user_total_dropped: Int,
        @XmlElement val user_total_plantowatch: Int
    )

    @Serializable
    @XmlSerialName("mymangalist")
    private data class RawMyMangaList(
        @XmlElement val myinfo: RawMangaInfo,
        @XmlElement val manga: List<RawManga> = emptyList()
    )

    @Serializable
    @XmlSerialName("myinfo")
    private data class RawMangaInfo(
        @XmlElement val user_id: Int,
        @XmlElement val user_name: String,
        @XmlElement val user_export_type: Int,
        @XmlElement val user_total_manga: Int,
        @XmlElement val user_total_reading: Int,
        @XmlElement val user_total_completed: Int,
        @XmlElement val user_total_onhold: Int,
        @XmlElement val user_total_dropped: Int,
        @XmlElement val user_total_plantoread: Int
    )

    // ==================== XML PARSER ====================
    // ==================== XML PARSER ====================
    @OptIn(ExperimentalSerializationApi::class, ExperimentalXmlUtilApi::class)
    private val xml = XML {
        this.xmlDeclMode = XmlDeclMode.None
        // Bilinmeyen (data class'ta tanımlamadığın) bir XML etiketi gelirse çökmesini engeller
        this.unknownChildHandler = nl.adaptivity.xmlutil.serialization.UnknownChildHandler { _, _, _, _, _ ->
            emptyList()
        }
    }

    object Anime {
        /**
         * XML string'inden anime listesini parse et (TÜM alanlarla birlikte)
         */
        fun parse(xmlString: String): AnimeResult? {
            return try {
                val cleanXmlString = xmlString.trim()
                val raw = xml.decodeFromString<RawMyAnimeList>(cleanXmlString)
                val animeInfo = AnimeInfo(
                    user_id = raw.myinfo.user_id,
                    user_name = raw.myinfo.user_name,
                    user_export_type = raw.myinfo.user_export_type,
                    user_total_anime = raw.myinfo.user_total_anime,
                    user_total_watching = raw.myinfo.user_total_watching,
                    user_total_completed = raw.myinfo.user_total_completed,
                    user_total_onhold = raw.myinfo.user_total_onhold,
                    user_total_dropped = raw.myinfo.user_total_dropped,
                    user_total_plantowatch = raw.myinfo.user_total_plantowatch
                )

                val animeList = raw.anime.map { AnimeItem(raw = it, isSelected = true) }

                AnimeResult(myinfo = animeInfo, animeList = animeList)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        /**
         * Anime listesini orijinal XML formatına çevir ve byte array olarak döndür
         */
        fun exportToBytes(animeResult: AnimeResult, onlySelected: Boolean = true): ByteArray {
            val animeListToExport = if (onlySelected)
                animeResult.animeList.filter { it.isSelected }.map { it.raw }
            else
                animeResult.animeList.map { it.raw }

            // Orijinal myinfo'yu kullan, sadece total sayıları güncelle
            val updatedInfo = animeResult.myinfo.copy(
                user_total_anime = animeListToExport.size,
                user_total_watching = animeListToExport.count { it.my_status == "Watching" },
                user_total_completed = animeListToExport.count { it.my_status == "Completed" },
                user_total_onhold = animeListToExport.count { it.my_status == "On-Hold" },
                user_total_dropped = animeListToExport.count { it.my_status == "Dropped" },
                user_total_plantowatch = animeListToExport.count { it.my_status == "Plan to Watch" }
            )

            val rawInfo = RawAnimeInfo(
                user_id = updatedInfo.user_id,
                user_name = updatedInfo.user_name,
                user_export_type = updatedInfo.user_export_type,
                user_total_anime = updatedInfo.user_total_anime,
                user_total_watching = updatedInfo.user_total_watching,
                user_total_completed = updatedInfo.user_total_completed,
                user_total_onhold = updatedInfo.user_total_onhold,
                user_total_dropped = updatedInfo.user_total_dropped,
                user_total_plantowatch = updatedInfo.user_total_plantowatch
            )

            val rawList = RawMyAnimeList(
                myinfo = rawInfo,
                anime = animeListToExport
            )

            val xmlString = buildString {
                appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>")
                appendLine()
                appendLine("<!--")
                appendLine(" Created by XML Export feature at MyAnimeList.net")
                appendLine(" Version 1.1.0")
                appendLine("-->")
                appendLine()

                val rawXml = xml.encodeToString(rawList)
                // XML deklarasyonunu kaldır (biz ekledik)
                val cleanXml = rawXml.substringAfter("?>").trim()
                append(cleanXml)
            }

            return xmlString.toByteArray(Charset.forName("UTF-8"))
        }

        /**
         * İstatistikleri hesapla
         */
        fun getStats(animeList: List<AnimeItem>): Map<String, Any> {
            return mapOf(
                "total" to animeList.size,
                "watching" to animeList.count { it.raw.my_status == "Watching" },
                "completed" to animeList.count { it.raw.my_status == "Completed" },
                "onHold" to animeList.count { it.raw.my_status == "On-Hold" },
                "dropped" to animeList.count { it.raw.my_status == "Dropped" },
                "planToWatch" to animeList.count { it.raw.my_status == "Plan to Watch" },
                "averageScore" to (animeList.filter { it.raw.my_score > 0 }.map { it.raw.my_score }.average()),
                "totalEpisodes" to animeList.sumOf { it.raw.my_watched_episodes },
                "selectedCount" to animeList.count { it.isSelected }
            )
        }

        /**
         * Anime ara
         */
        fun search(animeList: List<AnimeItem>, query: String): List<AnimeItem> {
            if (query.isBlank()) return animeList
            return animeList.filter {
                it.raw.series_title.contains(query, ignoreCase = true)
            }
        }

        /**
         * Duruma göre filtrele
         */
        fun filterByStatus(animeList: List<AnimeItem>, status: String?): List<AnimeItem> {
            return if (status == null) animeList
            else animeList.filter { it.raw.my_status == status }
        }

        /**
         * Seçim işlemleri
         */
        fun selectAll(animeList: List<AnimeItem>): List<AnimeItem> {
            return animeList.map { it.copy(isSelected = true) }
        }

        fun deselectAll(animeList: List<AnimeItem>): List<AnimeItem> {
            return animeList.map { it.copy(isSelected = false) }
        }

        fun toggleSelect(animeList: List<AnimeItem>, id: Int): List<AnimeItem> {
            return animeList.map {
                if (it.raw.series_animedb_id == id)
                    it.copy(isSelected = !it.isSelected)
                else it
            }
        }

        fun selectByIds(animeList: List<AnimeItem>, ids: Set<Int>): List<AnimeItem> {
            return animeList.map {
                if (ids.contains(it.raw.series_animedb_id))
                    it.copy(isSelected = true)
                else it
            }
        }

        fun deselectByIds(animeList: List<AnimeItem>, ids: Set<Int>): List<AnimeItem> {
            return animeList.map {
                if (ids.contains(it.raw.series_animedb_id))
                    it.copy(isSelected = false)
                else it
            }
        }

        fun getSelected(animeList: List<AnimeItem>): List<AnimeItem> {
            return animeList.filter { it.isSelected }
        }

        fun getUnselected(animeList: List<AnimeItem>): List<AnimeItem> {
            return animeList.filter { !it.isSelected }
        }

        /**
         * Sıralama
         */
        fun sortByTitle(animeList: List<AnimeItem>, ascending: Boolean = true): List<AnimeItem> {
            return if (ascending)
                animeList.sortedBy { it.raw.series_title }
            else
                animeList.sortedByDescending { it.raw.series_title }
        }

        fun sortByScore(animeList: List<AnimeItem>, ascending: Boolean = false): List<AnimeItem> {
            return if (ascending)
                animeList.sortedBy { it.raw.my_score }
            else
                animeList.sortedByDescending { it.raw.my_score }
        }

        fun sortByProgress(animeList: List<AnimeItem>, ascending: Boolean = false): List<AnimeItem> {
            return if (ascending)
                animeList.sortedBy { it.progressPercentage }
            else
                animeList.sortedByDescending { it.progressPercentage }
        }

        fun sortByStatus(animeList: List<AnimeItem>): List<AnimeItem> {
            return animeList.sortedBy { it.raw.my_status }
        }
    }

    object Manga {
        /**
         * XML string'inden manga listesini parse et (TÜM alanlarla birlikte)
         */
        fun parse(xmlString: String): MangaResult? {
            return try {
                val raw = xml.decodeFromString<RawMyMangaList>(xmlString)
                val mangaInfo = MangaInfo(
                    user_id = raw.myinfo.user_id,
                    user_name = raw.myinfo.user_name,
                    user_export_type = raw.myinfo.user_export_type,
                    user_total_manga = raw.myinfo.user_total_manga,
                    user_total_reading = raw.myinfo.user_total_reading,
                    user_total_completed = raw.myinfo.user_total_completed,
                    user_total_onhold = raw.myinfo.user_total_onhold,
                    user_total_dropped = raw.myinfo.user_total_dropped,
                    user_total_plantoread = raw.myinfo.user_total_plantoread
                )

                val mangaList = raw.manga.map { MangaItem(raw = it, isSelected = true) }

                MangaResult(myinfo = mangaInfo, mangaList = mangaList)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        /**
         * Manga listesini orijinal XML formatına çevir ve byte array olarak döndür
         */
        fun exportToBytes(mangaResult: MangaResult, onlySelected: Boolean = true): ByteArray {
            val mangaListToExport = if (onlySelected)
                mangaResult.mangaList.filter { it.isSelected }.map { it.raw }
            else
                mangaResult.mangaList.map { it.raw }

            val updatedInfo = mangaResult.myinfo.copy(
                user_total_manga = mangaListToExport.size,
                user_total_reading = mangaListToExport.count { it.my_status == "Reading" },
                user_total_completed = mangaListToExport.count { it.my_status == "Completed" },
                user_total_onhold = mangaListToExport.count { it.my_status == "On-Hold" },
                user_total_dropped = mangaListToExport.count { it.my_status == "Dropped" },
                user_total_plantoread = mangaListToExport.count { it.my_status == "Plan to Read" }
            )

            val rawInfo = RawMangaInfo(
                user_id = updatedInfo.user_id,
                user_name = updatedInfo.user_name,
                user_export_type = updatedInfo.user_export_type,
                user_total_manga = updatedInfo.user_total_manga,
                user_total_reading = updatedInfo.user_total_reading,
                user_total_completed = updatedInfo.user_total_completed,
                user_total_onhold = updatedInfo.user_total_onhold,
                user_total_dropped = updatedInfo.user_total_dropped,
                user_total_plantoread = updatedInfo.user_total_plantoread
            )

            val rawList = RawMyMangaList(
                myinfo = rawInfo,
                manga = mangaListToExport
            )

            val xmlString = buildString {
                appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>")
                appendLine()
                appendLine("<!--")
                appendLine(" Created by XML Export feature at MyAnimeList.net")
                appendLine(" Version 1.1.0")
                appendLine("-->")
                appendLine()

                val rawXml = xml.encodeToString(rawList)
                val cleanXml = rawXml.substringAfter("?>").trim()
                append(cleanXml)
            }

            return xmlString.toByteArray(Charset.forName("UTF-8"))
        }

        // Manga için de benzer yardımcı fonksiyonlar (search, filter, sort, select)
        fun search(mangaList: List<MangaItem>, query: String): List<MangaItem> {
            if (query.isBlank()) return mangaList
            return mangaList.filter {
                it.raw.manga_title.contains(query, ignoreCase = true)
            }
        }

        fun getStats(mangaList: List<MangaItem>): Map<String, Any> {
            return mapOf(
                "total" to mangaList.size,
                "reading" to mangaList.count { it.raw.my_status == "Reading" },
                "completed" to mangaList.count { it.raw.my_status == "Completed" },
                "planToRead" to mangaList.count { it.raw.my_status == "Plan to Read" },
                "averageScore" to (mangaList.filter { it.raw.my_score > 0 }.map { it.raw.my_score }.average()),
                "totalChapters" to mangaList.sumOf { it.raw.my_read_chapters },
                "selectedCount" to mangaList.count { it.isSelected }
            )
        }

        fun filterByStatus(mangaList: List<MangaItem>, status: String?): List<MangaItem> {
            return if (status == null) mangaList
            else mangaList.filter { it.raw.my_status == status }
        }

        fun selectAll(mangaList: List<MangaItem>): List<MangaItem> {
            return mangaList.map { it.copy(isSelected = true) }
        }

        fun deselectAll(mangaList: List<MangaItem>): List<MangaItem> {
            return mangaList.map { it.copy(isSelected = false) }
        }

        fun toggleSelect(mangaList: List<MangaItem>, id: Int): List<MangaItem> {
            return mangaList.map {
                if (it.raw.manga_mangadb_id == id)
                    it.copy(isSelected = !it.isSelected)
                else it
            }
        }
    }

    // ==================== COMPOSE UI ====================
    object UI {

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun AnimeListScreen(
            animeResult: AnimeResult,
            onExport: (ByteArray) -> Unit,
            modifier: Modifier = Modifier
        ) {
            var animeList by remember { mutableStateOf(animeResult.animeList) }
            var searchQuery by remember { mutableStateOf("") }
            var selectedStatus by remember { mutableStateOf<String?>(null) }
            var sortOption by remember { mutableStateOf(SortOption.TITLE) }
            var sortAscending by remember { mutableStateOf(true) }
            var showStats by remember { mutableStateOf(false) }
            var showFilterSheet by remember { mutableStateOf(false) }

            val stats = remember(animeList) { Anime.getStats(animeList) }

            val filteredList = remember(animeList, searchQuery, selectedStatus, sortOption, sortAscending) {
                var result = animeList

                // Arama
                if (searchQuery.isNotBlank()) {
                    result = Anime.search(result, searchQuery)
                }

                // Status filtresi
                if (selectedStatus != null) {
                    result = Anime.filterByStatus(result, selectedStatus)
                }

                // Sıralama
                result = when (sortOption) {
                    SortOption.TITLE -> Anime.sortByTitle(result, sortAscending)
                    SortOption.SCORE -> Anime.sortByScore(result, sortAscending)
                    SortOption.PROGRESS -> Anime.sortByProgress(result, sortAscending)
                    SortOption.STATUS -> Anime.sortByStatus(result)
                    SortOption.ID -> result.sortedBy { it.raw.series_animedb_id }
                }

                result
            }

            // Filter Bottom Sheet
            if (showFilterSheet) {
                FilterBottomSheet(
                    selectedStatus = selectedStatus,
                    sortOption = sortOption,
                    sortAscending = sortAscending,
                    onStatusSelected = { selectedStatus = it },
                    onSortChange = { sortOption = it },
                    onSortDirectionChange = { sortAscending = !sortAscending },
                    onDismiss = { showFilterSheet = false }
                )
            }

            // Stats Bottom Sheet
            if (showStats) {
                StatsBottomSheet(
                    stats = stats,
                    onDismiss = { showStats = false }
                )
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "Anime Listem",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = Color.White,
                            actionIconContentColor = Color.White
                        ),
                        actions = {
                            // Seçili sayısı
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .background(
                                        color = Color.White.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${stats["selectedCount"]}/${stats["total"]}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }

                            // Filtre butonu
                            IconButton(
                                onClick = { showFilterSheet = true },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = if (selectedStatus != null || sortOption != SortOption.TITLE)
                                        Color.White.copy(alpha = 0.9f)
                                    else Color.White
                                )
                            ) {
                                Badge(
                                    containerColor = if (selectedStatus != null || sortOption != SortOption.TITLE)
                                        Color(0xFFFFC107)
                                    else Color.Transparent
                                ) {
                                    Icon(
                                        Icons.Default.Tune,
                                        contentDescription = "Filter",
                                        tint = if (selectedStatus != null || sortOption != SortOption.TITLE)
                                            Color.Black
                                        else Color.White
                                    )
                                }
                            }

                            // Stats butonu
                            IconButton(onClick = { showStats = true }) {
                                Icon(Icons.Default.Info, contentDescription = "Stats")
                            }

                            // Export butonu
                            IconButton(
                                onClick = {
                                    val bytes = Anime.exportToBytes(
                                        animeResult.copy(animeList = animeList),
                                        onlySelected = true
                                    )
                                    onExport(bytes)
                                }
                            ) {
                                Icon(Icons.Default.Download, contentDescription = "Export")
                            }
                        }
                    )
                },
                bottomBar = {
                    BottomAppBar(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Selection controls
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilledTonalButton(
                                    onClick = { animeList = Anime.selectAll(animeList) },
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f),
                                        contentColor = Color(0xFF4CAF50)
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Tümünü Seç")
                                }

                                FilledTonalButton(
                                    onClick = { animeList = Anime.deselectAll(animeList) },
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = Color(0xFFF44336).copy(alpha = 0.1f),
                                        contentColor = Color(0xFFF44336)
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Icon(
                                        Icons.Default.RemoveCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Tümünü Kaldır")
                                }
                            }

                            // Active filters indicator
                            if (selectedStatus != null || sortOption != SortOption.TITLE) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text(
                                        text = buildString {
                                            if (selectedStatus != null) {
                                                append(when (selectedStatus) {
                                                    "Watching" -> "İzleniyor"
                                                    "Completed" -> "Tamamlandı"
                                                    "Plan to Watch" -> "İzlenecek"
                                                    "On-Hold" -> "Beklemede"
                                                    "Dropped" -> "Bırakıldı"
                                                    else -> selectedStatus
                                                })
                                            }
                                            if (sortOption != SortOption.TITLE) {
                                                if (selectedStatus != null) append(" • ")
                                                append("Sıralama: ${when (sortOption) {
                                                    SortOption.SCORE -> "Puan"
                                                    SortOption.PROGRESS -> "İlerleme"
                                                    SortOption.STATUS -> "Durum"
                                                    SortOption.ID -> "ID"
                                                    else -> ""
                                                }} ${if (sortAscending) "↑" else "↓"}")
                                            }
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Search Bar
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it }
                    )

                    // Anime List
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredList) { anime ->
                            AnimeCard(
                                anime = anime,
                                onToggleSelect = {
                                    animeList = Anime.toggleSelect(animeList, it.raw.series_animedb_id)
                                }
                            )
                        }

                        if (filteredList.isEmpty()) {
                            item {
                                EmptyStateView(
                                    hasSearchQuery = searchQuery.isNotBlank(),
                                    hasFilter = selectedStatus != null
                                )
                            }
                        }
                    }
                }
            }
        }

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        private fun FilterBottomSheet(
            selectedStatus: String?,
            sortOption: SortOption,
            sortAscending: Boolean,
            onStatusSelected: (String?) -> Unit,
            onSortChange: (SortOption) -> Unit,
            onSortDirectionChange: () -> Unit,
            onDismiss: () -> Unit
        ) {
            ModalBottomSheet(
                onDismissRequest = onDismiss,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filtrele ve Sırala",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        TextButton(
                            onClick = {
                                onStatusSelected(null)
                                onSortChange(SortOption.TITLE)
                                onDismiss()
                            }
                        ) {
                            Text("Sıfırla")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Status Filter Section
                    Text(
                        text = "Durum",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val statuses = listOf(
                            null to "Tümü",
                            "Watching" to "İzleniyor",
                            "Completed" to "Tamamlandı",
                            "Plan to Watch" to "İzlenecek",
                            "On-Hold" to "Beklemede",
                            "Dropped" to "Bırakıldı"
                        )

                        statuses.forEach { (status, label) ->
                            FilterChip(
                                selected = selectedStatus == status,
                                onClick = { onStatusSelected(status) },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sort Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sıralama",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        // Sort direction toggle
                        IconButton(
                            onClick = onSortDirectionChange,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        ) {
                            Icon(
                                if (sortAscending) Icons.Default.ArrowUpward
                                else Icons.Default.ArrowDownward,
                                contentDescription = "Sort Direction",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sort options
                    SortOption.entries.forEach { option ->
                        val isSelected = sortOption == option

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSortChange(option) }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = when (option) {
                                        SortOption.TITLE -> "İsim"
                                        SortOption.SCORE -> "Puan"
                                        SortOption.PROGRESS -> "İlerleme"
                                        SortOption.STATUS -> "Durum"
                                        SortOption.ID -> "ID"
                                    },
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface
                                )

                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Apply button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Uygula")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        private fun StatsBottomSheet(
            stats: Map<String, Any>,
            onDismiss: () -> Unit
        ) {
            ModalBottomSheet(
                onDismissRequest = onDismiss,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "İstatistikler",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Genel istatistikler
                    StatSection(
                        title = "Genel",
                        items = listOf(
                            "Toplam Anime" to stats["total"].toString(),
                            "Seçili Anime" to stats["selectedCount"].toString(),
                            "Toplam Bölüm" to stats["totalEpisodes"].toString()
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Durum bazlı
                    StatSection(
                        title = "Durum Bazlı",
                        items = listOf(
                            "İzleniyor" to stats["watching"].toString(),
                            "Tamamlandı" to stats["completed"].toString(),
                            "İzlenecek" to stats["planToWatch"].toString(),
                            "Beklemede" to stats["onHold"].toString(),
                            "Bırakıldı" to stats["dropped"].toString()
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Puan istatistikleri
                    StatSection(
                        title = "Puanlar",
                        items = listOf(
                            "Ortalama Puan" to String.format("%.2f", stats["averageScore"])
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Kapat")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        @Composable
        private fun StatSection(
            title: String,
            items: List<Pair<String, String>>
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    items.forEach { (label, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        @Composable
        private fun EmptyStateView(
            hasSearchQuery: Boolean,
            hasFilter: Boolean
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        if (hasSearchQuery || hasFilter) Icons.Default.SearchOff
                        else Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )

                    Text(
                        text = when {
                            hasSearchQuery -> "Aramanızla eşleşen anime bulunamadı"
                            hasFilter -> "Bu filtreye uygun anime bulunamadı"
                            else -> "Listelenecek anime bulunamadı"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        @Composable
        private fun SearchBar(
            query: String,
            onQueryChange: (String) -> Unit
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Anime ara...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = if (query.isNotBlank()) {
                    {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                } else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )
        }

        @Composable
        private fun AnimeCard(
            anime: AnimeItem,
            onToggleSelect: (AnimeItem) -> Unit
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                colors = CardDefaults.cardColors(
                    containerColor = if (anime.isSelected)
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (anime.isSelected) 4.dp else 2.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Select Checkbox
                    Checkbox(
                        checked = anime.isSelected,
                        onCheckedChange = { onToggleSelect(anime) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF4CAF50)
                        )
                    )

                    // Progress Box
                    ProgressBox(anime = anime)

                    // Info
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = anime.displayTitle,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            style = MaterialTheme.typography.titleMedium
                        )

                        // Chips Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Status Chip
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = anime.displayStatus,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Score Chip
                            if (anime.raw.my_score > 0) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFFFC107).copy(alpha = 0.1f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = Color(0xFFFFC107)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = anime.displayScore,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFFFC107)
                                        )
                                    }
                                }
                            }
                        }

                        // Type and ID
                        Text(
                            text = "${anime.raw.series_type} • ID: ${anime.raw.series_animedb_id}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        @Composable
        private fun ProgressBox(anime: AnimeItem) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when {
                            anime.raw.my_status == "Completed" -> Color(0xFF4CAF50)
                            anime.raw.my_status == "Watching" -> Color(0xFF2196F3)
                            anime.raw.my_status == "Plan to Watch" -> Color(0xFF9E9E9E)
                            else -> Color(0xFFFF9800)
                        }
                    )
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = anime.progress.split("/")[0],
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "/${anime.progress.split("/")[1]}",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }

    // FlowRow composable for filter chips
    @Composable
    fun FlowRow(
        modifier: Modifier = Modifier,
        horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
        verticalArrangement: Arrangement.Vertical = Arrangement.Top,
        content: @Composable () -> Unit
    ) {
        Layout(
            content = content,
            modifier = modifier
        ) { measurables, constraints ->
            val layoutWidth = constraints.maxWidth
            var currentRowWidth = 0
            var currentRowHeight = 0
            val rows = mutableListOf<Pair<Int, List<Placeable>>>()
            var currentRow = mutableListOf<Placeable>()

            val placeables = measurables.map { it.measure(constraints) }

            placeables.forEach { placeable ->
                val itemWidth = placeable.width
                if (currentRowWidth + itemWidth <= layoutWidth) {
                    currentRow.add(placeable)
                    currentRowWidth += itemWidth
                    currentRowHeight = maxOf(currentRowHeight, placeable.height)
                } else {
                    rows.add(currentRowHeight to currentRow.toList())
                    currentRow = mutableListOf(placeable)
                    currentRowWidth = itemWidth
                    currentRowHeight = placeable.height
                }
            }

            if (currentRow.isNotEmpty()) {
                rows.add(currentRowHeight to currentRow.toList())
            }

            val totalHeight = rows.sumOf { it.first } + (rows.size - 1) * verticalArrangement.spacing.roundToPx()

            layout(layoutWidth, totalHeight) {
                var yPosition = 0
                rows.forEach { (rowHeight, rowPlaceables) ->
                    var xPosition = 0
                    rowPlaceables.forEach { placeable ->
                        placeable.place(xPosition, yPosition)
                        xPosition += placeable.width + horizontalArrangement.spacing.roundToPx()
                    }
                    yPosition += rowHeight + verticalArrangement.spacing.roundToPx()
                }
            }
        }
    }

    enum class SortOption {
        TITLE, SCORE, PROGRESS, STATUS, ID
    }
}

// Extension for spacing
//private val Arrangement.Horizontal.spacing: Dp
//    get() = when (this) {
//        is Arrangement.SpacedAligned -> spacing
//        else -> 0.dp
//    }
//
//private val Arrangement.Vertical.spacing: Dp
//    get() = when (this) {
//        is Arrangement.SpacedAligned -> spacing
//        else -> 0.dp
//    }