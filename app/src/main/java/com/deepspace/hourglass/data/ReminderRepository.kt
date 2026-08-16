package com.deepspace.hourglass.data

import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val dao: ReminderDao) {
    val allReminders: Flow<List<ReminderItem>> = dao.getAllReminders()
    suspend fun getAllRemindersList(): List<ReminderItem> = dao.getAllRemindersList()
    suspend fun getById(id: Int): ReminderItem? = dao.getById(id)
    suspend fun insert(reminder: ReminderItem): Long = dao.insert(reminder)
    suspend fun update(reminder: ReminderItem) = dao.update(reminder)
    suspend fun delete(reminder: ReminderItem) = dao.delete(reminder)
}
