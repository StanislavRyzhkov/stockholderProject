package company.ryzhkov.sh.service

import company.ryzhkov.sh.entity.*
import company.ryzhkov.sh.exception.AlreadyExistsException
import company.ryzhkov.sh.exception.AuthException
import company.ryzhkov.sh.exception.NotFoundException
import company.ryzhkov.sh.repository.UserRepository
import company.ryzhkov.sh.security.GeneralUser
import company.ryzhkov.sh.util.Constants.ACCESS_DENIED
import company.ryzhkov.sh.util.Constants.ADMIN_EMAIL
import company.ryzhkov.sh.util.Constants.ADMIN_PASSWORD
import company.ryzhkov.sh.util.Constants.ADMIN_USERNAME
import company.ryzhkov.sh.util.Constants.EMAIL_ALREADY_EXISTS
import company.ryzhkov.sh.util.Constants.INVALID_USERNAME_OR_PASSWORD
import company.ryzhkov.sh.util.Constants.PASSWORD_UPDATED
import company.ryzhkov.sh.util.Constants.USER_ALREADY_EXISTS
import company.ryzhkov.sh.util.Constants.USER_CREATED
import company.ryzhkov.sh.util.Constants.USER_DELETED
import company.ryzhkov.sh.util.Constants.USER_NOT_FOUND
import company.ryzhkov.sh.util.Constants.USER_UPDATED
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*
import javax.annotation.PostConstruct

@Service class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val applicationArguments: ApplicationArguments
) : ReactiveUserDetailsService {

    private val log: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(UserService::class.java)

    override fun findByUsername(username: String): Mono<UserDetails> = findActiveUserByUsername(username)
        .map { GeneralUser.createInstance(it) }
        .onErrorMap(NotFoundException::class.java) { BadCredentialsException(INVALID_USERNAME_OR_PASSWORD) }

    fun register(register: Register): Mono<String> = checkUsernameUnique(register.username)
        .zipWith(checkEmailUnique(register.email))
        .flatMap { tuple ->
            if (!tuple.t1) throw AlreadyExistsException(USER_ALREADY_EXISTS)
            if (!tuple.t2) throw AlreadyExistsException(EMAIL_ALREADY_EXISTS)
            val passwordHash = passwordEncoder.encode(register.password1)
            userRepository.save(User(
                username = register.username,
                email = register.email,
                password = passwordHash,
                roles = Collections.singletonList("ROLE_USER")
            ))
        }
        .doOnNext { user -> log.info("{} created", user.toString()) }
        .map { USER_CREATED }

    fun updateAccount(userDetails: UserDetails, updateAccount: UpdateAccount): Mono<String> {
        val user = (userDetails as GeneralUser).user
        val (firstName, secondName, phoneNumber) = updateAccount
        val updatedUser = user.copy(
            firstName = firstName,
            secondName = secondName,
            phoneNumber = phoneNumber
        )
        return userRepository
            .save(updatedUser)
            .doOnNext { log.info("{} updated", it.toString()) }
            .map { USER_UPDATED }
    }

    fun deleteAccount(userDetails: UserDetails, deleteAccount: DeleteAccount): Mono<String> {
        val (username, password1, _) = deleteAccount

        if (username != userDetails.username) {
            throw AuthException(ACCESS_DENIED)
        }

        if (!passwordEncoder.matches(password1, userDetails.password)) {
            throw AuthException(INVALID_USERNAME_OR_PASSWORD)
        }

        val user = (userDetails as GeneralUser).user
        val deletedUser = user.copy(status = "DELETED")

        return userRepository
            .save(deletedUser)
            .doOnNext { log.info("{} deleted", it.toString()) }
            .map { USER_DELETED }
    }

    fun updatePassword(userDetails: UserDetails, updatePassword: UpdatePassword): Mono<String> {
        val (oldPassword, newPassword1, _) = updatePassword

        if (!passwordEncoder.matches(oldPassword, userDetails.password)) {
            throw AuthException(INVALID_USERNAME_OR_PASSWORD)
        }

        val user = (userDetails as GeneralUser).user
        val userWithUpdatedPassword = user.copy(password = passwordEncoder.encode(newPassword1))

        return userRepository
            .save(userWithUpdatedPassword)
            .doOnNext { log.info("{} password updated", it.toString()) }
            .map { PASSWORD_UPDATED }
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
                .subscribe { log.info("Admin {} successfully created", it.username) }
        }
    }

    private fun findAnyUserByUsername(username: String): Mono<User> = userRepository
        .findByUsername(username)
        .switchIfEmpty(Mono.error(NotFoundException(USER_NOT_FOUND)))

    private fun findActiveUserByUsername(username: String): Mono<User> = userRepository
        .findByUsernameAndStatus(username, "ACTIVE")
        .switchIfEmpty(Mono.error(NotFoundException(USER_NOT_FOUND)))

    private fun findAnyUserByEmail(email: String): Mono<User> = userRepository
        .findByEmail(email)
        .switchIfEmpty(Mono.error(NotFoundException(USER_NOT_FOUND)))

    private fun checkUsernameUnique(username: String): Mono<Boolean> = findAnyUserByUsername(username)
        .map { false }
        .onErrorResume(NotFoundException::class.java) { Mono.just(true) }

    private fun checkEmailUnique(email: String): Mono<Boolean> = findAnyUserByEmail(email)
        .map { false }
        .onErrorResume(NotFoundException::class.java) { Mono.just(true) }
}
