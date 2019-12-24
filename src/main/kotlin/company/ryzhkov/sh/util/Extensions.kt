package company.ryzhkov.sh.util

import company.ryzhkov.sh.entity.User
import company.ryzhkov.sh.security.GeneralUser
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono

fun UserDetails.fix(): User = (this as GeneralUser).user

fun Authentication.fix(): User = (this.principal as UserDetails).fix()

fun <A> Mono<Authentication>.plusUser() = print("")
