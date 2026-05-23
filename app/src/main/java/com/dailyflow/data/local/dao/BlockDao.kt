package com.dailyflow.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dailyflow.data.local.entities.BlockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockDao {
    @Query("SELECT * FROM blocks ORDER BY startTime ASC")
    fun getAllBlocks(): Flow<List<BlockEntity>>

    @Query("SELECT * FROM blocks WHERE id = :id")
    suspend fun getBlockById(id: Int): BlockEntity?

    @Query("SELECT * FROM blocks WHERE dayOfWeek = :dayOfWeek ORDER BY startTime ASC")
    fun getBlocksForDay(dayOfWeek: Int): Flow<List<BlockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlock(block: BlockEntity): Long

    @Update
    suspend fun updateBlock(block: BlockEntity)

    @Delete
    suspend fun deleteBlock(block: BlockEntity)
}
