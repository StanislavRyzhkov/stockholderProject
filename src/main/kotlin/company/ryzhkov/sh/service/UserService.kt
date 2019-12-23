package company.ryzhkov.sh.service

import company.ryzhkov.sh.entity.*
import company.ryzhkov.sh.exception.AlreadyExistsException
import company.ryzhkov.sh.exception.AuthException
import company.ryzhkov.sh.repository.UserRepository
import company.ryzhkov.sh.security.GeneralUser
import company.ryzhkov.sh.util.Constants.ACCESS_DENIED
import company.ryzhkov.sh.util.Constants.ADMIN_EMAIL
import company.ryzhkov.sh.util.Constants.ADMIN_PASSWORD
import company.ryzhkov.sh.util.Constants.ADMIN_USERNAME
import company.ryzhkov.sh.util.Constants.EMAIL_ALREADY_EXISTS
import company.ryzhkov.sh.util.Constants.INVALID_USERNAME_OR_PASSWORD
import company.ryzhkov.sh.util.Constants.USER_ALREADY_EXISTS
import company.ryzhkov.sh.util.fix
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.*
import javax.annotation.PostConstruct

@Service class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val applicationArguments: ApplicationArguments
) : ReactiveUserDetailsService {

    private val log: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(UserService::class.java)

    override fun findByUsername(username: String): Mono<UserDetails> = userRepository
        .findByUsernameAndStatus(username, "ACTIVE")
        .map { GeneralUser.createInstance(it) }
        .switchIfEmpty(Mono.error(BadCredentialsException(INVALID_USERNAME_OR_PASSWORD)))

    fun register(register: Register): Mono<User> = Mono
        .zip(checkUsernameUnique(register.username), checkEmailUnique(register.email))
        .flatMap { when {
            !it.t1 -> Mono.error(AlreadyExistsException(USER_ALREADY_EXISTS))
            !it.t2 -> Mono.error(AlreadyExistsException(EMAIL_ALREADY_EXISTS))
            else -> {
                val passwordHash = passwordEncoder.encode(register.password1)
                userRepository.save(User(
                    username = register.username,
                    email = register.email,
                    password = passwordHash,
                    roles = Collections.singletonList("ROLE_USER")
                ))
            }
        } }

    fun updateAccount(authMono: Mono<Authentication>, updateAccountMono: Mono<UpdateAccount>): Mono<User> = Mono
        .zip(authMono, updateAccountMono)
        .flatMap {
            val user = it.t1.fix()
            val (firstName, secondName, phoneNumber) = it.t2
            val updatedUser = user.copy(firstName = firstName, secondName = secondName, phoneNumber = phoneNumber)
            userRepository.save(updatedUser)
        }

    fun deleteAccount(monoAuth: Mono<Authentication>, monoDeleteAccount: Mono<DeleteAccount>): Mono<User> = Mono
        .zip(monoAuth, monoDeleteAccount)
        .flatMap {
            val user = it.t1.fix()
            val deleteAccount = it.t2
            val (username, password1, _) = deleteAccount

            if (username != user.username)
                throw AuthException(ACCESS_DENIED)

            if (!passwordEncoder.matches(password1, user.password))
                throw AuthException(INVALID_USERNAME_OR_PASSWORD)

            val deletedUser = user.copy(status = "DELETED")
            userRepository.save(deletedUser)
        }

    fun updatePassword(
        authenticationMono: Mono<Authentication>,
        updatePasswordMono: Mono<UpdatePassword>
    ): Mono<User> = Mono
        .zip(authenticationMono, updatePasswordMono)
        .flatMap {
            val user = it.t1.fix()
            val (oldPassword, newPassword1, _) = it.t2
            if (!passwordEncoder.matches(oldPassword, user.password))
                throw AuthException(INVALID_USERNAME_OR_PASSWORD)
            val userWithUpdatedPassword = user.copy(password = passwordEncoder.encode(newPassword1))
            userRepository.save(userWithUpdatedPassword)
        }

    @PostConstruct fun createAdminUser() {
        if ("--admin" in applicationArguments.sourceArgs) {
            val user = User(
                username = ADMIN_USERNAME,
                email = ADMIN_EMAIL,
                password = passwordEncoder.encode(ADMIN_PASSWORD),
                roles = listOf("ROLE_ADMIN", "ROLE_USER")
            )

            userRepository
                .insert(user)
                .subscribeOn(Schedulers.elastic())
                .subscribe { log.info("Admin {} successfully created", it.username) }
        }
    }

    private fun checkUsernameUnique(username: String): Mono<Boolean> = userRepository
        .findByUsername(username)
        .map { false }
        .defaultIfEmpty(true)

    private fun checkEmailUnique(email: String): Mono<Boolean> = userRepository
        .findByEmail(email)
        .map { false }
        .defaultIfEmpty(true)
}
