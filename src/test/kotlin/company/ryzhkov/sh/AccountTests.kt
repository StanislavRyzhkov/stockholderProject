package company.ryzhkov.sh

import company.ryzhkov.sh.entity.Account
import company.ryzhkov.sh.entity.DeleteAccount
import company.ryzhkov.sh.entity.Message
import company.ryzhkov.sh.entity.UpdateAccount
import company.ryzhkov.sh.util.AccessConstants.ACCESS_DENIED
import company.ryzhkov.sh.util.PhoneNumberConstants.INVALID_PHONE_NUMBER_FORMAT
import company.ryzhkov.sh.util.UserConstants.USER_DELETED
import company.ryzhkov.sh.util.UserConstants.USER_UPDATED
import company.ryzhkov.sh.util.UsernameConstants.INVALID_USERNAME_OR_PASSWORD
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
class AccountTests(@Autowired val client: WebTestClient) {

    // token for admin
    val token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIlJPTEVfQURNSU4iLCJST0xFX1VTRVIiXSwiaWF0IjoxNTc3NzI1ODY3LCJleHAiOjE1ODAyMjU4Njd9.zLhbWaYnDurO8Expao7Bv_5Jk7iymZIypZR_rngPkE4"
    val badToken = "bad!"

    @Test
    fun username() {
        client.get().uri("/api/user_area/username")
            .header("Authorization", "Bearer $token")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Message>()
            .isEqualTo(Message("admin"))
    }

    @Test
    fun usernameBadToken() {
        client.get().uri("/api/user_area/username")
            .header("Authorization", "Bearer $badToken")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isUnauthorized
            .expectBody<Message>()
            .isEqualTo(Message(ACCESS_DENIED))
    }

    @Test
    fun account() {
        client.get().uri("/api/user_area/account")
            .header("Authorization", "Bearer $token")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Account>()
            .consumeWith {
                it.responseBody?.secondName == "Smith"
            }
    }

    @Test
    fun updateAccountUpdateFirstName() {
        val updateAccount = UpdateAccount("John", "Smith", "")
        val updateAccountBack = UpdateAccount("", "Smith", "")

        client.put().uri("/api/user_area/account")
            .bodyValue(updateAccount)
            .header("Authorization", "Bearer $token")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Message>()
            .isEqualTo(Message(USER_UPDATED))

        client.get().uri("/api/user_area/account")
            .header("Authorization", "Bearer $token")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Account>()
            .consumeWith {
                it.responseBody?.firstName == "John"
            }

        client.put().uri("/api/user_area/account")
            .bodyValue(updateAccountBack)
            .header("Authorization", "Bearer $token")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Message>()
            .isEqualTo(Message(USER_UPDATED))
    }

    @Test
    fun updateAccountUpdatePhoneNumber() {
        val updateAccount = UpdateAccount("", "Smith", "+7-900-000-0000")
        val updateAccountBack = UpdateAccount("", "Smith", "")

        client.put().uri("/api/user_area/account")
            .bodyValue(updateAccount)
            .header("Authorization", "Bearer $token")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Message>()
            .isEqualTo(Message(USER_UPDATED))

        client.get().uri("/api/user_area/account")
            .header("Authorization", "Bearer $token")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Account>()
            .consumeWith {
                it.responseBody?.phoneNumber == "+7-900-000-0000"
            }

        client.put().uri("/api/user_area/account")
            .bodyValue(updateAccountBack)
            .header("Authorization", "Bearer $token")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Message>()
            .isEqualTo(Message(USER_UPDATED))
    }

    @Test
    fun updateAccountUpdatePhoneNumberFail() {
        val updateAccount = UpdateAccount("", "Smith", "asd")

        client.put().uri("/api/user_area/account")
            .bodyValue(updateAccount)
            .header("Authorization", "Bearer $token")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<Message>()
            .isEqualTo(Message(INVALID_PHONE_NUMBER_FORMAT))
    }

    @Test
    fun deleteAccount() {
        val deleteAccount = DeleteAccount("admin", "admin", "admin")

        client.put().uri("/api/user_area/account/delete")
            .bodyValue(deleteAccount)
            .header("Authorization", "Bearer $token")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Message>()
            .isEqualTo(Message(USER_DELETED))
    }

    @Test
    fun deleteAccountWrongUsername() {
        val deleteAccount = DeleteAccount("bob", "admin", "admin")

        client.put().uri("/api/user_area/account/delete")
            .bodyValue(deleteAccount)
            .header("Authorization", "Bearer $token")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<Message>()
            .isEqualTo(Message(INVALID_USERNAME_OR_PASSWORD))
    }

    @Test
    fun deleteAccountWrongPassword() {
        val deleteAccount = DeleteAccount("admin", "qwerty", "qwerty")

        client.put().uri("/api/user_area/account/delete")
            .bodyValue(deleteAccount)
            .header("Authorization", "Bearer $token")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<Message>()
            .isEqualTo(Message(INVALID_USERNAME_OR_PASSWORD))
    }
}
