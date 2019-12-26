package company.ryzhkov.sh.security

import company.ryzhkov.sh.exception.AuthException
import company.ryzhkov.sh.repository.KeyElementRepository
import company.ryzhkov.sh.util.Constants.ACCESS_DENIED
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec

class TokenProvider(
    private val userService: ReactiveUserDetailsService,
    private val keyElementRepository: KeyElementRepository
) {

    private var key: Key? = null

    fun init() {
        keyElementRepository.findAll().take(1).subscribe { (_, secretString) ->
            val bytes = Base64.getDecoder().decode(secretString)
            key = SecretKeySpec(bytes, SignatureAlgorithm.HS256.jcaName)
        }
    }

    fun getAuthentication(request: ServerRequest): Mono<out Authentication> = getUsername(request)
        .flatMap { userService.findByUsername(it) }
        .doOnNext { println(it) }
        .map {userDetails -> UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities) }
        .onErrorResume { Mono.just(UsernamePasswordAuthenticationToken("", "")) }

    fun createToken(authentication: Authentication): String {
        val username = (authentication.principal as UserDetails).username
        val authorities = authentication.authorities.map { it.authority }
        return createToken(username, authorities)
    }

    fun createToken(username: String): String = createToken(username, listOf("ROLE_USER"))

    private fun createToken(username: String, roles: List<String>): String {
        val claims = Jwts.claims().setSubject(username)
        claims["roles"] = roles
        val now = Date()
        val expired = 2500000000L
        val validity = Date(now.time + expired)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(key)
            .compact()
    }

    fun getUsername(request: ServerRequest): Mono<String> = Mono
        .fromCallable {
            val list = request.headers().header("Authorization")
            if (list.isEmpty()) {
                throw AuthException(ACCESS_DENIED)
            }
            val authHeader = list[0]
            if (authHeader == null || !authHeader.startsWith("Bearer")) {
                throw AuthException(ACCESS_DENIED)
            }
            val token = authHeader.substring("Bearer".length).trim()
            Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .body
                .subject
        }
        .onErrorMap { AuthException(ACCESS_DENIED) }
        .subscribeOn(Schedulers.elastic())
}
