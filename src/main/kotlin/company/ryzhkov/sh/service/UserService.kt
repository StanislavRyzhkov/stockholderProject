package company.ryzhkov.sh.service

import company.ryzhkov.sh.entity.*
import company.ryzhkov.sh.exception.AlreadyExistsException
import company.ryzhkov.sh.exception.AuthException
import company.ryzhkov.sh.exception.NotFoundException
import company.ryzhkov.sh.repository.UserRepository
import company.ryzhkov.sh.security.GeneralUser
import company.ryzhkov.sh.util.AdminConstants.ADMIN_EMAIL
import company.ryzhkov.sh.util.AdminConstants.ADMIN_PASSWORD
import company.ryzhkov.sh.util.AdminConstants.ADMIN_USERNAME
import company.ryzhkov.sh.util.ArgsConstants.ARG_ADMIN
import company.ryzhkov.sh.util.EmailConstants.EMAIL_ALREADY_EXISTS
import company.ryzhkov.sh.util.RolesConstants.ROLE_ADMIN
import company.ryzhkov.sh.util.RolesConstants.ROLE_USER
import company.ryzhkov.sh.util.UserConstants.USER_ALREADY_EXISTS
import company.ryzhkov.sh.util.UserConstants.USER_NOT_FOUND
import company.ryzhkov.sh.util.UsernameConstants.INVALID_USERNAME_OR_PASSWORD
import org.springframework.boot.ApplicationArguments
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import reactor.core.publisher.Mono
import java.util.*

class UserService (
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val applicationArguments: ApplicationArguments
) : ReactiveUserDetailsService {

    private val log: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(UserService::class.java)

    override fun findByUsername(username: String): Mono<UserDetails> = findActiveUserByUsername(username)
        .map { GeneralUser.createInstance(it) }
        .onErrorMap(NotFoundException::class.java) { BadCredentialsException(INVALID_USERNAME_OR_PASSWORD) }

    fun register(register: Register): Mono<User> = checkUsernameUnique(register.username)
        .zipWith(checkEmailUnique(register.email))
        .flatMap { tuple ->
            if (!tuple.t1) throw AlreadyExistsException(USER_ALREADY_EXISTS)
            if (!tuple.t2) throw AlreadyExistsException(EMAIL_ALREADY_EXISTS)
            val passwordHash = passwordEncoder.encode(register.password1)
            userRepository.insert(User(
                username =  register.username,
                email =     register.email,
                password =  passwordHash,
                roles =     Collections.singletonList(ROLE_USER)
            ))
        }

    fun updateAccount(updateAccountWithUser: UpdateAccountWithUser): Mono<User> {
        val (firstName, secondName, phoneNumber, user) = updateAccountWithUser
        val updatedUser = user.copy(
            firstName =     firstName,
            secondName =    secondName,
            phoneNumber =   phoneNumber
        )
        return userRepository.save(updatedUser)
    }

    fun deleteAccount(deleteAccountWithUser: DeleteAccountWithUser): Mono<User> {
        val (username, password1, _, user) = deleteAccountWithUser
        if (username != user.username) {
            throw AuthException(INVALID_USERNAME_OR_PASSWORD)
        }
        if (!passwordEncoder.matches(password1, user.password)) {
            throw AuthException(INVALID_USERNAME_OR_PASSWORD)
        }
        val deletedUser = user.copy(status = "DELETED")
        return userRepository.save(deletedUser)
    }

    fun updatePassword(updatePasswordWithUser: UpdatePasswordWithUser): Mono<User> {
        val (oldPassword, newPassword1, _, user) = updatePasswordWithUser
        if (!passwordEncoder.matches(oldPassword, user.password)) {
            throw AuthException(INVALID_USERNAME_OR_PASSWORD)
        }
        val userWithUpdatedPassword = user.copy(password = passwordEncoder.encode(newPassword1))
        return userRepository.save(userWithUpdatedPassword)
    }

    fun createAdminUser() {
        if (ARG_ADMIN in applicationArguments.sourceArgs) {
            val user = User(
                username =  ADMIN_USERNAME,
                email =     ADMIN_EMAIL,
                password =  passwordEncoder.encode(ADMIN_PASSWORD),
                roles =     listOf(ROLE_ADMIN, ROLE_USER)
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
