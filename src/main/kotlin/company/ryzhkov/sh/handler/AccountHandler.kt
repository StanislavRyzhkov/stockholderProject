package company.ryzhkov.sh.handler

import company.ryzhkov.sh.entity.UpdateAccount
import company.ryzhkov.sh.entity.UpdateAccountWithUser
import company.ryzhkov.sh.entity.validate
import company.ryzhkov.sh.service.UserService
import company.ryzhkov.sh.util.plus
import company.ryzhkov.sh.util.toAccount
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

    fun updateAccount(serverRequest: ServerRequest): Mono<ServerResponse> {
        val user = serverRequest.principal().map { it.toUser() }
        val updateAccount = serverRequest
            .bodyToMono(UpdateAccount::class.java)
            .map { it.validate() }
        val updateAccountWithUser = updateAccount + user
        TODO()
    }
}
