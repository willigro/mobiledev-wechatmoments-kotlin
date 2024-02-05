package com.tws.moments.ui.createtweet.shared

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tws.moments.core.file.FileResolver
import com.tws.moments.core.file.utils.toBitmapExif
import com.tws.moments.core.tracker.track
import com.tws.moments.datasource.usecase.MomentsUseCase
import com.tws.moments.datasource.usecase.helpers.IDispatcher
import com.tws.moments.ui.createtweet.shared.ui.CreateTweetNavigationEvent
import com.tws.moments.ui.createtweet.shared.ui.CreateTweetUiEvent
import com.tws.moments.ui.createtweet.shared.ui.CreateTweetUiState
import com.tws.moments.ui.navigation.AppNavigator
import com.tws.moments.ui.navigation.ScreensNavigation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ExperimentalGetImage
@HiltViewModel
class CreateTweetViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val fileResolver: FileResolver,
    private val momentsUseCase: MomentsUseCase,
    private val iDispatcher: IDispatcher,
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

            CreateTweetNavigationEvent.Closes -> {
                appNavigator.navigateTo(
                    route = ScreensNavigation.Main.destination,
                    inclusive = true,
                )
            }
        }
    }

    fun onEvent(event: CreateTweetUiEvent) {
        when (event) {
            is CreateTweetUiEvent.TakePicture -> {
                takePicture(event.imageCapture)
            }

            is CreateTweetUiEvent.SavePicture -> {
                savePicture(event)
            }
        }
    }

    private fun takePicture(imageCapture: ImageCapture) = viewModelScope.launch {
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
                    track(it)
                },
            )
        }
    }

    private fun savePicture(event: CreateTweetUiEvent.SavePicture) = viewModelScope.launch {
        withContext(iDispatcher.dispatcherIO()) {
            uiState.value.bitmapExif?.let { bitmapExif ->
                fileResolver.saveInternalFile(bitmapExif, "testing")
            }?.also { result ->
                track(result)

                momentsUseCase.createTweet(
                    event.content,
                    result,
                ).collectLatest { resultUC ->
                    if (resultUC.isSuccess) {
                        onNavigationEvent(CreateTweetNavigationEvent.Closes)
                    }
                }
            }
        }
    }
}