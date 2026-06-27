package com.example.ui.habit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Category
import com.example.data.Habit
import com.example.data.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddHabitUiState(
    val title: String = "",
    val description: String = "",
    val frequency: String = "Daily",
    val selectedColor: Int = 0xFF6200EE.toInt(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Int = 0,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

class AddHabitViewModel(private val repository: HabitRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AddHabitUiState())
    val uiState: StateFlow<AddHabitUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allCategories.collect { categories ->
                if (categories.isEmpty()) {
                    repository.seedCategories()
                }
                _uiState.value = _uiState.value.copy(categories = categories)
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _uiState.value = _uiState.value.copy(title = newTitle)
    }

    fun onDescriptionChange(newDesc: String) {
        _uiState.value = _uiState.value.copy(description = newDesc)
    }

    fun onFrequencyChange(newFreq: String) {
        _uiState.value = _uiState.value.copy(frequency = newFreq)
    }

    fun onCategorySelect(categoryId: Int) {
        val category = _uiState.value.categories.find { it.id == categoryId }
        _uiState.value = _uiState.value.copy(
            selectedCategoryId = categoryId,
            selectedColor = category?.color ?: _uiState.value.selectedColor
        )
    }

    fun saveHabit() {
        if (_uiState.value.title.isBlank()) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            val habit = Habit(
                title = _uiState.value.title,
                description = _uiState.value.description,
                frequency = _uiState.value.frequency,
                color = _uiState.value.selectedColor,
                categoryId = _uiState.value.selectedCategoryId
            )
            repository.addHabit(habit)
            _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
        }
    }
}
