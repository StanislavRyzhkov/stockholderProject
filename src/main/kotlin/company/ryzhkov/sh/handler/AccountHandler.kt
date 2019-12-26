package company.ryzhkov.sh.handler

import company.ryzhkov.sh.service.UserService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

class AccountHandler(
    private val userService: UserService
) {

    fun username(serverRequest: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok().body(Mono.just(5))
    }
}
