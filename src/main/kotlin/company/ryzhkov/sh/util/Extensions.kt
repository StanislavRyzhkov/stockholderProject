package company.ryzhkov.sh.util

import company.ryzhkov.sh.entity.Message
import company.ryzhkov.sh.entity.User
import company.ryzhkov.sh.security.GeneralUser
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.security.Principal
import java.util.regex.Pattern


import company.ryzhkov.sh.entity.Register
import company.ryzhkov.sh.entity.validate
import company.ryzhkov.sh.exception.CustomException
import company.ryzhkov.sh.service.UserService
import company.ryzhkov.sh.util.UserConstants.USER_CREATED
import company.ryzhkov.sh.util.mapToMessage
import company.ryzhkov.sh.util.toMessage
import company.ryzhkov.sh.util.toMonoMessage
import org.springframework.web.reactive.function.server.ServerRequest

import org.springframework.web.reactive.function.server.ServerResponse.badRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body

import java.io.Serializable

fun UserDetails.fix(): User = (this as GeneralUser).user

fun Principal.fix(): Authentication = this as Authentication

fun Principal.toUser(): User = (this.fix().principal as UserDetails).fix()

fun String.toMessage(): Message = Message(this)

fun Mono<String>.mapToMessage() = this.map { it.toMessage() }

fun String.toMonoMessage(): Mono<Message> = Mono.just(Message(this))

fun String.validateAsEmail(): Boolean =
    Pattern
        .compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
        .matcher(this)
        .matches()

//inline fun <reified A> Mono<A>.toResponse() =
//    ServerResponse.ok().body(this)

