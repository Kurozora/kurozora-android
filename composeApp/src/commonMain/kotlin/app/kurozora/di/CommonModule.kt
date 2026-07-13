package app.kurozora.di

import app.kurozora.BuildKonfig
import app.kurozora.core.settings.AccountManager
import app.kurozora.core.settings.SettingsManager
import app.kurozora.core.theme.DownloadedThemeManager
import app.kurozora.core.theme.ThemeDownloader
import app.kurozora.getPlatform
import app.kurozora.ui.screens.airseason.AirSeasonViewModel
import app.kurozora.ui.screens.auth.AuthViewModel
import app.kurozora.ui.screens.detail.CharacterDetailViewModel
import app.kurozora.ui.screens.detail.EpisodeDetailViewModel
import app.kurozora.ui.screens.detail.GameDetailViewModel
import app.kurozora.ui.screens.detail.LiteratureDetailViewModel
import app.kurozora.ui.screens.detail.PersonDetailViewModel
import app.kurozora.ui.screens.detail.ShowDetailViewModel
import app.kurozora.ui.screens.detail.SongDetailViewModel
import app.kurozora.ui.screens.detail.StudioDetailViewModel
import app.kurozora.ui.screens.detail.season.SeasonDetailViewModel
import app.kurozora.ui.screens.explore.ExploreViewModel
import app.kurozora.ui.screens.favorite.FavoriteViewModel
import app.kurozora.ui.screens.feed.FeedViewModel
import app.kurozora.ui.screens.library.LibraryViewModel
import app.kurozora.ui.screens.list.ItemListViewModel
import app.kurozora.ui.screens.main.MainViewModel
import app.kurozora.ui.screens.profile.ProfileViewModel
import app.kurozora.ui.screens.profile.settings.SettingsViewModel
import app.kurozora.ui.screens.recap.RecapItemViewModel
import app.kurozora.ui.screens.reminder.ReminderViewModel
import app.kurozora.ui.screens.schedule.ScheduleViewModel
import app.kurozora.ui.screens.search.SearchViewModel
import app.kurozora.ui.screens.search.filters.CharacterFilterViewModel
import app.kurozora.ui.screens.search.filters.EpisodeFilterViewModel
import app.kurozora.ui.screens.search.filters.GameFilterViewModel
import app.kurozora.ui.screens.search.filters.LiteratureFilterViewModel
import app.kurozora.ui.screens.search.filters.PersonFilterViewModel
import app.kurozora.ui.screens.search.filters.ShowFilterViewModel
import app.kurozora.ui.screens.search.filters.StudioFilterViewModel
import com.russhwolf.settings.Settings
import kurozorakit.api.AccountUser
import kurozorakit.api.TokenProvider
import kurozorakit.core.KurozoraApi
import kurozorakit.core.KurozoraKit
import kurozorakit.data.models.search.filters.CharacterFilter
import kurozorakit.data.models.search.filters.EpisodeFilter
import kurozorakit.data.models.search.filters.GameFilter
import kurozorakit.data.models.search.filters.LiteratureFilter
import kurozorakit.data.models.search.filters.PersonFilter
import kurozorakit.data.models.search.filters.ShowFilter
import kurozorakit.data.models.search.filters.StudioFilter
import kurozorakit.shared.UserAgent
import kurozorakit.shared.logging.KurozoraLogger
import kurozorakit.shared.logging.LogLevel
import kurozorakit.shared.logging.MemoryBufferSink
import kurozorakit.store.DatabaseDriverFactory
import kurozorakit.store.JvmDatabaseDriverFactory
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.module.Module
import org.koin.dsl.module
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

fun commonModule() = module {
    single { MemoryBufferSink(capacity = 1000) }

    single {
        val driverFactory: DatabaseDriverFactory = get()
        KurozoraKit.Builder()
            .apiEndpoint(KurozoraApi.V1.baseUrl)
            .apiKey(BuildKonfig.API_KEY)
            .tokenProvider(KurozoraTokenProvider)
            .platform(getPlatform())
            .userAgent(UserAgent(appName = "KtorClient", appVersion = "1.0.0", appID = "com.seloreis.kurozora", platformName = getPlatform().platform, platformVersion = getPlatform().platformVersion))
            .databaseDriverFactory(driverFactory)
            .logLevel(LogLevel.DEBUG)
            .customSink(get<MemoryBufferSink>())
            .build()
    }

    single<Settings> { Settings() }
    single { SettingsManager(get()) }
    single { AccountManager(get()) }
    single { DownloadedThemeManager(get()) }
    single { ThemeDownloader(get()) }

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
    viewModel { AuthViewModel(kurozoraKit = get(), accountManager = get()) }
    viewModel { SearchViewModel(kurozoraKit = get()) }
    viewModel { ProfileViewModel(kurozoraKit = get()) }
    viewModel { FavoriteViewModel(kurozoraKit = get()) }
    viewModel { ReminderViewModel(kurozoraKit = get()) }
    viewModel { ScheduleViewModel(kurozoraKit = get()) }
    viewModel { AirSeasonViewModel(kurozoraKit = get()) }
    viewModel { SettingsViewModel(kurozoraKit = get(), accountManager = get(), memoryBufferSink = get(), downloadedThemeManager = get(), themeDownloader = get()) }
    viewModel { RecapItemViewModel(kurozoraKit = get()) }

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
