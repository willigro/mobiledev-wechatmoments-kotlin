package com.tws.moments.core.file

import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface

data class BitmapExif(
    val bitmap: Bitmap?,
    val exifInterface: ExifInterface?,
)