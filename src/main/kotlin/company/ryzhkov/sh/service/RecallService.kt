package company.ryzhkov.sh.service

import company.ryzhkov.sh.entity.Recall
import company.ryzhkov.sh.repository.RecallRepository
import reactor.core.publisher.Mono

class RecallService(
    private val recallRepository: RecallRepository
) {

    fun createRecall(recall: Recall): Mono<Recall> = recallRepository.insert(recall)
}
