package com.dailyflow.data.repository

import android.content.Context
import com.dailyflow.data.local.dao.BlockDao
import com.dailyflow.data.local.entities.toDomain
import com.dailyflow.domain.model.Block
import com.dailyflow.domain.model.toEntity
import com.dailyflow.notifications.NotificationScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BlockRepository(
    private val blockDao: BlockDao,
    private val context: Context? = null
) {
    private val notificationScheduler: NotificationScheduler? by lazy {
        context?.let { NotificationScheduler(it) }
    }

    fun getAllBlocks(): Flow<List<Block>> =
        blockDao.getAllBlocks().map { entities -> entities.map { entity -> entity.toDomain() } }

    suspend fun getBlockById(id: Int): Block? =
        blockDao.getBlockById(id)?.toDomain()

    fun getBlocksForDay(dayOfWeek: Int): Flow<List<Block>> =
        blockDao.getBlocksForDay(dayOfWeek).map { entities -> entities.map { entity -> entity.toDomain() } }

    suspend fun insertBlock(block: Block): Long {
        val blockId = blockDao.insertBlock(block.toEntity())
        notificationScheduler?.scheduleBlockNotification(block.copy(id = blockId.toInt()))
        return blockId
    }

    suspend fun updateBlock(block: Block) {
        blockDao.updateBlock(block.toEntity())
        notificationScheduler?.cancelBlockNotification(block.id)
        notificationScheduler?.scheduleBlockNotification(block)
    }

    suspend fun deleteBlock(block: Block) {
        blockDao.deleteBlock(block.toEntity())
        notificationScheduler?.cancelBlockNotification(block.id)
    }
}
