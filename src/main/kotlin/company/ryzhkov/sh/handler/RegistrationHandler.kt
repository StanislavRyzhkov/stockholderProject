package company.ryzhkov.sh.handler

import company.ryzhkov.sh.entity.Register
import company.ryzhkov.sh.exception.CustomException
import company.ryzhkov.sh.service.UserService
import company.ryzhkov.sh.util.toMonoMessage
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

class RegistrationHandler(private val userService: UserService) {

    fun register(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest
            .bodyToMono(Register::class.java)
            .flatMap { userService.register(it) }
            .flatMap { ServerResponse.ok().body(it.toMonoMessage()) }
            .onErrorResume (CustomException::class.java) {
                ServerResponse.badRequest().body(it.message.toMonoMessage())
            }
}
