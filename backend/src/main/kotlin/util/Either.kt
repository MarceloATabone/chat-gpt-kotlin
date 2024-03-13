package util

/**
 * Sealed class that represents a value of one of two possible types (a disjoint union).
 * An instance of Either is an instance of Left or Right.
 *
 * @param L The type of the Left value
 * @param R The type of the Right value
 */
sealed class Either<out L, out R> {

    val isSuccessful get() = this is Success<R>
    val isFailure get() = this is Failure<L>

    fun <L> failure(a: L) = Failure(a)
    fun <R> success(b: R) = Success(b)

    /**
     * Represents the left side of [Either] class which by convention is a "Failure".
     *
     * @param L The type of the Left value
     * @param value The value of the Left instance
     */
    data class Failure<out L>(val value: L) : Either<L, Nothing>()

    /**
     * Represents the right side of [Either] class which by convention is a "Success".
     *
     * @param R The type of the Right value
     * @param value The value of the Right instance
     */
    data class Success<out R>(val value: R) : Either<Nothing, R>()

    /**
     * Applies one of the given functions depending on whether this is a [Failure] or [Success].
     *
     * @param T The type of the result value
     * @param failure The function to apply if this is a [Failure]
     * @param success The function to apply if this is a [Success]
     * @return The result of applying the appropriate function
     */
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

    /**
     * Transforms the [Success] value of this [Either] instance if it is [Success], otherwise
     * returns this unchanged.
     *
     * @param R2 The type of the new Right value
     * @param transform The function to transform the [Success] value
     * @return A new [Either] instance with the transformed [Success] value, or this if it's [Failure]
     */
    fun <R2> map(transform: (R) -> R2): Either<L, R2> =
        when (this) {
            is Failure -> this
            is Success -> Success(transform(value))
        }

    /**
     * Applies the given functions to transform both the [Failure] and [Success] values of this [Either] instance.
     *
     * @param L2 The type of the new Left value
     * @param R2 The type of the new Right value
     * @param leftFn The function to transform the [Failure] value
     * @param rightFn The function to transform the [Success] value
     * @return A new [Either] instance with transformed [Failure] and [Success] values
     */
    fun <L2, R2> bimap(leftFn: (L) -> L2, rightFn: (R) -> R2): Either<L2, R2> =
        when (this) {
            is Failure -> Failure(leftFn(value))
            is Success -> Success(rightFn(value))
        }

    /**
     * Transforms the [Success] value of this [Either] instance by applying the given function if it is [Success],
     * otherwise returns this unchanged. The function itself returns another [Either] instance.
     *
     * @param R2 The type of the new Right value
     * @param transform The function to transform the [Success] value
     * @return A new [Either] instance with the transformed [Success] value
     * @return A new [Either] instance with the transformed [Success] value, or this if it's [Failure]
     */
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