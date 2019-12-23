package company.ryzhkov.sh.service

import company.ryzhkov.sh.entity.*
import company.ryzhkov.sh.repository.TextRepository
import company.ryzhkov.sh.util.fix
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.nio.charset.StandardCharsets
import java.nio.file.Files.readAllBytes
import java.nio.file.Files.readAllLines
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream
import javax.annotation.PostConstruct

@Service class TextService @Autowired constructor(
    private val textRepository: TextRepository,
    private val applicationArguments: ApplicationArguments
) {

    private val log: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(TextService::class.java)

    fun findFullTextByEnglishTitle(englishTitle: String): Mono<TextFull> = textRepository
        .findByEnglishTitle(englishTitle)
        .map { TextFull.createInstance(it) }

    fun findAllArticles(): Flux<TextInfo> = textRepository
        .findByKindOrderByCreatedDesc("ARTICLE")
        .map { TextInfo.createInstance(it) }

    fun findAllArticles(number: Int): Flux<TextInfo> = textRepository
        .findByKindOrderByCreatedDesc(
            "ARTICLE",
            PageRequest.of(0, number)
        )
        .map { TextInfo.createInstance(it) }

    fun createReply(authMono: Mono<Authentication>, createReplyMono: Mono<CreateReply>): Mono<Text> = Mono
        .zip(authMono, createReplyMono)
        .flatMap {
            val user = it.t1.fix()
            val (englishTitle, content) = it.t2
            val textMono = textRepository.findByEnglishTitle(englishTitle)
            val newReply = Reply(username = user.username, content = content, created = Date())
            textMono.flatMap { article ->
                val replies = article.replies
                replies.add(newReply)
                val updatedArticle = article.copy(replies = replies)
                textRepository.save(updatedArticle)
            }
        }

    @PostConstruct fun createText() {
        if ("--article" in applicationArguments.sourceArgs) createText("ARTICLE")
        if ("--text" in applicationArguments.sourceArgs) createText("TEXT")
    }

    private fun createText(kind: String) {
        val titles = readAllLines(Paths.get("a1.txt"))
        val bytes = readAllBytes(Paths.get("a2.txt"))
        val dividedText = String(bytes, StandardCharsets.UTF_8).split("\n\n")
        val textComponents = IntStream
            .range(0, dividedText.size)
            .mapToObj { i ->
                val arr = dividedText[i].split("\n")
                TextComponent(
                    number = i,
                    tag = arr[0],
                    source = arr[1],
                    content = arr[2]
                )
            }
            .collect(Collectors.toList())

        val text = Text(
            title = titles[0],
            englishTitle = titles[1],
            kind = kind,
            textComponents = textComponents
        )

        textRepository
            .insert(text)
            .subscribeOn(Schedulers.elastic())
            .subscribe { log.info("Text {} created", it.title) }
    }
}
