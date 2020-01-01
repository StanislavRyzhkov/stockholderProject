package company.ryzhkov.sh.routes

import company.ryzhkov.sh.handler.*
import company.ryzhkov.sh.util.AccessConstants.ACCESS_DENIED
import company.ryzhkov.sh.util.toMessage
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.web.reactive.function.server.HandlerFilterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

class Routes(
    private val registrationHandler: RegistrationHandler,
    private val authHandler: AuthHandler,
    private val accountHandler: AccountHandler,
    private val articleHandler: ArticleHandler,
    private val recallHandler: RecallHandler
) {

    private val handlerFilterFunction =
        HandlerFilterFunction<ServerResponse, ServerResponse> { request, next ->
            request.principal().flatMap {
                val authentication = it as Authentication
                if (authentication.isAuthenticated) next.handle(request)
                else ServerResponse
                    .status(HttpStatus.UNAUTHORIZED)
                    .bodyValue(ACCESS_DENIED.toMessage())
            }
        }

    fun registrationRouter() = router {
        "/api/register".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                POST("/", registrationHandler::register)
            }
        }
    }

    fun authRouter() = router {
        "/api/auth".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                POST("/", authHandler::authenticate)
            }
        }
    }

    fun articleRouter() = router {
        "/api/articles".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("/all", articleHandler::all)
                GET("/two", articleHandler::two)
                GET("/detail/{englishTitle}", articleHandler::findOne)
            }
        }
    }

    fun replyRouter() = router {
        ".api/replies".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                POST("", articleHandler::createReply)
            }
        }
    }.filter(handlerFilterFunction)

    // It's not allowed to send a body with DELETE! So, we use PUT METHOD instead of it: PUT /account/delete
    fun userAreaRouter() = router {
        "/api/user_area".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("/username", accountHandler::username)
                GET("/account", accountHandler::account)
                PUT("/account", accountHandler::updateAccount)
                PUT("/account/delete", accountHandler::deleteAccount)
                PUT("/password", accountHandler::updatePassword)
            }
        }
    }.filter(handlerFilterFunction)

    fun recallRouter() = router {
        "/api/recall".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                POST("/", recallHandler::createRecall)
            }
        }
    }
}
