package com.tws.moments.core.file

import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import com.tws.moments.core.file.utils.generateInternalFileToSave
import com.tws.moments.core.file.utils.saveTo
import java.util.concurrent.ExecutorService


const val INTERNAL_DIRECTORY = "imageDir"

class FileResolver(
    private val context: Context,
    private val executorService: ExecutorService,
) {
    fun saveInternalFile(bitmapExif: BitmapExif, name: String): Image {
        val file = generateInternalFileToSave(context, name)

        val path = bitmapExif.bitmap?.saveTo(file)

        Exif.saveExif(bitmapExif.exifInterface, path)

        return file.makeImage()
    }

    fun takePhoto(
        imageCapture: ImageCapture,
        onImageCaptured: (ImageProxy) -> Unit,
        onError: (ImageCaptureException) -> Unit,
    ) {
        imageCapture.takePicture(
            executorService,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                }

                override fun onCaptureSuccess(image: ImageProxy) {
                    onImageCaptured(image)
                }
            }
        )
    }
}