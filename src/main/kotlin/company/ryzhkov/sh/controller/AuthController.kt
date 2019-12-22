package company.ryzhkov.sh.controller

import company.ryzhkov.sh.entity.Auth
import company.ryzhkov.sh.entity.Message
import company.ryzhkov.sh.security.TokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping(value = ["/api/auth"])
class AuthController @Autowired constructor (
    private val manager: ReactiveAuthenticationManager,
    private val provider: TokenProvider
) {

    @PostMapping fun authenticate(@Valid @RequestBody authMono: Mono<Auth>): Mono<Message> =
        authMono
            .flatMap { auth ->
                manager.authenticate(UsernamePasswordAuthenticationToken(
                    auth.username,
                    auth.password
                ))
            }
            .map { provider.createToken(it) }
            .map { Message(it) }
}
