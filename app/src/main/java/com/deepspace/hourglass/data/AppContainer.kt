package com.deepspace.hourglass.data

import android.content.Context

class AppContainer(context: Context) {
    private val database = ReminderDatabase.getInstance(context)
    val reminderRepository = ReminderRepository(database.reminderDao())
}
