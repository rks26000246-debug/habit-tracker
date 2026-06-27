package com.example.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    object Dashboard : Screen
    
    @Serializable
    data class HabitDetail(val habitId: Int) : Screen
    
    @Serializable
    object AddHabit : Screen
    
    @Serializable
    object Statistics : Screen
    
    @Serializable
    object Profile : Screen

    @Serializable
    object Onboarding : Screen
}
