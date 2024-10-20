package android.azadevs.geminitalk.utils

/**
 * Created by : Azamat Kalmurzaev
 * 20/10/24
 */
sealed class Result<out T, out E> {

    data class Success<out T>(val value: T) : Result<T, Nothing>()

    data class Failure<out E>(val error: E) : Result<Nothing, E>()

}