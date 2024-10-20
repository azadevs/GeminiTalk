package android.azadevs.geminitalk

import android.azadevs.geminitalk.ui.theme.GeminiTalkTheme
import android.azadevs.geminitalk.utils.ChatUiEvent
import android.azadevs.geminitalk.viewmodel.ChatViewModel
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import coil3.asDrawable
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.size.Size
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var uriState = MutableStateFlow("")

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            uriState.update {
                uri.toString()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GeminiTalkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    ChatScreen()
                }
            }
        }
    }

    @Composable
    fun ChatScreen() {
        val viewModel by viewModels<ChatViewModel>()
        val state = viewModel.state.collectAsState().value
        val bitmap = getBitMap()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                reverseLayout = true
            ) {
                itemsIndexed(state.chat) { _, item ->
                    if (item.isUser) {
                        UserMessageItem(prompt = item.prompt, bitmap = item.bitmap)
                    } else {
                        ModelMessageItem(response = item.prompt)
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, start = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    bitmap?.let {
                        Image(
                            modifier = Modifier
                                .size(40.dp)
                                .padding(bottom = 2.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentDescription = "Picked image",
                            contentScale = ContentScale.Crop,
                            bitmap = it.asImageBitmap()
                        )
                    }
                    Icon(
                        imageVector = Icons.Rounded.AddPhotoAlternate,
                        contentDescription = "Add photo",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                imagePicker.launch(
                                    PickVisualMediaRequest
                                        .Builder()
                                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        .build()
                                )
                            },
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextField(modifier = Modifier.weight(1f), value = state.prompt, onValueChange = {
                    viewModel.onEvent(ChatUiEvent.UpdatePrompt(it))
                }, placeholder = {
                    Text(text = "Ask me anything...")
                })

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = "Send prompt",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            viewModel.onEvent(ChatUiEvent.SendPrompt(state.prompt, bitmap))
                            uriState.update { "" }
                        },
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        viewModel.errorMessage.collectAsState(null).value?.let {
            Toast.makeText(LocalContext.current, it, Toast.LENGTH_SHORT).show()
        }
    }

    @Composable
    fun UserMessageItem(prompt: String, bitmap: Bitmap?) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 100.dp),
        ) {
            bitmap?.let {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 2.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                    bitmap = it.asImageBitmap()
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp),
                text = prompt,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    @Composable
    fun ModelMessageItem(response: String) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, end = 100.dp),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(16.dp),
                text = response,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    @Composable
    fun getBitMap(): Bitmap? {
        val uri = uriState.collectAsState().value
        val imageState = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current).data(uri).size(Size.ORIGINAL).build()
        ).state.collectAsState().value
        if (imageState is AsyncImagePainter.State.Success) {
            return imageState.result.image.asDrawable(resources = resources).toBitmap()
        }
        return null
    }
}
