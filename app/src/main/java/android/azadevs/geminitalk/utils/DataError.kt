package android.azadevs.geminitalk.utils

/**
 * Created by : Azamat Kalmurzaev
 * 20/10/24
 */
sealed interface DataError {

    data object NoInternet : DataError

    data object EmptyResponse : DataError

    data class ServerError(val errorMessage: String) : DataError

    data class UnknownError(val errorMessage: String) : DataError
}