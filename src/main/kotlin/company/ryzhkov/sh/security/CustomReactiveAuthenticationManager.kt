package company.ryzhkov.sh.security

import company.ryzhkov.sh.util.Constants.INVALID_USERNAME_OR_PASSWORD
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component class CustomReactiveAuthenticationManager @Autowired constructor(
    @Qualifier("userService")
    private val userDetailsService: ReactiveUserDetailsService,
    private val encoder: PasswordEncoder
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> = userDetailsService
        .findByUsername(authentication.name)
        .map { userDetails ->
            val password = authentication.credentials as String
            if (!encoder.matches(password, userDetails.password)) {
                throw BadCredentialsException(INVALID_USERNAME_OR_PASSWORD)
            }
            UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        }
}
