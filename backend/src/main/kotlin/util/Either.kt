package util

sealed class Either<out L, out R> {
    data class Failure<out T>(val value: T) : Either<T, Nothing>()
    data class Success<out T>(val value: T) : Either<Nothing, T>()
}

inline fun <L, R, T> Either<L, R>.fold(left: (L) -> T, right: (R) -> T): T =
    when (this) {
        is Either.Failure -> left(value)
        is Either.Success -> right(value)
    }

inline fun <L, R, T> Either<L, R>.flatMap(f: (R) -> Either<L, T>): Either<L, T> =
    fold(left = { this as Either.Failure }, right = f)

inline fun <L, R, T> Either<L, R>.map(f: (R) -> T): Either<L, T> =
    flatMap { Either.Success(f(it)) }