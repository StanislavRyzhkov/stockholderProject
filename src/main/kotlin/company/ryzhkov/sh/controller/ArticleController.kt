package company.ryzhkov.sh.controller

import company.ryzhkov.sh.entity.CreateReply
import company.ryzhkov.sh.entity.Message
import company.ryzhkov.sh.entity.TextFull
import company.ryzhkov.sh.entity.TextInfo
import company.ryzhkov.sh.service.TextService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
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
    ): Mono<TextFull> = textService.findFullTextByEnglishTitle(englishTitle)

    @PostMapping(value = ["reply"])
    @PreAuthorize(value = "hasRole('USER')")
    fun createReply(
        authenticationMono: Mono<Authentication>,
        @Valid @RequestBody createReplyMono: Mono<CreateReply>
    ): Mono<Message> = authenticationMono.zipWith(createReplyMono).flatMap { tuple ->
        val userDetails = tuple.t1.principal as UserDetails
        val createReply = tuple.t2
        textService.createReply(userDetails, createReply)
    }.map { Message(it) }
}
