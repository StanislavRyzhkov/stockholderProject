package company.ryzhkov.sh.routes

import company.ryzhkov.sh.handler.AccountHandler
import company.ryzhkov.sh.security.TokenProvider
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

class AccountRoutes(
    private val accountHandler: AccountHandler,
    private val tokenProvider: TokenProvider
) {

    fun router() = router {
        "/api/user_area".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("/username", accountHandler::username)
            }
        }
    }.filter { request, next ->
        println("Filter")
        tokenProvider
            .getAuthentication(request)
            .flatMap {
                if (it.isAuthenticated) {next.handle(request)}
                else ServerResponse.badRequest().body(Mono.just(1))
            }
    }
}
