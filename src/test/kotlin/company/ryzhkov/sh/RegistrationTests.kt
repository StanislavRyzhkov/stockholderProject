package company.ryzhkov.sh

import company.ryzhkov.sh.entity.*
import company.ryzhkov.sh.repository.TextRepository
import company.ryzhkov.sh.repository.UserRepository
import company.ryzhkov.sh.security.JwtFilter
import company.ryzhkov.sh.service.UserService
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.WebFilter
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@WebFluxTest(UserService::class)
class RegistrationTests(private val jwtFilter: JwtFilter) {

    @Autowired
    lateinit var context: ApplicationContext

    @MockBean
    lateinit var textRepository: TextRepository

    @MockBean
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var webTestClient: WebTestClient

    private val client = WebClient.create()

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

    private val textComponent = TextComponent(1, "a", "a", "a")
    private val textComponent2 = TextComponent(2, "a", "a", "a")
    private val textComponent3 = TextComponent(3, "a", "a", "a")
    private val textComponent4 = TextComponent(4, "a", "a", "a")
    private val textComponent5 = TextComponent(5, "a", "a", "a")

    private val article = Text(
        title = "bar",
        englishTitle = "bar",
        kind = "baz",
        textComponents = listOf(
            textComponent,
            textComponent2,
            textComponent3,
            textComponent4,
            textComponent5
        )
    )

    private val users = listOf(user1)
    private val texts = Flux.fromIterable(mutableListOf(article))

    @Test
    fun testRegister() {
        val bar: Mono<User> = Mono.empty()

        Mockito.`when`(textRepository.findByKindOrderByCreatedDesc("ARTICLE")).thenReturn(texts)

        webTestClient.get().uri("/api/articles/all")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun bar() {
        val bar: Mono<User> = Mono.empty()

        Mockito.`when`(userRepository.insert(user1)).thenReturn(Mono.just(user1))
        Mockito.`when`(userRepository.findByEmail("bob@bob.ru")).thenReturn(bar)
        Mockito.`when`(userRepository.findByUsername("Bob")).thenReturn(bar)
        Mockito.`when`(userRepository.findByUsernameAndStatus("Bob", "ACTIVE")).thenReturn(bar)

        val rest: WebTestClient = WebTestClient
            .bindToApplicationContext(context)
//            .webFilter<>()
            .build()

        rest
            .post().uri("/api/register")
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromObject(register))
            .exchange()
            .expectStatus().isOk
    }
}

