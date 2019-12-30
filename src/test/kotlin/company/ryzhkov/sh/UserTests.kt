package company.ryzhkov.sh


import company.ryzhkov.sh.entity.Message
import company.ryzhkov.sh.entity.Register
import company.ryzhkov.sh.util.UserConstants.USER_CREATED
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.util.*


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class UserTests(@Autowired var client: WebTestClient) {

    @Test
    fun registerNewUser() {
        val register = Register(
            username = "Bob100" + UUID.randomUUID().toString(),
            email = "bob@bob" + UUID.randomUUID().toString() + ".ru",
            password1 = "12345",
            password2 = "12345"
        )

        client.post().uri("/api/register")
            .bodyValue(register)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody<Message>()
            .isEqualTo(Message(USER_CREATED))
    }
}

