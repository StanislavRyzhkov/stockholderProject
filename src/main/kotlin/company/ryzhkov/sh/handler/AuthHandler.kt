package company.ryzhkov.sh.handler

import company.ryzhkov.sh.entity.Auth
import company.ryzhkov.sh.exception.CustomException
import company.ryzhkov.sh.security.CustomReactiveAuthenticationManager
import company.ryzhkov.sh.security.TokenProvider
import company.ryzhkov.sh.util.toMessage
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

class AuthHandler(
    private val manager: CustomReactiveAuthenticationManager,
    private val provider: TokenProvider
) {

    fun authenticate(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest
            .bodyToMono(Auth::class.java)
            .flatMap {
                manager.authenticate(
                    UsernamePasswordAuthenticationToken(it.username, it.password)
                )
            }
            .map { provider.createToken(it) }
            .flatMap { ok().bodyValue(it.toMessage()) }
            .onErrorResume (CustomException::class.java) {
                ServerResponse.badRequest().bodyValue(it.message.toMessage())
            }

}
