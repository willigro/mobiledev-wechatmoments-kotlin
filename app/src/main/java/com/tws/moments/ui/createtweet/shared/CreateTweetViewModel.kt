package com.tws.moments.ui.createtweet.shared

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tws.moments.core.file.BitmapExif
import com.tws.moments.core.file.FileResolver
import com.tws.moments.core.file.utils.toBitmapExif
import com.tws.moments.ui.createtweet.shared.ui.CreateTweetNavigationEvent
import com.tws.moments.ui.createtweet.shared.ui.CreateTweetUiState
import com.tws.moments.ui.navigation.AppNavigator
import com.tws.moments.ui.navigation.ScreensNavigation
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
    private val appNavigator: AppNavigator,
    private val fileResolver: FileResolver,
) : ViewModel() {

    private val _uiState: MutableStateFlow<CreateTweetUiState> = MutableStateFlow(
        CreateTweetUiState()
    )
    val uiState: StateFlow<CreateTweetUiState>
        get() = _uiState

    fun onNavigationEvent(event: CreateTweetNavigationEvent) = viewModelScope.launch {
        when (event) {
            CreateTweetNavigationEvent.Back -> {
                appNavigator.navigateBack()
            }

            CreateTweetNavigationEvent.ShowPicture -> {
                appNavigator.navigateTo(ScreensNavigation.CreateTweet.ShowPictureTweet.destination)
            }

            CreateTweetNavigationEvent.TakePicture -> {
                _uiState.update {
                    it.copy(bitmapExif = null)
                }

                appNavigator.navigateBack(ScreensNavigation.CreateTweet.TakeSinglePicture.destination)
            }

            CreateTweetNavigationEvent.SavePicture -> {
                appNavigator.navigateTo(ScreensNavigation.CreateTweet.SaveTweet.destination)
            }
        }
    }

    fun takePicture(imageCapture: ImageCapture) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            fileResolver.takePhoto(
                imageCapture = imageCapture,
                onImageCaptured = { imageProxy ->
                    _uiState.update {
                        it.copy(
                            bitmapExif = imageProxy.image?.toBitmapExif(),
                        )
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