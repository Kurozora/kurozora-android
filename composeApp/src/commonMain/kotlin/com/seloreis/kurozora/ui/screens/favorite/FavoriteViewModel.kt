package com.seloreis.kurozora.ui.screens.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seloreis.kurozora.ui.screens.library.LibraryTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit
import kurozorakit.data.enums.KKLibrary
import kurozorakit.shared.Result

class FavoriteViewModel(
    private val kurozoraKit: KurozoraKit
) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteState())
    val state: StateFlow<FavoriteState> = _state.asStateFlow()

    init {
        fetchFavorites()
    }

    fun selectTab(tab: LibraryTab) {
        _state.update { it.copy(selectedTab = tab, errorMessage = null) }
        fetchFavorites()
    }

    fun fetchFavorites() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val currentState = _state.value

            val result = kurozoraKit.user().getMyFavorites(libraryKind = currentState.selectedTab.kind)

            if (result is Result.Success) {
                val library = result.data.data

                _state.update {
                    it.copy(
                        shows = library.shows ?: emptyList(),
                        games = library.games ?: emptyList(),
                        literatures = library.literatures ?: emptyList(),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.toString()
                    )
                }
            }
        }
    }

}