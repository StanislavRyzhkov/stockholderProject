package company.ryzhkov.sh.config

import company.ryzhkov.sh.handler.AccountHandler
import company.ryzhkov.sh.repository.KeyElementRepository
import company.ryzhkov.sh.repository.UserRepository
import company.ryzhkov.sh.routes.AccountRoutes
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
    bean("accountRoutes") {
        AccountRoutes(ref())
    }
    bean {
        ref<AccountRoutes>().router()
    }
}

class BeansInitializer : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(context: GenericApplicationContext) =
        beans.initialize(context)
}
