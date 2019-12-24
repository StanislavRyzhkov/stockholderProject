package company.ryzhkov.sh.controller

import company.ryzhkov.sh.entity.*
import company.ryzhkov.sh.security.GeneralUser
import company.ryzhkov.sh.service.UserService
import company.ryzhkov.sh.util.Constants.PASSWORD_UPDATED
import company.ryzhkov.sh.util.Constants.USER_DELETED
import company.ryzhkov.sh.util.Constants.USER_UPDATED
import company.ryzhkov.sh.util.fix
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping(value = ["/api/user_area"])
class AccountController @Autowired constructor(
    private val userService: UserService
) {

    @GetMapping(value = ["username"])
    @PreAuthorize(value = "hasRole('USER')")
    fun getUsername(authenticationMono: Mono<Authentication>): Mono<Message> = authenticationMono
        .map { Message(it.fix().username) }

    @GetMapping(value = ["account"])
    @PreAuthorize(value = "hasRole('USER')")
    fun getAccount(authenticationMono: Mono<Authentication>): Mono<Account> = authenticationMono
        .map { Account.createInstance(it.fix()) }

    @PutMapping(value = ["account"])
    @PreAuthorize(value = "hasRole('USER')")
    fun updateAccount(
        authenticationMono: Mono<Authentication>,
        @Valid @RequestBody updateAccountMono: Mono<UpdateAccount>
    ): Mono<Message> = userService
        .updateAccount(authenticationMono, updateAccountMono)
        .map { Message(USER_UPDATED) }

    @DeleteMapping(value = ["account"])
    @PreAuthorize(value = "hasRole('USER')")
    fun deleteAccount(
        authenticationMono: Mono<Authentication>,
        @Valid @RequestBody deleteAccountMono: Mono<DeleteAccount>
    ): Mono<Message> = userService
        .deleteAccount(authenticationMono, deleteAccountMono)
        .map { Message(USER_DELETED) }

    @PutMapping(value = ["password"])
    @PreAuthorize(value = "hasRole('USER')")
    fun updatePassword(
        authenticationMono: Mono<Authentication>,
        @Valid @RequestBody updatePasswordMono: Mono<UpdatePassword>
    ): Mono<Message> = userService
        .updatePassword(authenticationMono, updatePasswordMono)
        .map { Message(PASSWORD_UPDATED) }
}
