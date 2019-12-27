package company.ryzhkov.sh.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import company.ryzhkov.sh.util.AccountPhoneNumberMatch
import company.ryzhkov.sh.util.Constants.INVALID_LENGTH
import company.ryzhkov.sh.util.Constants.INVALID_PHONE_NUMBER_LENGTH
import company.ryzhkov.sh.util.Constants.INVALID_SECOND_NAME
import company.ryzhkov.sh.util.Constants.PASSWORD_FIELD_IS_EMPTY
import company.ryzhkov.sh.util.EmailConstants.EMAIL_FIELD_IS_EMPTY
import company.ryzhkov.sh.util.EmailConstants.EMAIL_FIELD_TOO_LONG
import company.ryzhkov.sh.util.EmailConstants.INVALID_EMAIL
import company.ryzhkov.sh.util.PasswordConstants.PASSWORDS_DO_NOT_MATCH
import company.ryzhkov.sh.util.PasswordConstants.PASSWORD_TOO_LONG
import company.ryzhkov.sh.util.PasswordConstants.PASSWORD_TOO_SHORT
import company.ryzhkov.sh.util.PasswordRepeatMatch
import company.ryzhkov.sh.util.UsernameConstants.USERNAME_FIELD_IS_EMPTY
import company.ryzhkov.sh.util.UsernameConstants.USERNAME_TOO_LONG
import company.ryzhkov.sh.util.Validator
import company.ryzhkov.sh.util.validateAsEmail
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.format.annotation.DateTimeFormat
import java.io.Serializable
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Document(collection = "users")
data class User(
    @field:Id val id: String? = null,
    val username: String,
    val email: String,
    val password: String,
    val firstName: String = "",
    val secondName: String = "",
    val phoneNumber: String = "",
    val status: String = "ACTIVE",
    val roles: List<String>,
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val created: Date = Date()
)

fun User.toAccount() = Account(
    this.username,
    this.email,
    this.firstName,
    this.secondName,
    this.phoneNumber
)

fun Register.validate(): Register =
    Validator(this)
        .check(USERNAME_TOO_LONG) { it.username.length < 64 }
        .check(USERNAME_FIELD_IS_EMPTY) { it.username.isNotEmpty() }
        .check(EMAIL_FIELD_TOO_LONG) { it.email.length < 64 }
        .check(EMAIL_FIELD_IS_EMPTY) { it.email.isNotEmpty() }
        .check(INVALID_EMAIL) { it.email.validateAsEmail() }
        .check(PASSWORDS_DO_NOT_MATCH) { it.password1 == it.password2 }
        .check(PASSWORD_TOO_LONG) { it.password1.length < 64 }
        .check(PASSWORD_TOO_SHORT) { it.password1.length > 4 }
        .create()

data class Register @JsonCreator constructor(
    @param:JsonProperty("username")
    val username: String,
    @param:JsonProperty("email")
    val email: String,
    @param:JsonProperty("password1")
    val password1: String,
    @param:JsonProperty("password2")
    val password2: String
) {

    override fun toString(): String =
        "Register ($username, $email, $password1, $password2)"
}

data class Auth @JsonCreator constructor(
    @param:JsonProperty("username")
    @field:NotBlank(message = USERNAME_FIELD_IS_EMPTY)
    @field:Size(max = 64, message = USERNAME_TOO_LONG)
    val username: String,

    @param:JsonProperty("password")
    @field:NotBlank(message = PASSWORD_FIELD_IS_EMPTY)
    @field:Size(max = 64, message = PASSWORD_TOO_LONG)
    val password: String
)

data class Account constructor(
    val username: String,
    val email: String,
    val firstName: String,
    val secondName: String,
    val phoneNumber: String
) : Serializable {

    companion object {
        fun createInstance(user: User): Account = Account(
            user.username,
            user.email,
            user.firstName,
            user.secondName,
            user.phoneNumber
        )
    }
}

@AccountPhoneNumberMatch(fieldName = "phoneNumber")
data class UpdateAccount @JsonCreator constructor(
    @param:JsonProperty("firstName")
    @field:NotNull
    @field:Size(max = 120, message = "")
    val firstName: String,

    @param:JsonProperty("secondName")
    @field:NotNull
    @field:Size(max = 120, message = INVALID_SECOND_NAME)
    val secondName: String,

    @param:JsonProperty("phoneNumber")
    @field:NotNull
    @field:Size(max = 120, message = INVALID_PHONE_NUMBER_LENGTH)
    val phoneNumber: String
)

@PasswordRepeatMatch(first = "password1", second = "password2")
data class DeleteAccount @JsonCreator constructor(
    @param:JsonProperty("username")
    @field:NotBlank(message = USERNAME_FIELD_IS_EMPTY)
    @field:Size(max = 64, message = USERNAME_TOO_LONG)
    val username: String,

    @param:JsonProperty("password1")
    @field:NotBlank(message = PASSWORD_FIELD_IS_EMPTY)
    @field:Size(min = 5, max = 64, message = INVALID_LENGTH)
    val password1: String,

    @param:JsonProperty("password2")
    @field:NotBlank(message = PASSWORD_FIELD_IS_EMPTY)
    @field:Size(min = 5, max = 64, message = INVALID_LENGTH)
    val password2: String
)

@PasswordRepeatMatch(first = "newPassword1", second = "newPassword2")
data class UpdatePassword @JsonCreator constructor(
    @param:JsonProperty("oldPassword")
    @field:NotBlank(message = PASSWORD_FIELD_IS_EMPTY)
    @field:Size(min = 5, max = 64, message = INVALID_LENGTH)
    val oldPassword: String,

    @param:JsonProperty("newPassword1")
    @field:NotBlank(message = PASSWORD_FIELD_IS_EMPTY)
    @field:Size(min = 5 , max = 64, message = INVALID_LENGTH)
    val newPassword1: String,

    @param:JsonProperty("newPassword2")
    @field:NotBlank(message = PASSWORD_FIELD_IS_EMPTY)
    @field:Size(min = 5, max = 64, message = INVALID_LENGTH)
    val newPassword2: String
)
