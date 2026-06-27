package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.example.data.AppDatabase
import com.example.data.HabitRepository
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.dashboard.DashboardViewModel
import com.example.ui.habit.AddHabitScreen
import com.example.ui.habit.AddHabitViewModel
import com.example.ui.navigation.Screen
import com.example.ui.profile.ProfileScreen
import com.example.ui.statistics.StatisticsScreen
import com.example.ui.theme.HabitTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = HabitRepository(database.habitDao())

        setContent {
            HabitTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HabitTrackerApp(repository)
                }
            }
        }
    }
}

@Composable
fun HabitTrackerApp(repository: HabitRepository) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            if (currentDestination?.hasRoute<Screen.Dashboard>() == true ||
                currentDestination?.hasRoute<Screen.Statistics>() == true ||
                currentDestination?.hasRoute<Screen.Profile>() == true
            ) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentDestination.hasRoute<Screen.Dashboard>(),
                        onClick = { navController.navigate(Screen.Dashboard) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                        label = { Text("Today") }
                    )
                    NavigationBarItem(
                        selected = currentDestination.hasRoute<Screen.Statistics>(),
                        onClick = { navController.navigate(Screen.Statistics) },
                        icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
                        label = { Text("Stats") }
                    )
                    NavigationBarItem(
                        selected = currentDestination.hasRoute<Screen.Profile>(),
                        onClick = { navController.navigate(Screen.Profile) },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Profile") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screen.Dashboard> {
                val viewModel: DashboardViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return DashboardViewModel(repository) as T
                        }
                    }
                )
                DashboardScreen(
                    viewModel = viewModel,
                    onAddHabit = { navController.navigate(Screen.AddHabit) },
                    onHabitClick = { /* Navigate to detail */ }
                )
            }

            composable<Screen.AddHabit> {
                val viewModel: AddHabitViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return AddHabitViewModel(repository) as T
                        }
                    }
                )
                AddHabitScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable<Screen.Statistics> {
                StatisticsScreen(onBack = { navController.popBackStack() })
            }

            composable<Screen.Profile> {
                ProfileScreen()
            }
        }
    }
}
