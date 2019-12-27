package company.ryzhkov.sh.routes

import company.ryzhkov.sh.handler.AccountHandler
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

class AccountRoutes(
    private val accountHandler: AccountHandler
) {

    fun router() = router {
        "/api/user_area".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("/username", accountHandler::username)
            }
        }
    }.filter { request, next ->
        request.principal()
            .flatMap {
                val authentication = it as Authentication
                if (authentication.isAuthenticated) next.handle(request)
                else ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
            }
    }
}
