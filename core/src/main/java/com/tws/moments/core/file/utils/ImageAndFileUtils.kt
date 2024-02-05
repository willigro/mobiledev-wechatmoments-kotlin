package com.tws.moments.core.file.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import androidx.exifinterface.media.ExifInterface
import com.tws.moments.core.file.BitmapExif
import com.tws.moments.core.file.Exif
import com.tws.moments.core.file.INTERNAL_DIRECTORY
import com.tws.moments.core.tracker.track
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale

fun Image.toBitmapExif(): BitmapExif? {
    val buffer: ByteBuffer = planes[0].buffer
    return try {
        val bytes = ByteArray(buffer.capacity())

        track("Image=$this, buffer=$buffer, bytes=${bytes.size}")

        buffer.get(bytes)

        val exif = ExifInterface(ByteArrayInputStream(bytes))

        val bitmap = Exif.fixBitmapOrientation(
            exif,
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null),
        )

        BitmapExif(bitmap, exif)
    } catch (e: Exception) {
        track(e)
        null
    } finally {
        // Without it, the pos is not going to be 0, and then it will break
        // due to BufferUnderflowException
        buffer.clear()
    }
}

fun generateInternalFileToSave(context: Context, name: String): File {
    val cw = ContextWrapper(context)

    val directory = cw.getDir(INTERNAL_DIRECTORY, Context.MODE_PRIVATE)

    return File(
        directory,
        generateFileName(name),
    )
}

fun generateFileName(name: String): String =
    if (name.isEmpty()) {
        SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss-SSS",
            Locale.US,
        ).format(System.currentTimeMillis()) + ".jpeg"
    } else {
        val n = if (name.contains(".")) {
            name.split(".")[0]
        } else {
            name
        }

        "$n.jpeg"
    }

fun Bitmap.saveTo(file: File): String? {
    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(file)

        this.compress(Bitmap.CompressFormat.JPEG, 100, fos)
    } catch (e: Exception) {
        track(e)
    } finally {
        try {
            fos?.close()
        } catch (e: IOException) {
            track(e)
        }
    }
    return file.absolutePath
}