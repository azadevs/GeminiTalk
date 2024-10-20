package android.azadevs.geminitalk.utils

import android.azadevs.geminitalk.model.Chat
import android.graphics.Bitmap

/**
 * Created by : Azamat Kalmurzaev
 * 20/10/24
 */
data class ChatState(
    val chat: MutableList<Chat> = mutableListOf(),
    val prompt: String = "",
    val bitmap: Bitmap? = null,
)
