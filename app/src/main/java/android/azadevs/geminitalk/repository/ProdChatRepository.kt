package android.azadevs.geminitalk.repository

import android.azadevs.geminitalk.BuildConfig
import android.azadevs.geminitalk.model.Chat
import android.azadevs.geminitalk.utils.DataError
import android.azadevs.geminitalk.utils.Result
import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ResponseStoppedException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

/**
 * Created by : Azamat Kalmurzaev
 * 20/10/24
 */
class ProdChatRepository @Inject constructor() : ChatRepository {
    override suspend fun getResponseText(prompt: String): Result<Chat, DataError> {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash", apiKey = BuildConfig.API_KEY
        )
        try {
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt)
            }
            return if (response.text != null) Result.Success(
                Chat(
                    prompt = response.text!!,
                    bitmap = null,
                    isUser = false
                )
            ) else Result.Failure(DataError.EmptyResponse)

        } catch (e: IOException) {
            return Result.Failure(DataError.NoInternet)
        } catch (e: ResponseStoppedException) {
            return Result.Failure(DataError.ServerError(e.message ?: "Server Error"))
        } catch (e: Exception) {
            return Result.Failure(DataError.UnknownError(e.message ?: "Unknown Error"))
        }
    }

    override suspend fun getResponseTextWithImage(
        prompt: String,
        bitmap: Bitmap
    ): Result<Chat, DataError> {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash", apiKey = BuildConfig.API_KEY
        )
        try {
            val inputContent = content {
                text(prompt)
                image(bitmap)
            }
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(inputContent)
            }
            return if (response.text != null) Result.Success(
                Chat(
                    prompt = response.text!!,
                    bitmap = null,
                    isUser = false
                )
            ) else Result.Failure(DataError.UnknownError(""))
        } catch (e: IOException) {
            return Result.Failure(DataError.NoInternet)
        } catch (e: ResponseStoppedException) {
            return Result.Failure(DataError.ServerError(e.message ?: "Server Error"))
        } catch (e: Exception) {
            return Result.Failure(DataError.UnknownError(e.message ?: "Unknown Error"))
        }
    }


}