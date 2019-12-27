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

        val x = serverRequest.principal().map { e ->
            println(e)
            "HUY"
        }.switchIfEmpty(Mono.just("GO!"))
            .doOnNext {
                println("1")
                println(it)
                println("2")
            }
        return ServerResponse.ok().body(x)
    }
}
