package com.seloreis.kurozora.di

import com.russhwolf.settings.Settings
import com.seloreis.kurozora.core.settings.AccountManager
import com.seloreis.kurozora.core.settings.SettingsManager
import com.seloreis.kurozora.getPlatform
import com.seloreis.kurozora.ui.screens.airseason.AirSeasonViewModel
import com.seloreis.kurozora.ui.screens.detail.CharacterDetailViewModel
import com.seloreis.kurozora.ui.screens.detail.EpisodeDetailViewModel
import com.seloreis.kurozora.ui.screens.detail.GameDetailViewModel
import com.seloreis.kurozora.ui.screens.detail.LiteratureDetailViewModel
import com.seloreis.kurozora.ui.screens.detail.PersonDetailViewModel
import com.seloreis.kurozora.ui.screens.detail.ShowDetailViewModel
import com.seloreis.kurozora.ui.screens.detail.SongDetailViewModel
import com.seloreis.kurozora.ui.screens.detail.StudioDetailViewModel
import com.seloreis.kurozora.ui.screens.detail.season.SeasonDetailViewModel
import com.seloreis.kurozora.ui.screens.explore.ExploreViewModel
import com.seloreis.kurozora.ui.screens.favorite.FavoriteViewModel
import com.seloreis.kurozora.ui.screens.feed.FeedViewModel
import com.seloreis.kurozora.ui.screens.library.LibraryViewModel
import com.seloreis.kurozora.ui.screens.list.ItemListViewModel
import com.seloreis.kurozora.ui.screens.list.show.RelatedShowsViewModel
import com.seloreis.kurozora.ui.screens.auth.AuthViewModel
import com.seloreis.kurozora.ui.screens.main.MainViewModel
import com.seloreis.kurozora.ui.screens.profile.ProfileViewModel
import com.seloreis.kurozora.ui.screens.reminder.ReminderViewModel
import com.seloreis.kurozora.ui.screens.schedule.ScheduleViewModel
import com.seloreis.kurozora.ui.screens.search.SearchViewModel
import com.seloreis.kurozora.ui.screens.search.filters.CharacterFilterViewModel
import com.seloreis.kurozora.ui.screens.search.filters.EpisodeFilterViewModel
import com.seloreis.kurozora.ui.screens.search.filters.GameFilterViewModel
import com.seloreis.kurozora.ui.screens.search.filters.LiteratureFilterViewModel
import com.seloreis.kurozora.ui.screens.search.filters.PersonFilterViewModel
import com.seloreis.kurozora.ui.screens.search.filters.ShowFilterViewModel
import com.seloreis.kurozora.ui.screens.search.filters.StudioFilterViewModel
import kurozorakit.api.AccountUser
import kurozorakit.api.TokenProvider
import kurozorakit.cache.CacheConfig
import kurozorakit.cache.CacheManager
import kurozorakit.cache.FileBasedCache
import kurozorakit.cache.InMemoryCache
import kurozorakit.core.KurozoraApi
import kurozorakit.core.KurozoraKit
import kurozorakit.data.models.search.filters.CharacterFilter
import kurozorakit.data.models.search.filters.EpisodeFilter
import kurozorakit.data.models.search.filters.GameFilter
import kurozorakit.data.models.search.filters.LiteratureFilter
import kurozorakit.data.models.search.filters.PersonFilter
import kurozorakit.data.models.search.filters.ShowFilter
import kurozorakit.data.models.search.filters.StudioFilter
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import java.io.File

object KurozoraTokenProvider : TokenProvider {
    val accountManager = GlobalContext.get().get<AccountManager>()

    override suspend fun saveToken(user: AccountUser) {
        accountManager.addAccount(user)
    }

    override suspend fun getToken(): String? {
        return accountManager.activeAccount.value?.token
    }
}

val inMemoryCache = InMemoryCache(
    name = "L1-Memory",
    config = CacheConfig(
        defaultTtlMillis = 300000, // 5 minutes
        maxEntries = 100
    )
)

val fileCache = FileBasedCache(
    name = "L2-Disk",
    cacheDir = File("./kurozora-cache"),
    config = CacheConfig(
        defaultTtlMillis = 300000, // 1 hour
        maxSize = 50 * 1024 * 1024 // 50 MB
    )
)

val cacheManager = CacheManager(
    caches = listOf(
        //fileCache,
        inMemoryCache,
    )
)

fun commonModule(enableNetworkLogs: Boolean) = module {
    single {
        KurozoraKit.Builder()
            .apiEndpoint(KurozoraApi.V1.baseUrl)
            .apiKey("api_key")
            .tokenProvider(KurozoraTokenProvider)
            .platform(getPlatform())
            //.cacheManager(cacheManager)
            .build()
    }

    single<Settings> { Settings() } // JVM’de in-memory veya file-backed
    single { SettingsManager(get()) }          // factory yok
    single { AccountManager(get()) }

    viewModel { MainViewModel(kit = get(), accountManager = get()) }
    viewModel { ExploreViewModel(kurozoraKit = get()) }
    viewModel { LibraryViewModel(kurozoraKit = get()) }
    viewModel { FeedViewModel(kurozoraKit = get()) }
    viewModel { ShowDetailViewModel(kurozoraKit = get()) }
    viewModel { LiteratureDetailViewModel(kurozoraKit = get()) }
    viewModel { GameDetailViewModel(kurozoraKit = get()) }
    viewModel { EpisodeDetailViewModel(kurozoraKit = get()) }
    viewModel { CharacterDetailViewModel(kurozoraKit = get()) }
    viewModel { PersonDetailViewModel(kurozoraKit = get()) }
    viewModel { StudioDetailViewModel(kurozoraKit = get()) }
    viewModel { SongDetailViewModel(kurozoraKit = get()) }
    viewModel { ItemListViewModel(kurozoraKit = get()) }
    viewModel { SeasonDetailViewModel(kurozoraKit = get()) }
    viewModel { RelatedShowsViewModel() }
    viewModel { AuthViewModel(kurozoraKit = get(), accountManager = get()) }
    viewModel { SearchViewModel(kurozoraKit = get()) }
    viewModel { ProfileViewModel(kurozoraKit = get()) }
    viewModel { FavoriteViewModel(kurozoraKit = get()) }
    viewModel { ReminderViewModel(kurozoraKit = get()) }
    viewModel { ScheduleViewModel(kurozoraKit = get()) }
    viewModel { AirSeasonViewModel(kurozoraKit = get()) }
    viewModel { (filter: ShowFilter?) ->
        ShowFilterViewModel(filter)
    }
    viewModel { (filter: LiteratureFilter?) ->
        LiteratureFilterViewModel(filter)
    }
    viewModel { (filter: GameFilter?) ->
        GameFilterViewModel(filter)
    }
    viewModel { (filter: CharacterFilter?) ->
        CharacterFilterViewModel(filter)
    }
    viewModel { (filter: EpisodeFilter?) ->
        EpisodeFilterViewModel(filter)
    }
    viewModel { (filter: PersonFilter?) ->
        PersonFilterViewModel(filter)
    }
    viewModel { (filter: StudioFilter?) ->
        StudioFilterViewModel(filter)
    }
}

expect fun platformModule(): Module
