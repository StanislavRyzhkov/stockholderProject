package company.ryzhkov.sh

import company.ryzhkov.sh.entity.Auth
import company.ryzhkov.sh.entity.Message
import company.ryzhkov.sh.util.UsernameConstants.INVALID_USERNAME_OR_PASSWORD
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

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class AuthTests(@Autowired val client: WebTestClient) {

    @Test
    fun auth() {
        val auth = Auth("admin", "admin")

        client.post().uri("/api/auth")
            .bodyValue(auth)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Message>().consumeWith {
                Assertions.assertTrue(it.responseBody!!.text!!.startsWith("e"))
            }
    }

    @Test
    fun authBadUsername() {
        val auth = Auth("asdkjskdjshkyu", "admin")

        client.post().uri("/api/auth")
            .bodyValue(auth)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<Message>()
            .isEqualTo(Message(INVALID_USERNAME_OR_PASSWORD))
    }

    @Test
    fun authBadPassword() {
        val auth = Auth("admin", "asfdsgdgh")

        client.post().uri("/api/auth")
            .bodyValue(auth)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<Message>()
            .isEqualTo(Message(INVALID_USERNAME_OR_PASSWORD))
    }
}
