package company.ryzhkov.sh


import company.ryzhkov.sh.entity.Message
import company.ryzhkov.sh.entity.Register
import company.ryzhkov.sh.util.EmailConstants.EMAIL_ALREADY_EXISTS
import company.ryzhkov.sh.util.PasswordConstants.PASSWORDS_DO_NOT_MATCH
import company.ryzhkov.sh.util.PasswordConstants.PASSWORD_TOO_SHORT
import company.ryzhkov.sh.util.UserConstants.USER_ALREADY_EXISTS
import company.ryzhkov.sh.util.UserConstants.USER_CREATED
import company.ryzhkov.sh.util.UsernameConstants.USERNAME_FIELD_IS_EMPTY
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

    @Test
    fun registerUsernameExists() {
        val register = Register(
            username = "admin",
            email = "bob@bob" + UUID.randomUUID().toString() + ".ru",
            password1 = "12345",
            password2 = "12345"
        )

        client.post().uri("/api/register")
            .bodyValue(register)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<Message>()
            .isEqualTo(Message(USER_ALREADY_EXISTS))
    }

    @Test
    fun registerEmailExists() {
        val register = Register(
            username = "admin" + UUID.randomUUID().toString(),
            email = "rvmail@mail.ru",
            password1 = "12345",
            password2 = "12345"
        )

        client.post().uri("/api/register")
            .bodyValue(register)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<Message>()
            .isEqualTo(Message(EMAIL_ALREADY_EXISTS))
    }

    @Test
    fun registerShortPassword() {
        val register = Register(
            username = "admin" + UUID.randomUUID().toString(),
            email = "bob@bob" + UUID.randomUUID().toString() + ".ru",
            password1 = "1234",
            password2 = "1234"
        )

        client.post().uri("/api/register")
            .bodyValue(register)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<Message>()
            .isEqualTo(Message(PASSWORD_TOO_SHORT))
    }

    @Test
    fun registerPasswordsNotMatch() {
        val register = Register(
            username = "admin" + UUID.randomUUID().toString(),
            email = "bob@bob" + UUID.randomUUID().toString() + ".ru",
            password1 = "12345",
            password2 = "1234567890"
        )

        client.post().uri("/api/register")
            .bodyValue(register)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<Message>()
            .isEqualTo(Message(PASSWORDS_DO_NOT_MATCH))
    }

    @Test
    fun registerEmptyField() {
        val register = Register(
            username = "",
            email = "bob@bob" + UUID.randomUUID().toString() + ".ru",
            password1 = "12345",
            password2 = "1234567890"
        )

        client.post().uri("/api/register")
            .bodyValue(register)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<Message>()
            .isEqualTo(Message(USERNAME_FIELD_IS_EMPTY))
    }
}

