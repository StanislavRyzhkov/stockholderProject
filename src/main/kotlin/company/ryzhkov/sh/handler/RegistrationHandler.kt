package company.ryzhkov.sh.handler

import company.ryzhkov.sh.entity.Register
import company.ryzhkov.sh.entity.validate
import company.ryzhkov.sh.exception.CustomException
import company.ryzhkov.sh.service.UserService
import company.ryzhkov.sh.util.UserConstants.USER_CREATED
import company.ryzhkov.sh.util.toMessage
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.badRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

class RegistrationHandler(private val userService: UserService) {

    fun register(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest
            .bodyToMono(Register::class.java)
            .map { it.validate() }
            .flatMap { userService.register(it) }
            .flatMap { ok().bodyValue(USER_CREATED.toMessage()) }
            .onErrorResume (CustomException::class.java) {
                badRequest().bodyValue(it.message.toMessage())
            }
}
