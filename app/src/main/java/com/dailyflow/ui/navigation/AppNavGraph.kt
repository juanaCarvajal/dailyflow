package com.dailyflow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dailyflow.MainActivity
import com.dailyflow.ui.theme.DailyFlowTheme

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = "dashboard"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding
        composable("onboarding") {
            com.dailyflow.ui.onboarding.OnboardingScreen(
                onNavigateToFeatures = {
                    navController.navigate("features")
                }
            )
        }

        // Features
        composable("features") {
            val userName = com.dailyflow.ui.onboarding.OnboardingPreferences.getUserName(
                androidx.compose.ui.platform.LocalContext.current
            )
            com.dailyflow.ui.onboarding.FeaturesScreen(
                userName = userName,
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        // Dashboard
        composable("dashboard") {
            com.dailyflow.ui.dashboard.DashboardScreen(
                onNavigateToTaskList = {
                    navController.navigate("task_list")
                },
                onNavigateToSchedule = {
                    navController.navigate("schedule")
                },
                onNavigateToChooseType = {
                    navController.navigate("choose_type")
                },
                onNavigateToTaskForm = { taskId: Int -> // ✅ AGREGADO: Navegar a editar tarea desde dashboard
                    navController.navigate("task_form/$taskId")
                },
                onNavigateToBlockForm = { blockId: Int -> // ✅ AGREGADO: Navegar a editar bloque desde dashboard
                    navController.navigate("block_form/$blockId")
                }
            )
        }

        // Task List
        composable("task_list") {
            com.dailyflow.ui.tasks.TaskListScreen(
                onNavigateToTaskForm = { taskId: Int ->
                    navController.navigate("task_form/$taskId")
                },
                onNavigateToChooseType = {
                    navController.navigate("choose_type")
                },
                onNavigateBack = { // ✅ AGREGADO: Navegación atrás
                    navController.popBackStack()
                }
            )
        }

        // Task Form
        composable(
            route = "task_form/{taskId}",
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0
            com.dailyflow.ui.tasks.TaskFormScreen(
                taskId = taskId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDashboard = { // ✅ AGREGADO: Navegar al dashboard después de guardar
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }

        // Schedule
        composable("schedule") {
            com.dailyflow.ui.schedule.ScheduleScreen(
                onNavigateToBlockForm = { blockId: Int ->
                    navController.navigate("block_form/$blockId")
                },
                onNavigateBack = { // ✅ AGREGADO: Navegación atrás
                    navController.popBackStack()
                }
            )
        }

        // Block Form
        composable(
            route = "block_form/{blockId}",
            arguments = listOf(
                navArgument("blockId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val blockId = backStackEntry.arguments?.getInt("blockId") ?: 0
            com.dailyflow.ui.schedule.BlockFormScreen(
                blockId = blockId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Categories
        composable("categories") {
            com.dailyflow.ui.categories.CategoriesScreen(
                onNavigateToCategoryForm = { categoryId: Int ->
                    navController.navigate("category_form/$categoryId")
                }
            )
        }

        // Category Form
        composable(
            route = "category_form/{categoryId}",
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
            com.dailyflow.ui.categories.CategoryFormScreen(
                categoryId = categoryId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Choose Type
        composable("choose_type") {
            com.dailyflow.ui.tasks.ChooseTypeScreen(
                onNavigateToTaskForm = {
                    navController.navigate("task_form/0")
                },
                onNavigateToBlockForm = {
                    navController.navigate("block_form/0")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

// Pantalla placeholder temporal
@Composable
fun PlaceholderScreen(screenName: String) {
    androidx.compose.material3.Text(
        text = "Pantalla: $screenName\n(Pendiente de implementar)",
        style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
    )
}
