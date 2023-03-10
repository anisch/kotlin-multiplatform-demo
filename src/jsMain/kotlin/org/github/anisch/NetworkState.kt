package org.github.anisch

sealed class NetworkState<out T>(
    val data: T? = null,
    val cause: Throwable? = null,
) {
    class Init : NetworkState<Nothing>()
    class IsLoading : NetworkState<Nothing>()
    class Error(cause: Throwable) : NetworkState<Nothing>(cause = cause)
    class NoData: NetworkState<Nothing>()
    class Success<T>(data: T) : NetworkState<T>(data)
}
