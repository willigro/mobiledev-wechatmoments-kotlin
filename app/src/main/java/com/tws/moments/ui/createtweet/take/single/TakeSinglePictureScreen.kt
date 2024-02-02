package com.tws.moments.ui.createtweet.take.single

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.tws.moments.designsystem.components.NavigationWrapper
import com.tws.moments.designsystem.theme.AppTheme
import com.tws.moments.ui.createtweet.shared.CreateTweetViewModel
import com.tws.moments.ui.createtweet.shared.ui.CreateTweetToolbar
import com.tws.moments.ui.navigation.ScreensNavigation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 *
 * Steps Single picture:
 *     - Take/Select picture
 *     - Add filter (for version 2)
 *     - Add content
 *         - Save
 *
 * */

@Composable
fun TakeSinglePictureScreenRoot(
    navigationWrapper: NavigationWrapper,
    viewModel: CreateTweetViewModel = hiltViewModel(),
) {
    TakeSinglePictureScreen(navigationWrapper = navigationWrapper)
}

@Composable
fun TakeSinglePictureScreen(navigationWrapper: NavigationWrapper) {
    Column(modifier = Modifier.fillMaxSize()) {
        CreateTweetToolbar(modifier = Modifier, navigationWrapper = navigationWrapper)
        Text(
            text = "Next",
            modifier = Modifier.clickable { navigationWrapper.navigate(ScreensNavigation.CreateTweet.SaveTweet.destination) }
        )
        CameraView()
    }
}


@Composable
fun CameraView() {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture,
        )

        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                previewView
            },
            modifier = Modifier.fillMaxSize(),
        )

        val clickOffset = remember {
            mutableStateOf<Circle?>(null)
        }

        val isInside = remember {
            mutableStateOf(false)
        }

        Canvas(
            modifier = Modifier
                .size(AppTheme.dimensions.takePicture.takePictureButtonSize)
                .padding(
                    bottom = AppTheme.dimensions.takePicture.takePictureButtonBottomPadding
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            clickOffset.value?.also { circle ->
                                if (circle.intersect(offset.x, offset.y)) {
                                    isInside.value = true
                                }
                            }
                        },
                        onTap = {
                            if (isInside.value) {
                                // viewModel.takePhoto(imageCapture = imageCapture)
                            }

                            isInside.value = false
                        }
                    )
                }
        ) {
            drawCircle(
                color = Color.White,
                center = center,
                style = Stroke(6f)
            )

            val radius = (size.minDimension / 2f) / 2f

            drawCircle(
                color = if (isInside.value) Color.Red else Color.White,
                radius = radius,
                center = center,
            )

            clickOffset.value = Circle(
                x = center.x,
                y = center.y,
                radius = radius,
            )
        }
    }
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            continuation.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
    }
}

class Circle(val x: Float, val y: Float, val radius: Float) {
    fun intersect(
        x: Float, y: Float
    ): Boolean {
        return (x - this.x) * (x - this.x) + (y - this.y) * (y - this.y) <= this.radius * this.radius
    }
}