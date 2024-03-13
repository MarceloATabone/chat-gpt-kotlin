package util

sealed class Either<out L, out R> {

    val isSuccessful get() = this is Success<R>
    val isFailure get() = this is Failure<L>

    fun <L> failure(a: L) = Failure(a)
    fun <R> success(b: R) = Success(b)

    data class Failure<out L>(val value: L) : Either<L, Nothing>()
    data class Success<out R>(val value: R) : Either<Nothing, R>()

    fun <T> fold(failure: (L) -> T, success: (R) -> T): T =
        when (this) {
            is Failure -> failure(value)
            is Success -> success(value)
        }

    suspend fun <T> foldSuspendable(failure: suspend (L) -> T, success: suspend (R) -> T): T =
        when (this) {
            is Failure -> failure(value)
            is Success -> success(value)
        }

    fun <R2> map(transform: (R) -> R2): Either<L, R2> =
        when (this) {
            is Failure -> this
            is Success -> Success(transform(value))
        }

    fun <L2, R2> bimap(leftFn: (L) -> L2, rightFn: (R) -> R2): Either<L2, R2> =
        when (this) {
            is Failure -> Failure(leftFn(value))
            is Success -> Success(rightFn(value))
        }

    fun <L, R2> flatMap(transform: (R) -> Either<L, R2>): Either<Any?, R2> =
        when (this) {
            is Failure -> this
            is Success -> transform(value)
        }

    suspend fun <L, R2> flatMapSuspended(transform: suspend (R) -> Either<L, R2>): Either<Any?, R2> =
        when (this) {
            is Failure -> this
            is Success -> transform(value)
        }
}
