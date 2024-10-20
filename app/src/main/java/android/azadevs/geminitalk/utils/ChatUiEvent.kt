package android.azadevs.geminitalk.utils

import android.graphics.Bitmap

/**
 * Created by : Azamat Kalmurzaev
 * 20/10/24
 */
sealed class ChatUiEvent {

    data class UpdatePrompt(val prompt: String) : ChatUiEvent()

    data class SendPrompt(val prompt: String, val bitmap: Bitmap? = null) : ChatUiEvent()

}