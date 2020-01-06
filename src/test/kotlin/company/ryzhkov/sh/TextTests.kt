package company.ryzhkov.sh

import company.ryzhkov.sh.entity.CreateReply
import company.ryzhkov.sh.entity.Message
import company.ryzhkov.sh.entity.TextFull
import company.ryzhkov.sh.entity.TextInfo
import company.ryzhkov.sh.util.AccessConstants.ACCESS_DENIED
import company.ryzhkov.sh.util.CreateReplyConstants.ARTICLE_NAME_IS_EMPTY
import company.ryzhkov.sh.util.CreateReplyConstants.REPLY_CREATED
import company.ryzhkov.sh.util.CreateReplyConstants.REPLY_IS_EMPTY
import company.ryzhkov.sh.util.CreateReplyConstants.WRONG_TEXT_NAME
import company.ryzhkov.sh.util.TextConstants.TEXT_NOT_FOUND
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TextTests(@Autowired val client: WebTestClient) {

    // token for admin
    val token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIlJ" +
        "PTEVfQURNSU4iLCJST0xFX1VTRVIiXSwiaWF0IjoxNTc3NzI1ODY3LCJleHAiOjE1OD" +
        "AyMjU4Njd9.zLhbWaYnDurO8Expao7Bv_5Jk7iymZIypZR_rngPkE4"
    val badToken = "bad!"

    @Test
    fun all() {
        client.get().uri("/api/articles/all")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList<TextInfo>()
            .hasSize(2)
    }

    @Test
    fun two() {
        client.get().uri("/api/articles/two")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList<TextInfo>()
            .hasSize(2)
    }

    @Test
    fun detail() {
        client.get().uri("/api/articles/detail/about_technical_analysis")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<TextFull>()
            .consumeWith {
                val body = it.responseBody
                Assertions.assertNotNull(body)
                Assertions.assertTrue(body!!.englishTitle == "about_technical_analysis")
            }
    }

    @Test
    fun detailNotFound() {
        client.get().uri("/api/articles/detail/qwerty")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectBody<Message>()
            .isEqualTo(Message(TEXT_NOT_FOUND))
    }

    @Test
    fun createReply() {
        val createReply = CreateReply("about_technical_analysis", "REPLY")

        client.post().uri("/api/replies")
            .bodyValue(createReply)
            .header("Authorization", "Bearer $token")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Message>()
            .isEqualTo(Message(REPLY_CREATED))
    }

    @Test
    fun createReplyBrokenToken() {
        val createReply = CreateReply("about_technical_analysis", "REPLY")

        client.post().uri("/api/replies")
            .bodyValue(createReply)
            .header("Authorization", "Bearer $badToken")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isUnauthorized
            .expectBody<Message>()
            .isEqualTo(Message(ACCESS_DENIED))
    }

    @Test
    fun createReplyNoToken() {
        val createReply = CreateReply("about_technical_analysis", "REPLY")

        client.post().uri("/api/replies")
            .bodyValue(createReply)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isUnauthorized
            .expectBody<Message>()
            .isEqualTo(Message(ACCESS_DENIED))
    }

    @Test
    fun createReplyBadArticle() {
        val createReply = CreateReply("qwerty", "REPLY")

        client.post().uri("/api/replies")
            .bodyValue(createReply)
            .header("Authorization", "Bearer $token")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<Message>()
            .isEqualTo(Message(WRONG_TEXT_NAME))
    }

    @Test
    fun createReplyEmptyEnglishTitle() {
        val createReply = CreateReply("", "REPLY")

        client.post().uri("/api/replies")
            .bodyValue(createReply)
            .header("Authorization", "Bearer $token")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<Message>()
            .isEqualTo(Message(ARTICLE_NAME_IS_EMPTY))
    }

    @Test
    fun createReplyEmptyContent() {
        val createReply = CreateReply("about_technical_analysis", "")

        client.post().uri("/api/replies")
            .bodyValue(createReply)
            .header("Authorization", "Bearer $token")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<Message>()
            .isEqualTo(Message(REPLY_IS_EMPTY))
    }
}
