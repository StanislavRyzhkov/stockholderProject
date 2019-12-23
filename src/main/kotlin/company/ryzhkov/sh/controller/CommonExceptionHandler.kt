package company.ryzhkov.sh.controller

import company.ryzhkov.sh.entity.Message
import company.ryzhkov.sh.exception.AlreadyExistsException
import company.ryzhkov.sh.exception.AuthException
import company.ryzhkov.sh.exception.NotFoundException
import org.springframework.http.HttpStatus.*
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.support.WebExchangeBindException
import reactor.core.publisher.Mono

@RestController @ControllerAdvice class CommonExceptionHandler {

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException::class)
    fun handleInvalidAuth(e: AccessDeniedException): Mono<Message> =
        Mono.just(Message.accessDenied())

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(AuthException::class)
    fun handleInvalidAuth(e: AuthException): Mono<Message> =
        Mono.just(Message(e.message))

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException::class)
    fun handleInvalidInput(e: WebExchangeBindException): Mono<Message> =
        Mono.just(Message.validationExcMessage(e))

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BadCredentialsException::class)
    fun handleInvalidAuth(e: BadCredentialsException): Mono<Message> =
        Mono.just(Message(e.message))

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(AlreadyExistsException::class)
    fun handleInvalidAuth(e: AlreadyExistsException): Mono<Message> =
        Mono.just(Message(e.message))

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException::class)
    fun handleInvalidAuth(e: NotFoundException): Mono<Message> =
        Mono.just(Message(e.message))
}
