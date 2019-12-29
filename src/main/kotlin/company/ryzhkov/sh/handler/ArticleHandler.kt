package company.ryzhkov.sh.handler

import company.ryzhkov.sh.exception.CustomException
import company.ryzhkov.sh.service.TextService
import company.ryzhkov.sh.util.toMessage
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
        textService
            .findFullTextByEnglishTitle(serverRequest.pathVariable("englishTitle"))
            .flatMap { ok().bodyValue(it) }
            .onErrorResume (CustomException::class.java) {
                ServerResponse.status(404).bodyValue(it.message.toMessage())
            }

    fun createReply(serverRequest: ServerRequest): Mono<ServerResponse> =
        TODO()
}
