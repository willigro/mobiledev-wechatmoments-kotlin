package com.tws.moments.ui.createtweet.shared

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tws.moments.core.file.BitmapExif
import com.tws.moments.core.file.FileResolver
import com.tws.moments.core.file.utils.toBitmapExif
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ExperimentalGetImage
@HiltViewModel
class CreateTweetViewModel @Inject constructor(
    private val fileResolver: FileResolver,
) : ViewModel() {

    private val _uiState: MutableStateFlow<BitmapExif?> = MutableStateFlow(null)
    val uiState: StateFlow<BitmapExif?>
        get() = _uiState

    fun takePicture(imageCapture: ImageCapture) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            fileResolver.takePhoto(
                imageCapture = imageCapture,
                onImageCaptured = { imageProxy ->
                    _uiState.update {
                        imageProxy.image?.toBitmapExif()
                    }
                },
                onError = {
                    com.tws.moments.core.tracker.track(it)
                },
            )
        }
    }

    fun savePicture(bitmapExif: BitmapExif) {
        val result = bitmapExif.let { it1 -> fileResolver.saveInternalFile(it1, "testing") }

        com.tws.moments.core.tracker.track(result)
    }
}