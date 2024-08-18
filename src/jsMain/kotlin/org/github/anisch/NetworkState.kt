package org.github.anisch

sealed class NetworkState<out T>(
    val data: T? = null,
    val cause: Throwable? = null,
) {
    data object Init : NetworkState<Nothing>()
    data object IsLoading : NetworkState<Nothing>()
    class Error(cause: Throwable) : NetworkState<Nothing>(cause = cause)
    class NoData: NetworkState<Nothing>()
    class Success<T>(data: T) : NetworkState<T>(data)
}
