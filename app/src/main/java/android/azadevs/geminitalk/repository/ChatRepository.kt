package android.azadevs.geminitalk.repository

import android.azadevs.geminitalk.model.Chat
import android.azadevs.geminitalk.utils.DataError
import android.azadevs.geminitalk.utils.Result
import android.graphics.Bitmap

/**
 * Created by : Azamat Kalmurzaev
 * 20/10/24
 */
interface ChatRepository {

    suspend fun getResponseText(prompt: String): Result<Chat, DataError>

    suspend fun getResponseTextWithImage(prompt: String, bitmap: Bitmap): Result<Chat, DataError>

}