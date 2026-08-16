package com.deepspace.hourglass.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.RemoteViews
import com.deepspace.hourglass.MainActivity
import com.deepspace.hourglass.R
import com.deepspace.hourglass.data.ReminderDatabase
import com.deepspace.hourglass.data.ReminderType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class CountdownWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        updateWidgets(context, appWidgetIds, appWidgetManager)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        for (id in appWidgetIds) WidgetConfigStore.deleteConfig(context, id)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_REFRESH,
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_USER_PRESENT -> {
                updateAllWidgets(context)
            }
        }
    }

    companion object {
        const val ACTION_REFRESH = "com.deepspace.hourglass.action.REFRESH_WIDGET"

        fun updateAllWidgets(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, CountdownWidgetProvider::class.java))
            if (ids.isNotEmpty()) updateWidgets(context, ids, manager)
        }

        private fun updateWidgets(context: Context, appWidgetIds: IntArray, appWidgetManager: AppWidgetManager) {
            CoroutineScope(Dispatchers.IO).launch {
                val dao = ReminderDatabase.getInstance(context).reminderDao()
                val reminders = dao.getAllRemindersList()
                val photoStorage = WidgetPhotoStorage(context)

                for (widgetId in appWidgetIds) {
                    val configuredId = WidgetConfigStore.getReminderId(context, widgetId)
                    val reminder = if (configuredId != -1) {
                        reminders.find { it.id == configuredId }
                    } else {
                        reminders.firstOrNull()
                    }

                    val views = RemoteViews(context.packageName, R.layout.widget_countdown)
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.widget_background)

                    if (reminder != null) {
                        val today = LocalDate.now()
                        val days = when (reminder.type) {
                            ReminderType.COUNTDOWN -> {
                                ChronoUnit.DAYS.between(today, reminder.date).toInt()
                            }
                            ReminderType.COUNT_UP -> {
                                ChronoUnit.DAYS.between(reminder.date, today).toInt().coerceAtLeast(0)
                            }
                        }
                        val label = when (reminder.type) {
                            ReminderType.COUNTDOWN -> if (days >= 0) "天后" else "天前"
                            ReminderType.COUNT_UP -> "天"
                        }
                        val accentColor = WidgetConfigStore.getAccentColor(context, widgetId)

                        views.setInt(R.id.widget_accent_bar, "setBackgroundColor", accentColor)
                        views.setTextViewText(R.id.widget_title, reminder.title)
                        views.setTextViewText(R.id.widget_days_value, days.toString())
                        views.setTextViewText(R.id.widget_days_label, label)
                        views.setTextViewText(R.id.widget_target_date, reminder.date.toString())
                        views.setTextColor(R.id.widget_title, Color.WHITE)
                        views.setTextColor(R.id.widget_days_value, Color.WHITE)
                        views.setTextColor(R.id.widget_days_label, 0xFFCAD5E2.toInt())
                        views.setTextColor(R.id.widget_target_date, 0xFF93A4B8.toInt())

                        // Background photo
                        val photoPaths = WidgetConfigStore.getPhotoPaths(context, widgetId)
                        val rotationHours = WidgetConfigStore.getRotationHours(context, widgetId)
                        val activePhoto = resolveActivePhoto(photoPaths, rotationHours)
                        val bitmap = activePhoto?.let { photoStorage.loadBitmap(it) }
                        if (bitmap != null) {
                            views.setViewVisibility(R.id.widget_bg_image, View.VISIBLE)
                            views.setImageViewBitmap(R.id.widget_bg_image, bitmap)
                            views.setInt(R.id.widget_overlay, "setBackgroundColor", 0x66000000)
                        } else {
                            views.setViewVisibility(R.id.widget_bg_image, View.GONE)
                            views.setInt(R.id.widget_overlay, "setBackgroundColor", Color.TRANSPARENT)
                        }

                        val intent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            putExtra("reminderId", reminder.id)
                        }
                        val pi = PendingIntent.getActivity(
                            context, reminder.id + 10000, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        views.setOnClickPendingIntent(R.id.widget_root, pi)
                    } else {
                        views.setTextViewText(R.id.widget_title, "暂无日程")
                        views.setTextViewText(R.id.widget_days_value, "0")
                        views.setTextViewText(R.id.widget_days_label, "天")
                        views.setTextViewText(R.id.widget_target_date, "——")
                        views.setViewVisibility(R.id.widget_bg_image, View.GONE)
                        views.setInt(R.id.widget_overlay, "setBackgroundColor", Color.TRANSPARENT)
                    }
                    appWidgetManager.updateAppWidget(widgetId, views)
                }
            }
        }

        private fun resolveActivePhoto(paths: List<String>, rotationHours: Int): String? {
            if (paths.isEmpty()) return null
            if (paths.size == 1) return paths.first()
            val window = rotationHours.coerceIn(1, 168)
            val epochHours = Instant.now().atZone(ZoneOffset.UTC).toEpochSecond() / 3600
            val idx = ((epochHours / window) % paths.size).toInt()
            return paths.getOrNull(idx)
        }
    }
}
