package company.ryzhkov.sh.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

abstract class JwtFilter
//(private val provider: TokenProvider)
    : WebFilter
//{

//    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> =
//        provider.getAuthentication(exchange.request)
//            .map { ReactiveSecurityContextHolder.withAuthentication(it) }
//            .flatMap { context -> chain.filter(exchange).subscriberContext(context) }
//}
