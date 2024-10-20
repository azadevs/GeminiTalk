package android.azadevs.geminitalk.di

import android.azadevs.geminitalk.repository.ChatRepository
import android.azadevs.geminitalk.repository.ProdChatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by : Azamat Kalmurzaev
 * 20/10/24
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindChatRepository(chatRepository: ProdChatRepository): ChatRepository

}