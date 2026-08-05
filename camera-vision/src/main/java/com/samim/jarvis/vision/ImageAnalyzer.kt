package com.samim.jarvis.vision

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * ImageAnalyzer: lightweight stub that can be replaced with an on-device model or remote API call.
 */
class ImageAnalyzer : VisionManager {
    override suspend fun analyzeImage(bitmap: Bitmap): ImageAnalysisResult = withContext(Dispatchers.Default) {
        // Placeholder analysis — return empty result
        ImageAnalysisResult(text = null, labels = emptyList(), safe = true)
    }

    override suspend fun analyzeImageFromPath(path: String): ImageAnalysisResult = withContext(Dispatchers.Default) {
        // Placeholder: load image and analyze
        ImageAnalysisResult(text = null, labels = emptyList(), safe = true)
    }
}
