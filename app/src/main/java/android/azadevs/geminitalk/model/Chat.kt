package android.azadevs.geminitalk.model

import android.graphics.Bitmap

/**
 * Created by : Azamat Kalmurzaev
 * 20/10/24
 */
data class Chat(
    val prompt: String,
    val bitmap: Bitmap?,
    val isUser: Boolean
)
