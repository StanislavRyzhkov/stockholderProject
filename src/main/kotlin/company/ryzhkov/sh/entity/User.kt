package company.ryzhkov.sh.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import company.ryzhkov.sh.util.AccountPhoneNumberMatch
import company.ryzhkov.sh.util.Constants.EMAIL_FIELD_IS_EMPTY
import company.ryzhkov.sh.util.Constants.EMAIL_TOO_LONG
import company.ryzhkov.sh.util.Constants.INVALID_EMAIL
import company.ryzhkov.sh.util.Constants.INVALID_FIRST_NAME
import company.ryzhkov.sh.util.Constants.INVALID_LENGTH
import company.ryzhkov.sh.util.Constants.INVALID_PHONE_NUMBER_LENGTH
import company.ryzhkov.sh.util.Constants.INVALID_SECOND_NAME
import company.ryzhkov.sh.util.Constants.PASSWORD_FIELD_IS_EMPTY
import company.ryzhkov.sh.util.Constants.PASSWORD_TOO_LONG
import company.ryzhkov.sh.util.Constants.USERNAME_FIELD_IS_EMPTY
import company.ryzhkov.sh.util.Constants.USERNAME_TOO_LONG
import company.ryzhkov.sh.util.PasswordRepeatMatch
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.format.annotation.DateTimeFormat
import java.io.Serializable
import java.util.*
import javax.validation.constraints.Email
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

@PasswordRepeatMatch(first = "password1", second = "password2")
data class Register @JsonCreator constructor(
    @param:JsonProperty("username")
    @field:NotBlank(message = USERNAME_FIELD_IS_EMPTY)
    @field:Size(max = 64, message = USERNAME_TOO_LONG)
    val username: String,

    @param:JsonProperty("email")
    @field:NotBlank(message = EMAIL_FIELD_IS_EMPTY)
    @field:Email(message = INVALID_EMAIL)
    @field:Size(max = 64, message = EMAIL_TOO_LONG)
    val email: String,

    @param:JsonProperty("password1")
    @field:NotBlank(message = PASSWORD_FIELD_IS_EMPTY)
    @field:Size(max = 64, min = 5, message = INVALID_LENGTH)
    val password1: String,

    @param:JsonProperty("password2")
    @field:NotBlank(message = PASSWORD_FIELD_IS_EMPTY)
    @field:Size(max = 64, min = 5, message = INVALID_LENGTH)
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
    @field:Size(max = 120, message = INVALID_FIRST_NAME)
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
