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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailyflow.DailyFlowApplication
import com.dailyflow.ui.theme.BackgroundApp
import com.dailyflow.ui.theme.PrimaryPink
import com.dailyflow.ui.theme.PrimaryPinkButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormScreen(
    categoryId: Int,
    onNavigateBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val application = context.applicationContext as DailyFlowApplication
    val factory = CategoryFormViewModelFactory(
        application.categoryRepository,
        categoryId
    )
    val viewModel: CategoryFormViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    val availableIcons = listOf(
        "label" to Icons.Filled.Label,
        "star" to Icons.Filled.Star,
        "flag" to Icons.Filled.Flag,
        "bookmark" to Icons.Filled.Bookmark,
        "tag" to Icons.Filled.Tag,
        "favorite" to Icons.Filled.Favorite
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundApp,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditMode) "Editar categoría" else "Nueva categoría"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Nombre
                Text(
                    text = "Nombre",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.onNameChange(it) },
                    placeholder = { Text("Ej. Trabajo") },
                    isError = uiState.nameError != null,
                    supportingText = if (uiState.nameError != null) {
                        { Text(uiState.nameError ?: "") }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryPink,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Icono
                Text(
                    text = "Icono",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Icon grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    availableIcons.forEach { (iconName, icon) ->
                        IconSelectorItem(
                            icon = icon,
                            isSelected = uiState.selectedIcon == iconName,
                            onClick = { viewModel.onIconChange(iconName) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Color
                Text(
                    text = "Color",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Color picker row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val colors = listOf(
                        Color(0xFFF06292), // Pink
                        Color(0xFF6200EE), // Purple
                        Color(0xFF2196F3), // Blue
                        Color(0xFF4CAF50), // Green
                        Color(0xFFFF9800), // Orange
                        Color(0xFFF44336)  // Red
                    )

                    colors.forEach { color ->
                        ColorSelectorItem(
                            color = color,
                            isSelected = uiState.selectedColor == color.hashCode(),
                            onClick = { viewModel.onColorChange(color.hashCode()) }
                        )
                    }
                }

                // Preview
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Vista previa",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val previewColor = Color(uiState.selectedColor)
                        val previewIcon = availableIcons.firstOrNull { it.first == uiState.selectedIcon }?.second
                            ?: Icons.Filled.Label

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = previewColor.copy(alpha = 0.2f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = previewIcon,
                                contentDescription = null,
                                tint = previewColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.size(16.dp))

                        Text(
                            text = uiState.name.ifEmpty { "Nombre de categoría" },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Botón guardar
            Button(
                onClick = { viewModel.onSaveCategory(onNavigateBack) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                shape = CircleShape,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PrimaryPinkButton)
            ) {
                Text(
                    text = "Guardar categoría",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun IconSelectorItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        PrimaryPink.copy(alpha = 0.2f)
    } else {
        Color(0xFFF0F0F0)
    }

    val iconColor = if (isSelected) {
        PrimaryPink
    } else {
        Color.Gray
    }

    Box(
        modifier = Modifier
            .size(56.dp)
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun ColorSelectorItem(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = color,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
