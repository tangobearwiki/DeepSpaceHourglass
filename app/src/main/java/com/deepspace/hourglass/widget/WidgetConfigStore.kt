package com.deepspace.hourglass.widget

import android.content.Context

object WidgetConfigStore {
    private const val PREFS = "hourglass_widget_prefs"

    fun saveReminderId(context: Context, widgetId: Int, reminderId: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putInt("widget_${widgetId}_reminder_id", reminderId).commit()
    }

    fun getReminderId(context: Context, widgetId: Int): Int {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt("widget_${widgetId}_reminder_id", -1)
    }

    fun savePhotoPaths(context: Context, widgetId: Int, paths: List<String>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("widget_${widgetId}_photos", paths.joinToString("\u001F")).commit()
    }

    fun getPhotoPaths(context: Context, widgetId: Int): List<String> {
        val s = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString("widget_${widgetId}_photos", "") ?: ""
        return if (s.isEmpty()) emptyList() else s.split("\u001F").filter { it.isNotBlank() }
    }

    fun saveAccentColor(context: Context, widgetId: Int, color: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putInt("widget_${widgetId}_accent", color).commit()
    }

    fun getAccentColor(context: Context, widgetId: Int): Int {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt("widget_${widgetId}_accent", 0xFF76E4F7.toInt())
    }

    fun saveRotationHours(context: Context, widgetId: Int, hours: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putInt("widget_${widgetId}_rotation", hours.coerceIn(1, 168)).commit()
    }

    fun getRotationHours(context: Context, widgetId: Int): Int {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt("widget_${widgetId}_rotation", 24)
    }

    fun deleteConfig(context: Context, widgetId: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .remove("widget_${widgetId}_reminder_id")
            .remove("widget_${widgetId}_photos")
            .remove("widget_${widgetId}_accent")
            .remove("widget_${widgetId}_rotation")
            .commit()
    }
}
