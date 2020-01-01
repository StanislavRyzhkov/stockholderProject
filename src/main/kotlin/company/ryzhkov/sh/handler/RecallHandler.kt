package company.ryzhkov.sh.handler

import company.ryzhkov.sh.entity.Recall
import company.ryzhkov.sh.entity.validate
import company.ryzhkov.sh.exception.CustomException
import company.ryzhkov.sh.service.RecallService
import company.ryzhkov.sh.util.RecallConstants.RECALL_CREATED
import company.ryzhkov.sh.util.toMessage
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

class RecallHandler(private val recallService: RecallService) {

    fun createRecall(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest
            .bodyToMono(Recall::class.java)
            .map { it.validate() }
            .flatMap { recallService.createRecall(it) }
            .flatMap { ok().bodyValue(RECALL_CREATED.toMessage()) }
            .onErrorResume (CustomException::class.java) {
                ServerResponse.badRequest().bodyValue(it.message.toMessage())
            }
}
