package company.ryzhkov.sh.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import company.ryzhkov.sh.util.Constants.INVALID_LENGTH
import company.ryzhkov.sh.util.EmailConstants.INVALID_EMAIL
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.format.annotation.DateTimeFormat
import java.util.*
import javax.validation.constraints.Email
import javax.validation.constraints.Size

@Document(collection = "recalls")
data class Recall @JsonCreator constructor(
    @field:Id val id: String? = null,

    @param:JsonProperty("author")
    @field:Size(min = 1, max = 120, message = INVALID_LENGTH)
    val author: String,

    @param:JsonProperty("email") @field:Email(message = INVALID_EMAIL)
    @field:Size(min = 1, max = 120, message = INVALID_LENGTH)
    val email: String,

    @param:JsonProperty("topic")
    @field:Size(min = 1, max = 120, message = INVALID_LENGTH)
    val topic: String,

    @param:JsonProperty("text")
    @field:Size(min = 1, max = 4500, message = INVALID_LENGTH)
    val text: String,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val created: Date = Date()
) {

    override fun toString(): String = "Author $author, topic $topic\n"
}
