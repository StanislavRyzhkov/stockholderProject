package company.ryzhkov.sh.service

import company.ryzhkov.sh.entity.Recall
import company.ryzhkov.sh.repository.RecallRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service class RecallService @Autowired constructor(
    private val recallRepository: RecallRepository
) {

    fun createRecall(recall: Recall): Mono<Recall> = recallRepository.insert(recall)
}
