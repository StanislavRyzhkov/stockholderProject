package company.ryzhkov.sh.controller

import company.ryzhkov.sh.entity.CreateReply
import company.ryzhkov.sh.entity.Message
import company.ryzhkov.sh.entity.TextFull
import company.ryzhkov.sh.entity.TextInfo
import company.ryzhkov.sh.exception.AuthException
import company.ryzhkov.sh.exception.NotFoundException
import company.ryzhkov.sh.service.TextService
import company.ryzhkov.sh.util.Constants.ACCESS_DENIED
import company.ryzhkov.sh.util.Constants.REPLY_CREATED
import company.ryzhkov.sh.util.Constants.TEXT_NOT_FOUND
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = ["*"])
class ArticleController @Autowired constructor(
    private val textService: TextService
) {

    @GetMapping(value = ["all"])
    fun allArticles(): Flux<TextInfo> = textService.findAllArticles()

    @GetMapping(value = ["two"])
    fun newTwoArticles(): Flux<TextInfo> = textService.findAllArticles(number = 2)

    @GetMapping(value = ["detail/{englishTitle}"])
    fun getArticleByEnglishTitle(
        @PathVariable(value = "englishTitle") englishTitle: String
    ): Mono<TextFull> = textService
        .findFullTextByEnglishTitle(englishTitle)
        .switchIfEmpty(Mono.error(NotFoundException(TEXT_NOT_FOUND)))

    @PostMapping(value = ["reply"])
    @PreAuthorize(value = "hasRole('USER')")
    fun createReply(
        authenticationMono: Mono<Authentication>,
        @Valid @RequestBody createReplyMono: Mono<CreateReply>
    ): Mono<Message> = textService
        .createReply(authenticationMono, createReplyMono)
        .map { Message(REPLY_CREATED) }
        .switchIfEmpty(Mono.error(AuthException(ACCESS_DENIED)))
}
