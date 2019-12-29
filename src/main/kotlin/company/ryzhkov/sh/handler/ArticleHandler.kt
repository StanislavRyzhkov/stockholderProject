package company.ryzhkov.sh.handler

import company.ryzhkov.sh.service.TextService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

class ArticleHandler(private val textService: TextService) {

    fun all(serverRequest: ServerRequest): Mono<ServerResponse> =
        ok().body(textService.findAllArticles())

    fun two(serverRequest: ServerRequest): Mono<ServerResponse> =
        ok().body(textService.findAllArticles(2))

    fun findOne(serverRequest: ServerRequest): Mono<ServerResponse> =
        ok()
            .body(
                textService
                    .findFullTextByEnglishTitle(
                        serverRequest.pathVariable("englishTitle")
                    )
            )

    fun createReply(serverRequest: ServerRequest): Mono<ServerResponse> =
        TODO()
}
