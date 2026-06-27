package com.example.data

import kotlinx.coroutines.flow.Flow
import java.util.*

class HabitRepository(private val habitDao: HabitDao) {
    val allHabits: Flow<List<Habit>> = habitDao.getAllActiveHabits()
    val allCategories: Flow<List<Category>> = habitDao.getAllCategories()

    suspend fun getHabitById(id: Int): Habit? = habitDao.getHabitById(id)

    suspend fun addHabit(habit: Habit) = habitDao.insertHabit(habit)

    suspend fun updateHabit(habit: Habit) = habitDao.updateHabit(habit)

    suspend fun deleteHabit(habit: Habit) = habitDao.deleteHabit(habit)

    fun getCompletionsForHabit(habitId: Int): Flow<List<HabitCompletion>> =
        habitDao.getCompletionsForHabit(habitId)

    fun getCompletionsForToday(): Flow<List<HabitCompletion>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endOfDay = calendar.timeInMillis
        
        return habitDao.getCompletionsInRange(startOfDay, endOfDay)
    }

    suspend fun toggleCompletion(habitId: Int, date: Long, isCompleted: Boolean) {
        if (isCompleted) {
            habitDao.insertCompletion(HabitCompletion(habitId = habitId, completedDate = date))
        } else {
            habitDao.deleteCompletion(habitId, date)
        }
    }

    suspend fun seedCategories() {
        val defaultCategories = listOf(
            Category(name = "Health", icon = "Favorite", color = 0xFFF44336.toInt()),
            Category(name = "Fitness", icon = "FitnessCenter", color = 0xFF4CAF50.toInt()),
            Category(name = "Work", icon = "Work", color = 0xFF2196F3.toInt()),
            Category(name = "Personal", icon = "Person", color = 0xFFFFC107.toInt())
        )
        defaultCategories.forEach { habitDao.insertCategory(it) }
    }
}
