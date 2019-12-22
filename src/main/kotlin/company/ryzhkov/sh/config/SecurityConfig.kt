package company.ryzhkov.sh.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig {

    @Bean fun authorization(http: ServerHttpSecurity): SecurityWebFilterChain = http
        .httpBasic().disable()
        .csrf().disable()
        .authorizeExchange()
        .anyExchange().permitAll()
        .and().cors()
        .and().build()
}
