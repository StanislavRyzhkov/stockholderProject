package company.ryzhkov.sh.routes

import company.ryzhkov.sh.handler.AccountHandler
import company.ryzhkov.sh.handler.RegistrationHandler
import company.ryzhkov.sh.util.Constants.ACCESS_DENIED
import company.ryzhkov.sh.util.toMessage
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

class Routes(
    private val registrationHandler: RegistrationHandler,
    private val accountHandler: AccountHandler
) {

    fun registrationRouter() = router {
        "/api/register".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                POST("/", registrationHandler::register)
            }
        }
    }

    fun userAreaRouter() = router {
        "/api/user_area".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("/username", accountHandler::username)
                GET("/account", accountHandler::account)
                PUT("/account", accountHandler::updateAccount)
                DELETE("/account", accountHandler::deleteAccount)
            }
        }
    }.filter { request, next ->
        request.principal()
            .flatMap {
                val authentication = it as Authentication
                if (authentication.isAuthenticated) next.handle(request)
                else ServerResponse
                    .status(HttpStatus.UNAUTHORIZED)
                    .bodyValue(ACCESS_DENIED.toMessage())
            }
    }
}
