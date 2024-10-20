package android.azadevs.geminitalk.viewmodel

import android.azadevs.geminitalk.model.Chat
import android.azadevs.geminitalk.repository.ChatRepository
import android.azadevs.geminitalk.utils.ChatState
import android.azadevs.geminitalk.utils.ChatUiEvent
import android.azadevs.geminitalk.utils.DataError
import android.azadevs.geminitalk.utils.Result
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by : Azamat Kalmurzaev
 * 20/10/24
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val prodChatRepository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    val errorMessage = MutableSharedFlow<String>()

    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.SendPrompt -> {
                if (event.prompt.isNotEmpty()) {
                    sendPrompt(event.prompt, event.bitmap)

                    if (event.bitmap != null) {
                        getResponseTextWithImage(event.prompt, event.bitmap)
                    } else {
                        getResponseText(event.prompt)
                    }
                }
            }

            is ChatUiEvent.UpdatePrompt -> _state.update {
                it.copy(prompt = event.prompt)
            }
        }

    }

    private fun sendPrompt(prompt: String, bitmap: Bitmap?) {
        _state.update {
            it.copy(
                chat = it.chat.toMutableList().apply {
                    add(
                        0, Chat(
                            prompt = prompt,
                            bitmap = bitmap,
                            isUser = true
                        )
                    )
                },
                prompt = "",
                bitmap = null
            )
        }
    }

    private fun getResponseText(prompt: String) {
        viewModelScope.launch {
            when (val responseText = prodChatRepository.getResponseText(prompt)) {
                is Result.Failure -> {
                    when (responseText.error) {
                        DataError.EmptyResponse -> errorMessage.emit("Empty response")
                        DataError.NoInternet -> errorMessage.emit("No internet connection")
                        is DataError.ServerError -> errorMessage.emit(responseText.error.errorMessage)
                        is DataError.UnknownError -> errorMessage.emit(responseText.error.errorMessage)
                    }
                }

                is Result.Success -> _state.update {
                    it.copy(
                        chat = it.chat.toMutableList().apply {
                            add(
                                0,
                                responseText.value
                            )
                        }
                    )
                }
            }
        }
    }

    private fun getResponseTextWithImage(prompt: String, bitmap: Bitmap) {
        viewModelScope.launch {
            when (val responseText = prodChatRepository.getResponseTextWithImage(prompt, bitmap)) {
                is Result.Failure -> {
                    when (responseText.error) {
                        DataError.EmptyResponse -> errorMessage.emit("Empty response")
                        DataError.NoInternet -> errorMessage.emit("No internet connection")
                        is DataError.ServerError -> errorMessage.emit(responseText.error.errorMessage)
                        is DataError.UnknownError -> errorMessage.emit(responseText.error.errorMessage)
                    }
                }

                is Result.Success -> _state.update {
                    it.copy(
                        chat = it.chat.toMutableList().apply {
                            add(
                                0,
                                responseText.value
                            )
                        }
                    )
                }
            }
        }
    }
}