package com.dailyflow.data.repository

import com.dailyflow.data.local.dao.CategoryDao
import com.dailyflow.data.local.entities.toDomain
import com.dailyflow.domain.model.Category
import com.dailyflow.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepository(private val categoryDao: CategoryDao) {
    fun getAllCategories(): Flow<List<Category>> =
        categoryDao.getAllCategories().map { entities -> entities.map { entity -> entity.toDomain() } }

    suspend fun getCategoryById(id: Int): Category? =
        categoryDao.getCategoryById(id)?.toDomain()

    suspend fun insertCategory(category: Category) = categoryDao.insertCategory(category.toEntity())
    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category.toEntity())
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category.toEntity())
}
