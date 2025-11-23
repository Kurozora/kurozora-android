package app.kurozora.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit
import kurozorakit.data.enums.KKLibrary
import kurozorakit.shared.Result

class ProfileViewModel(
    private val kurozoraKit: KurozoraKit,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()
    fun fetchUserDetails(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = kurozoraKit.auth().getUserProfile(userId)
            val showsLibrary = kurozoraKit.auth().getUserLibrary(
                libraryKind = KKLibrary.Kind.SHOWS,
                userId = userId,
                libraryStatus = KKLibrary.Status.INPROGRESS,
            )
            val literaturesLibrary = kurozoraKit.auth().getUserLibrary(
                libraryKind = KKLibrary.Kind.LITERATURES,
                userId = userId,
                libraryStatus = KKLibrary.Status.INPROGRESS,
            )
            val gamesLibrary = kurozoraKit.auth().getUserLibrary(
                libraryKind = KKLibrary.Kind.GAMES,
                userId = userId,
                libraryStatus = KKLibrary.Status.INPROGRESS,
            )
            val favoriteShows = kurozoraKit.auth()
                .getUserFavorites(userId = userId, libraryKind = KKLibrary.Kind.SHOWS)
            val favoriteLiteratures = kurozoraKit.auth()
                .getUserFavorites(userId = userId, libraryKind = KKLibrary.Kind.LITERATURES)
            val favoriteGames = kurozoraKit.auth()
                .getUserFavorites(userId = userId, libraryKind = KKLibrary.Kind.GAMES)

            if (result is Result.Success) {
                val user = result.data.data.firstOrNull() ?: return@launch
                user.relationships

                _state.update {
                    it.copy(
                        user = user,
                        showsLibrary = showsLibrary.getOrNull()?.data?.shows ?: emptyList(),
                        literaturesLibrary = literaturesLibrary.getOrNull()?.data?.literatures
                            ?: emptyList(),
                        gamesLibrary = gamesLibrary.getOrNull()?.data?.games ?: emptyList(),
                        favoriteShows = favoriteShows.getOrNull()?.data?.shows ?: emptyList(),
                        favoriteLiteratures = favoriteLiteratures.getOrNull()?.data?.literatures
                            ?: emptyList(),
                        favoriteGames = favoriteGames.getOrNull()?.data?.games ?: emptyList(),
                        isLoading = false
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, errorMessage = result.toString()) }
            }
        }
    }

    fun followUser(userId: String) {
        viewModelScope.launch {
            kurozoraKit.auth().updateFollowStatus(userId).onSuccess { res ->
                _state.update { it.copy(followStatus = res.data.followStatus) }
            }
        }
    }
}
