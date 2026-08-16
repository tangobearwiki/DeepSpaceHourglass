package com.deepspace.hourglass.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY isPinned DESC, dateEpochDay ASC")
    fun getAllReminders(): Flow<List<ReminderItem>>

    @Query("SELECT * FROM reminders ORDER BY isPinned DESC, dateEpochDay ASC")
    suspend fun getAllRemindersList(): List<ReminderItem>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: Int): ReminderItem?

    @Insert
    suspend fun insert(reminder: ReminderItem): Long

    @Update
    suspend fun update(reminder: ReminderItem)

    @Delete
    suspend fun delete(reminder: ReminderItem)
}
