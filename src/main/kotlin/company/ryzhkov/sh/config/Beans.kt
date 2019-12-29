package company.ryzhkov.sh.config

import company.ryzhkov.sh.handler.AccountHandler
import company.ryzhkov.sh.handler.ArticleHandler
import company.ryzhkov.sh.handler.AuthHandler
import company.ryzhkov.sh.handler.RegistrationHandler
import company.ryzhkov.sh.repository.KeyElementRepository
import company.ryzhkov.sh.repository.UserRepository
import company.ryzhkov.sh.routes.Routes
import company.ryzhkov.sh.security.CustomReactiveAuthenticationManager
import company.ryzhkov.sh.security.JwtFilter
import company.ryzhkov.sh.security.TokenProvider
import company.ryzhkov.sh.service.UserService
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans

val beans = beans {
    bean<SecurityConfig>("securityConfig")
    bean("bCryptPasswordEncoder") {
        ref<SecurityConfig>().encoder()
    }
    bean {
        ref<SecurityConfig>().authorization(ref())
    }
    bean<MongoConfig>("mongoConfig")
    bean {
        ref<MongoConfig>("mongoConfig").reactiveMongoClient()
    }
    bean<AppConfig>("appConfig")
    bean<UserRepository>("userRepository")
    bean<KeyElementRepository>("keyElementRepository")
    bean("userService") {
        val userService = UserService(ref(), ref(), ref())
        userService.createAdminUser()
        userService
    }
    bean {
        CustomReactiveAuthenticationManager(ref(), ref())
    }
    bean("tokenProvider") {
        val tokenProvider = TokenProvider(ref(), ref())
        tokenProvider.init()
        tokenProvider
    }
    bean("jwtFilter") {
        JwtFilter(ref())
    }
    bean("accountHandler") {
        AccountHandler(ref())
    }
    bean("registrationHandler") {
        RegistrationHandler(ref())
    }
    bean("authHandler") {
        AuthHandler(ref(), ref())
    }
    bean("articleHandler") {
        ArticleHandler(ref())
    }
    bean("Routes") {
        Routes(ref(), ref(), ref(), ref())
    }
    bean("userAreaRouter") {
        ref<Routes>().userAreaRouter()
    }
    bean("registrationRouter") {
        ref<Routes>().registrationRouter()
    }
    bean("authRouter") {
        ref<Routes>().authRouter()
    }
    bean("articlesRouter") {
        ref<Routes>().articleRouter()
    }
}

class BeansInitializer : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(context: GenericApplicationContext) =
        beans.initialize(context)
}
