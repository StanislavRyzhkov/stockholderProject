package company.ryzhkov.sh.security

import company.ryzhkov.sh.service.UserService
import company.ryzhkov.sh.util.UsernameConstants.INVALID_USERNAME_OR_PASSWORD
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import reactor.core.publisher.Mono

class CustomReactiveAuthenticationManager (
    private val userService: UserService,
    private val encoder: BCryptPasswordEncoder
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> = userService
        .findByUsername(authentication.name)
        .map { userDetails ->
            val password = authentication.credentials as String
            if (!encoder.matches(password, userDetails.password)) {
                throw BadCredentialsException(INVALID_USERNAME_OR_PASSWORD)
            }
            UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        }
}
