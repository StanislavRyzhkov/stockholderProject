package company.ryzhkov.sh.service

import arrow.core.*
import company.ryzhkov.sh.entity.*
import company.ryzhkov.sh.exception.AuthException
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
import company.ryzhkov.sh.util.Constants.USER_DELETED
import company.ryzhkov.sh.util.Constants.USER_UPDATED
import company.ryzhkov.sh.util.toEitherRight
import company.ryzhkov.sh.util.toMonoEitherLeft
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

    override fun findByUsername(username: String): Mono<UserDetails> =
        findActiveUserByUsername(username)
            .map {
                when(it) {
                    is Some -> GeneralUser.createInstance(it.t)
                    is None -> throw BadCredentialsException(INVALID_USERNAME_OR_PASSWORD)
                }
            }

    fun register(register: Register): Mono<Either<Message, User>> = Mono
        .zip(
            checkUsernameUnique(register.username),
            checkEmailUnique(register.email)
        )
        .flatMap {
            when {
                !it.t1 -> USER_ALREADY_EXISTS.toMonoEitherLeft()
                !it.t2 -> EMAIL_ALREADY_EXISTS.toMonoEitherLeft()
                else -> handleRegistration(register)
            }
        }

    fun handleRegistration(register: Register): Mono<Either<Message, User>> {
        val passwordHash = passwordEncoder.encode(register.password1)
        return userRepository
            .save(User(
                username = register.username,
                email = register.email,
                password = passwordHash,
                roles = Collections.singletonList("ROLE_USER"))
            )
            .map { it.toEitherRight() }
    }

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

    private fun findAnyUserByUsername(username: String): Mono<Option<User>> =
        userRepository
            .findByUsername(username)
            .map { it.toOption() }
            .defaultIfEmpty(None)

    private fun findActiveUserByUsername(username: String): Mono<Option<User>> =
        userRepository
            .findByUsernameAndStatus(username, "ACTIVE")
            .map { it.toOption() }
            .defaultIfEmpty(None)

    private fun findAnyUserByEmail(email: String): Mono<Option<User>> =
        userRepository
            .findByEmail(email)
            .map { it.toOption() }
            .defaultIfEmpty(None)

    private fun checkUsernameUnique(username: String): Mono<Boolean> =
        findAnyUserByUsername(username)
            .map { it is None }

    private fun checkEmailUnique(email: String): Mono<Boolean> =
        findAnyUserByEmail(email)
            .map { it is None }
}
