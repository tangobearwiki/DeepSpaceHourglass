package com.deepspace.hourglass.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.math.max

class WidgetPhotoStorage(private val context: Context) {
    private val photoDir: File
        get() = File(context.filesDir, "widget-photos").apply { mkdirs() }

    suspend fun replacePhotos(uris: List<Uri>, existingPaths: List<String>): List<String> = withContext(Dispatchers.IO) {
        val cachedFiles = uris.take(20).mapNotNull { uri -> cacheUri(uri) }
        existingPaths.map(::File)
            .filter { old -> cachedFiles.none { it.absolutePath == old.absolutePath } }
            .forEach { it.delete() }
        cachedFiles.map { it.absolutePath }
    }

    fun loadBitmap(path: String, requestedWidth: Int = 1200, requestedHeight: Int = 1200): Bitmap? {
        val file = File(path)
        if (!file.exists()) return null
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, bounds)
        val sample = calculateInSampleSize(bounds, requestedWidth, requestedHeight)
        val options = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        return BitmapFactory.decodeFile(file.absolutePath, options)
    }

    private fun cacheUri(uri: Uri): File? {
        val ext = when (context.contentResolver.getType(uri)) {
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> "jpg"
        }
        val file = File(photoDir, "${UUID.randomUUID()}.$ext")
        return runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            } ?: return null
            file
        }.getOrNull()
    }

    private fun calculateInSampleSize(opts: BitmapFactory.Options, reqW: Int, reqH: Int): Int {
        val h = opts.outHeight
        val w = opts.outWidth
        var s = 1
        if (h > reqH || w > reqW) {
            var hh = h / 2
            var ww = w / 2
            while ((hh / s) >= reqH && (ww / s) >= reqW) s *= 2
        }
        return max(1, s)
    }
}
