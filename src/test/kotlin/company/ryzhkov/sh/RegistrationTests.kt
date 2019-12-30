package company.ryzhkov.sh

import company.ryzhkov.sh.entity.Register
import company.ryzhkov.sh.entity.User
import company.ryzhkov.sh.repository.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Mono

@WebFluxTest
@ExtendWith(SpringExtension::class)
class RegistrationTests {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockBean
    lateinit var userRepository: UserRepository


    private val user1 = User(
        id=null,
        username = "Bob",
        email = "bob@bob.ru",
        password = "\$2a\$10\$q.q2/8VntJJZ2ocgUkjP5eKuWSxSCikZKKb3zJq80V3Qdpk76OPlG",
        firstName = "",
        secondName = "",
        phoneNumber = "",
        roles = listOf("ROLE_USER")
    )

    private val register = Register(
        username = "Bob",
        email = "bob@bob.ru",
        password1 = "12345",
        password2 = "12345"
    )

    private val users = listOf(user1)

    @Test
    fun testRegister() {
        val bar: Mono<User> = Mono.empty()

        Mockito.`when`(userRepository.insert(user1)).thenReturn(Mono.just(user1))
        Mockito.`when`(userRepository.findByUsername("Bob")).thenReturn(bar)
        Mockito.`when`(userRepository.findByUsernameAndStatus("Bob", "ACTIVE")).thenReturn(bar)
        Mockito.`when`(userRepository.findByEmail("bob@bob.ru")).thenReturn(bar)

        webTestClient.post().uri("/api/register")
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromObject(register))
            .exchange()
            .expectStatus().isOk
    }
}

