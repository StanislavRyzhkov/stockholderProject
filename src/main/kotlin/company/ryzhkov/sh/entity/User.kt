package company.ryzhkov.sh.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import company.ryzhkov.sh.util.*
import company.ryzhkov.sh.util.EmailConstants.EMAIL_FIELD_IS_EMPTY
import company.ryzhkov.sh.util.EmailConstants.EMAIL_FIELD_TOO_LONG
import company.ryzhkov.sh.util.EmailConstants.INVALID_EMAIL
import company.ryzhkov.sh.util.FirstNameConstants.FIRST_NAME_TOO_LONG
import company.ryzhkov.sh.util.PasswordConstants.PASSWORDS_DO_NOT_MATCH
import company.ryzhkov.sh.util.PasswordConstants.PASSWORD_TOO_LONG
import company.ryzhkov.sh.util.PasswordConstants.PASSWORD_TOO_SHORT
import company.ryzhkov.sh.util.PhoneNumberConstants.INVALID_PHONE_NUMBER_FORMAT
import company.ryzhkov.sh.util.SecondNameConstants.SECOND_NAME_TOO_LONG
import company.ryzhkov.sh.util.UsernameConstants.USERNAME_FIELD_IS_EMPTY
import company.ryzhkov.sh.util.UsernameConstants.USERNAME_TOO_LONG
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.format.annotation.DateTimeFormat
import java.util.*

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

data class Register @JsonCreator constructor(
    @param:JsonProperty("username")
    val username: String,
    @param:JsonProperty("email")
    val email: String,
    @param:JsonProperty("password1")
    val password1: String,
    @param:JsonProperty("password2")
    val password2: String
)

fun Register.validate(): Register =
    Validator(this)
        .check(USERNAME_TOO_LONG) { it.username.validateMaxLength(100) }
        .check(USERNAME_FIELD_IS_EMPTY) { it.username.isNotEmpty() }
        .check(EMAIL_FIELD_TOO_LONG) { it.email.validateMaxLength(100) }
        .check(EMAIL_FIELD_IS_EMPTY) { it.email.isNotEmpty() }
        .check(INVALID_EMAIL) { it.email.validateAsEmail() }
        .check(PASSWORDS_DO_NOT_MATCH) { it.password1 == it.password2 }
        .check(PASSWORD_TOO_LONG) { it.password1.validateMaxLength(100) }
        .check(PASSWORD_TOO_SHORT) { it.password1.validateMinLenght(5) }
        .create()

data class Auth @JsonCreator constructor(
    @param:JsonProperty("username")
    val username: String,
    @param:JsonProperty("password")
    val password: String
)

data class Account constructor(
    val username: String,
    val email: String,
    val firstName: String,
    val secondName: String,
    val phoneNumber: String
)

data class UpdateAccount @JsonCreator constructor(
    @param:JsonProperty("firstName")
    val firstName: String,
    @param:JsonProperty("secondName")
    val secondName: String,
    @param:JsonProperty("phoneNumber")
    val phoneNumber: String
)

fun UpdateAccount.validate(): UpdateAccount =
    Validator(this)
        .check(FIRST_NAME_TOO_LONG) { it.firstName.validateMaxLength(100) }
        .check(SECOND_NAME_TOO_LONG) { it.secondName.validateMaxLength(100) }
        .check(INVALID_PHONE_NUMBER_FORMAT) { it.phoneNumber.validateAsPhoneNumber() }
        .create()

data class UpdateAccountWithUser(
    val firstName: String,
    val secondName: String,
    val phoneNumber: String,
    val user: User
)

data class DeleteAccount @JsonCreator constructor(
    @param:JsonProperty("username")
    val username: String,
    @param:JsonProperty("password1")
    val password1: String,
    @param:JsonProperty("password2")
    val password2: String
)

fun DeleteAccount.validate(): DeleteAccount =
    Validator(this)
        .check(PASSWORDS_DO_NOT_MATCH) { it.password1 == it.password2 }
        .create()

data class DeleteAccountWithUser(
    val username: String,
    val password1: String,
    val password2: String,
    val user: User
)

data class UpdatePassword @JsonCreator constructor(
    @param:JsonProperty("oldPassword")
    val oldPassword: String,
    @param:JsonProperty("newPassword1")
    val newPassword1: String,
    @param:JsonProperty("newPassword2")
    val newPassword2: String
)

data class UpdatePasswordWithUser(
    val oldPassword: String,
    val newPassword1: String,
    val newPassword2: String,
    val user: User
)

fun UpdatePassword.validate() =
    Validator(this)
        .check(PASSWORD_TOO_SHORT) { it.newPassword1.validateMinLenght(5) }
        .check(PASSWORDS_DO_NOT_MATCH) { it.newPassword1 == it.newPassword2 }
        .create()
