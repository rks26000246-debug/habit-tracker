package com.example.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Habit
import com.example.data.HabitCompletion
import com.example.data.HabitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class DashboardUiState(
    val habits: List<Habit> = emptyList(),
    val completionsToday: List<HabitCompletion> = emptyList(),
    val currentStreak: Int = 0,
    val completionPercentage: Float = 0f,
    val quote: String = "Believe you can and you're halfway there."
)

class DashboardViewModel(private val repository: HabitRepository) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.allHabits,
        repository.getCompletionsForToday()
    ) { habits, completions ->
        val percentage = if (habits.isNotEmpty()) {
            completions.size.toFloat() / habits.size.toFloat()
        } else 0f
        
        DashboardUiState(
            habits = habits,
            completionsToday = completions,
            completionPercentage = percentage,
            currentStreak = calculateStreak(habits) // Simplified for now
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    fun toggleHabit(habitId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            repository.toggleCompletion(habitId, calendar.timeInMillis, isCompleted)
        }
    }

    private fun calculateStreak(habits: List<Habit>): Int {
        // This would normally be more complex, checking historical completions
        // For now, let's just return a placeholder or simplified logic
        return 5 
    }
}
