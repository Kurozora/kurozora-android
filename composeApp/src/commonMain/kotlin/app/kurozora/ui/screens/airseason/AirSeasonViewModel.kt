package app.kurozora.ui.screens.airseason

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit
import kurozorakit.data.enums.SeasonOfYear
import kurozorakit.shared.Result

class AirSeasonViewModel(
    private val kurozoraKit: KurozoraKit,
) : ViewModel() {
    private val _state = MutableStateFlow(AirSeasonState())
    val state: StateFlow<AirSeasonState> = _state.asStateFlow()
    fun selectTab(tab: SeasonTab) {
        _state.update {
            it.copy(
                selectedTab = tab,
                tabs = getSeasonTabs(tab)
            )
        }
        fetchSeason(tab.year.toString(), tab.season)
    }

    fun fetchSeason(year: String, season: SeasonOfYear) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = kurozoraKit.airSeason().getAnimeSeason(year, season)

            if (result is Result.Success) {
                _state.update {
                    it.copy(
                        shows = result.data.data.firstOrNull()?.relationships?.shows?.data
                            ?: emptyList(),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        shows = emptyList(),
                        isLoading = false,
                        errorMessage = result.toString()
                    )
                }
            }
        }
    }
}
