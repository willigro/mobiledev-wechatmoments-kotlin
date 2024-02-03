package com.tws.moments.core.file

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import java.io.IOException
import java.io.OutputStream

data class BitmapExif(
    val bitmap: Bitmap?,
    val exifInterface: ExifInterface?,
)


fun BitmapExif?.saveTo(
    resolver: ContentResolver,
    contentValues: ContentValues,
): Uri {
    var stream: OutputStream? = null
    var uri: Uri? = null

    return try {
        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        uri = resolver.insert(contentUri, contentValues)

        if (uri == null) {
            throw IOException("Failed to create new MediaStore record.")
        }

        stream = resolver.openOutputStream(uri)

        if (stream == null) {
            throw IOException("Failed to get output stream.")
        }

        if (this?.bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream) == false) {
            throw IOException("Failed to save bitmap.")
        }

        uri
    } catch (e: IOException) {
        if (uri != null) {
            // Don't leave an orphan entry in the MediaStore
            resolver.delete(uri, null, null)
        }

        throw e
    } finally {
        stream?.close()
    }
}