package com.dailyflow.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dailyflow.data.local.dao.BlockDao
import com.dailyflow.data.local.dao.CategoryDao
import com.dailyflow.data.local.dao.TaskDao
import com.dailyflow.data.local.entities.BlockEntity
import com.dailyflow.data.local.entities.CategoryEntity
import com.dailyflow.data.local.entities.TaskEntity

@Database(
    entities = [TaskEntity::class, BlockEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun blockDao(): BlockDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dailyflow_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
