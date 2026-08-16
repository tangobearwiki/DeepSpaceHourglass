package com.deepspace.hourglass.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class ReminderType {
    COUNTDOWN,
    COUNT_UP
}

@Entity(tableName = "reminders")
data class ReminderItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val date: LocalDate,
    val type: ReminderType = ReminderType.COUNTDOWN,
    val notes: String = "",
    val accentColor: Int = 0xFF76E4F7.toInt(),
    val isPinned: Boolean = false
)
