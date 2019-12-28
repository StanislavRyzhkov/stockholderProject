package company.ryzhkov.sh.util

import company.ryzhkov.sh.entity.*
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

fun String.validateAsPhoneNumber(): Boolean =
    Pattern
        .compile("\\+[0-9]+-[0-9]{3}-[0-9]{3}-[0-9]{4}")
        .matcher(this)
        .matches()

fun String.validateMaxLength(max: Int) = this.length < max

fun User.toAccount() = Account(
    this.username,
    this.email,
    this.firstName,
    this.secondName,
    this.phoneNumber
)

operator fun UpdateAccount.plus(user: User): UpdateAccountWithUser {
    val (firstName, secondName, phoneNumber) = this
    return UpdateAccountWithUser(firstName, secondName, phoneNumber, user)
}

operator fun Mono<UpdateAccount>.plus(user: Mono<User>): Mono<UpdateAccountWithUser> {
    return Mono.zip(this, user)
        .map { it.t1 + it.t2 }
}
