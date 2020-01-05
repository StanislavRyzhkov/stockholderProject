package company.ryzhkov.sh

import company.ryzhkov.sh.entity.TextInfo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TextTests(@Autowired val client: WebTestClient) {

    @Test
    fun all() {
        client.get().uri("/api/articles/all")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList<TextInfo>()
            .hasSize(2)
    }
}
