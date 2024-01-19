package com.tws.moments.datasource.usecase.helpers

import kotlinx.coroutines.flow.FlowCollector

sealed class ResultUC<out T>(
    val value: T?,
) {
    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this !is Success

    class Success<T>(value: T?) : ResultUC<T>(value)
    class Failure(
        val message: String?,
        val throwable: Throwable?
    ) : ResultUC<Nothing>(null)

    fun getOrNull(): T? =
        when {
            isSuccess -> value
            else -> null
        }

    @Suppress("UNCHECKED_CAST")
    fun <R, T : R> getOrDefault(defaultValue: R): R {
        if (value == null) return defaultValue
        return value as T
    }

    @Suppress("UNCHECKED_CAST")
    fun getError(): Throwable? {
        return (this as Failure).throwable
    }

    companion object {
        fun <T> success(value: T? = null): Success<T> = Success(value)
        fun failure(
            message: String? = null,
            throwable: Throwable? = null,
        ): Failure = Failure(message, throwable)
    }
}

suspend fun FlowCollector<ResultUC.Failure>.fails(throwable: Exception? = null) {
    emit(ResultUC.failure(throwable = throwable))
}

suspend inline fun <reified T : Throwable> FlowCollector<ResultUC.Failure>.failsThrowable() {
    emit(ResultUC.failure(throwable = getValue<T>()))
}

/* We have no way to guarantee that an empty constructor exists, so must return T? instead of T */
inline fun <reified T : Any> getValue(): T? {
    val primaryConstructor = T::class.constructors.find { it.parameters.isEmpty() }
    return primaryConstructor?.call()
}