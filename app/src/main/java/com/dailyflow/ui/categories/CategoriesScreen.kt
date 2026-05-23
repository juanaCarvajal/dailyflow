package com.dailyflow.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailyflow.DailyFlowApplication
import com.dailyflow.domain.model.Category
import com.dailyflow.ui.theme.BackgroundApp
import com.dailyflow.ui.theme.PrimaryPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    onNavigateToCategoryForm: (Int) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val application = context.applicationContext as DailyFlowApplication
    val factory = CategoriesViewModelFactory(application.categoryRepository, application.taskRepository)
    val viewModel: CategoriesViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundApp,
        topBar = {
            TopAppBar(
                title = { Text("Categorías") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToCategoryForm(0) },
                containerColor = PrimaryPink
            ) {
                Icon(Icons.Default.Add, "Crear", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.categories.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No hay categorías",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Toca + para crear una",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.categories) { category ->
                        CategoryCard(
                            category = category,
                            onClick = { onNavigateToCategoryForm(category.id) },
                            onDelete = {
                                categoryToDelete = category
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }

        // Delete confirmation dialog
        if (showDeleteDialog && categoryToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    categoryToDelete = null
                },
                title = { Text("Eliminar categoría") },
                text = { Text("¿Estás seguro de que deseas eliminar '${categoryToDelete?.name}'?") },
                confirmButton = {
                    Button(
                        onClick = {
                            categoryToDelete?.let { viewModel.onDeleteCategory(it) }
                            showDeleteDialog = false
                            categoryToDelete = null
                        }
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            categoryToDelete = null
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val categoryColor = try {
        Color(android.graphics.Color.parseColor(category.colorHex))
    } catch (e: Exception) {
        Color(0xFF6200EE)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = categoryColor.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconForName(category.iconName),
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            // Category name
            Text(
                text = category.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            // Delete button
            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.Red
                )
            }
        }
    }
}

private fun getIconForName(iconName: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (iconName) {
        "label" -> Icons.Filled.Label
        "star" -> Icons.Filled.Star
        "flag" -> Icons.Filled.Flag
        "bookmark" -> Icons.Filled.Bookmark
        "tag" -> Icons.Filled.Tag
        else -> Icons.Filled.Label
    }
}
