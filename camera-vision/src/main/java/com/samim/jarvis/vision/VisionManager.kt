package com.samim.jarvis.vision

import android.graphics.Bitmap

/**
 * VisionManager: top-level interface for image/camera analysis features.
 * Implementations can provide real vision models or call remote vision APIs.
 */
interface VisionManager {
    suspend fun analyzeImage(bitmap: Bitmap): ImageAnalysisResult
    suspend fun analyzeImageFromPath(path: String): ImageAnalysisResult
}

data class ImageAnalysisResult(
    val text: String?,
    val labels: List<String> = emptyList(),
    val safe: Boolean = true
)
