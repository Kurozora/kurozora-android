package app.kurozora.ui.screens.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kurozorakit.core.KurozoraKit
import kurozorakit.data.enums.KKScheduleType
import kurozorakit.shared.Result

class ScheduleViewModel(
    private val kurozoraKit: KurozoraKit,
) : ViewModel() {
    private val _state = MutableStateFlow(ScheduleState())
    val state: StateFlow<ScheduleState> = _state.asStateFlow()

    init {
        fetchSchedule()
    }

    fun fetchSchedule(date: String = "") {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = kurozoraKit.schedule().getSchedule(
                type = KKScheduleType.shows,
                date = date
            )

            if (result is Result.Success) {
                val schedule = result.data.data.firstOrNull() ?: return@launch
                val relationships = schedule.relationships

                _state.update {
                    it.copy(
                        shows = relationships.shows?.data ?: emptyList(),
                        games = relationships.games?.data ?: emptyList(),
                        literatures = relationships.literatures?.data ?: emptyList(),
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

    fun fetchScheduleForDay(day: DayOfWeek) {
        viewModelScope.launch {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val todayDow = today.dayOfWeek
            // Bugünden seçilen güne kadar fark
            val deltaDays = (day.ordinal - todayDow.ordinal + 7) % 7
            val targetDate = today.plus(DatePeriod(days = deltaDays))
            // Format YYYY-MM-DD
            val dateStr = "%04d-%02d-%02d".format(
                targetDate.year, targetDate.monthNumber, targetDate.dayOfMonth
            )

            fetchSchedule(dateStr)
        }
    }
}
