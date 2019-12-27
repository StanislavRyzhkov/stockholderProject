package company.ryzhkov.sh.exception

sealed class CustomException(override val message: String) : RuntimeException()

data class NotFoundException(
    override val message: String,
    val code: Int = 200
) : CustomException(message)

data class AlreadyExistsException(
    override val message: String,
    val code: Int = 400
) : CustomException(message)

data class ValidationException(
    override val message: String,
    val code: Int = 400
) : CustomException(message)

data class AuthException(
    override val message: String,
    val code: Int = 400
) : CustomException(message)
