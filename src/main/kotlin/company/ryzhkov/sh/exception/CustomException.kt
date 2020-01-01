package company.ryzhkov.sh.exception

class NotFoundException(override val message: String) : RuntimeException(message)

class AlreadyExistsException(override val message: String) : RuntimeException(message)

class AuthException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}
