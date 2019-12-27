package company.ryzhkov.sh.util

import company.ryzhkov.sh.entity.Message
import company.ryzhkov.sh.entity.User
import company.ryzhkov.sh.security.GeneralUser
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono
import java.security.Principal
import java.util.regex.Pattern

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

