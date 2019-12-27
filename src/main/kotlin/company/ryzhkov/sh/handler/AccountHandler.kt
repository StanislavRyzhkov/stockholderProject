package company.ryzhkov.sh.handler

import company.ryzhkov.sh.entity.toAccount
import company.ryzhkov.sh.service.UserService
import company.ryzhkov.sh.util.toMessage
import company.ryzhkov.sh.util.toUser
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

class AccountHandler(
    private val userService: UserService
) {

    fun username(serverRequest: ServerRequest): Mono<ServerResponse> =
        ok().body(
            serverRequest
                .principal()
                .map { it.toUser().username.toMessage() }
        )

    fun account(serverRequest: ServerRequest): Mono<ServerResponse> =
        ok().body(
            serverRequest
                .principal()
                .map { it.toUser().toAccount() }
        )

    fun updateAccount(serverRequest: ServerRequest): Mono<ServerResponse> =
        TODO()
}
