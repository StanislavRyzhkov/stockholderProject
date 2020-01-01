package company.ryzhkov.sh.controller

import company.ryzhkov.sh.entity.Message
import company.ryzhkov.sh.entity.Recall
import company.ryzhkov.sh.service.RecallService
import company.ryzhkov.sh.util.Constants.RECALL_CREATED
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping(value = ["/api/recall"])
@CrossOrigin(origins = ["*"])
class RecallController @Autowired constructor(private val recallService: RecallService) {

    @PostMapping fun createRecall(
        @Valid @RequestBody createRecallMono: Mono<Recall>
    ): Mono<Message> = createRecallMono
        .flatMap { recallService.createRecall(it) }
        .map { Message(RECALL_CREATED) }
}
