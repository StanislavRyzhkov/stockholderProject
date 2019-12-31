package company.ryzhkov.sh.handler

import company.ryzhkov.sh.entity.DeleteAccount
import company.ryzhkov.sh.entity.UpdateAccount
import company.ryzhkov.sh.entity.UpdatePassword
import company.ryzhkov.sh.entity.validate
import company.ryzhkov.sh.exception.CustomException
import company.ryzhkov.sh.service.UserService
import company.ryzhkov.sh.util.*
import company.ryzhkov.sh.util.PasswordConstants.PASSWORD_UPDATED
import company.ryzhkov.sh.util.UserConstants.USER_DELETED
import company.ryzhkov.sh.util.UserConstants.USER_UPDATED
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
        Mono.zip(
            serverRequest.toMonoUser(),
            serverRequest
                .bodyToMono(UpdateAccount::class.java)
                .map { it.validate() })
            .map { it.t2 + it.t1 }
            .flatMap { userService.updateAccount(it) }
            .flatMap { ok().bodyValue(USER_UPDATED.toMessage()) }
            .onErrorResume (CustomException::class.java) {
                ServerResponse.badRequest().bodyValue(it.message.toMessage())
            }

    fun deleteAccount(serverRequest: ServerRequest): Mono<ServerResponse> =
        Mono
            .zip(
                serverRequest.toMonoUser(),
                serverRequest
                    .bodyToMono(DeleteAccount::class.java)
                    .map { it.validate() }
            )
            .map { it.t2 + it.t1 }
            .flatMap { userService.deleteAccount(it) }
            .flatMap { ok().bodyValue(USER_DELETED.toMessage()) }
            .onErrorResume (CustomException::class.java) {
                ServerResponse.badRequest().bodyValue(it.message.toMessage())
            }

    fun updatePassword(serverRequest: ServerRequest): Mono<ServerResponse> =
        Mono
            .zip(
                serverRequest.toMonoUser(),
                serverRequest
                    .bodyToMono(UpdatePassword::class.java)
                    .map { it.validate() }
            )
            .map { it.t2 + it.t1 }
            .flatMap { userService.updatePassword(it) }
            .flatMap { ok().bodyValue(PASSWORD_UPDATED.toMessage()) }
            .onErrorResume (CustomException::class.java) {
                ServerResponse.badRequest().bodyValue(it.message.toMessage())
            }
}
