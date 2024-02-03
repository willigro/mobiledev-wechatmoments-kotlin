package com.tws.moments.core.file

import android.net.Uri
import java.io.File

data class Image(
    val uri: Uri,
    val file: File,
)

fun File.makeImage() = Image(
    uri = Uri.fromFile(this),
    file = this,
)