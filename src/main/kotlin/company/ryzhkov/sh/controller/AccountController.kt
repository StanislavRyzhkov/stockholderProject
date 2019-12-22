package company.ryzhkov.sh.controller

import company.ryzhkov.sh.entity.*
import company.ryzhkov.sh.security.GeneralUser
import company.ryzhkov.sh.service.UserService
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
        .map { authentication ->
            (authentication.principal as UserDetails).username
        }
        .map { Message(it) }

    @GetMapping(value = ["account"])
    @PreAuthorize(value = "hasRole('USER')")
    fun getAccount(authenticationMono: Mono<Authentication>): Mono<Account> = authenticationMono
        .map { authentication ->
            (authentication.principal as UserDetails) as GeneralUser
        }
        .map { Account.createInstance(it.user) }

    @PutMapping(value = ["account"])
    @PreAuthorize(value = "hasRole('USER')")
    fun updateAccount(
        authenticationMono: Mono<Authentication>,

        @Valid
        @RequestBody
        updateAccountMono: Mono<UpdateAccount>

    ): Mono<Message> = authenticationMono
        .zipWith(updateAccountMono)
        .flatMap { tuple ->
            val userDetails = tuple.t1.principal as UserDetails
            val updateAccount = tuple.t2
            userService.updateAccount(userDetails, updateAccount)
        }
        .map { Message(it) }

    @DeleteMapping(value = ["account"])
    @PreAuthorize(value = "hasRole('USER')")
    fun deleteAccount(
        authenticationMono: Mono<Authentication>,

        @Valid
        @RequestBody
        deleteAccountMono: Mono<DeleteAccount>

    ): Mono<Message> = authenticationMono
        .zipWith(deleteAccountMono)
        .flatMap { tuple ->
            val userDetails = tuple.t1.principal as UserDetails
            val deleteAccount = tuple.t2
            userService.deleteAccount(userDetails, deleteAccount)
        }
        .map { Message(it) }

    @PutMapping(value = ["password"])
    @PreAuthorize(value = "hasRole('USER')")
    fun updatePassword(
        authenticationMono: Mono<Authentication>,

        @Valid
        @RequestBody
        updatePasswordMono: Mono<UpdatePassword>

    ): Mono<Message> = authenticationMono
        .zipWith(updatePasswordMono)
        .flatMap { tuple ->
            val userDetails = tuple.t1.principal as UserDetails
            val updatePassword = tuple.t2
            userService.updatePassword(userDetails, updatePassword)
        }
        .map { Message(it) }
}
