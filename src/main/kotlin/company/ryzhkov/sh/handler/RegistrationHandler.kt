package company.ryzhkov.sh.handler

import company.ryzhkov.sh.entity.Register
import company.ryzhkov.sh.entity.validate
import company.ryzhkov.sh.exception.CustomException
import company.ryzhkov.sh.service.UserService
import company.ryzhkov.sh.util.UserConstants.USER_CREATED
import company.ryzhkov.sh.util.toMessage
import company.ryzhkov.sh.util.toMonoMessage
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.badRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

class RegistrationHandler(private val userService: UserService) {

    fun register(serverRequest: ServerRequest): Mono<ServerResponse> {
        return serverRequest
            .bodyToMono(Register::class.java)
            .map { it.validate() }
            .flatMap { userService.register(it) }
            .map { USER_CREATED.toMessage()  }
            .flatMap { ok().body(Mono.just(it)) }
            .onErrorResume (CustomException::class.java) {
                badRequest().body(it.message.toMonoMessage())
            }
    }
}
