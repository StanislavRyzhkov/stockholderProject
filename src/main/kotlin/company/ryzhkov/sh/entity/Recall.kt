package company.ryzhkov.sh.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import company.ryzhkov.sh.util.EmailConstants.INVALID_EMAIL
import company.ryzhkov.sh.util.RecallConstants.AUTHOR_IS_EMPTY
import company.ryzhkov.sh.util.RecallConstants.AUTHOR_TOO_LONG
import company.ryzhkov.sh.util.RecallConstants.EMAIL_IS_EMPTY
import company.ryzhkov.sh.util.RecallConstants.TEXT_IS_EMPTY
import company.ryzhkov.sh.util.RecallConstants.TEXT_TOO_LONG
import company.ryzhkov.sh.util.RecallConstants.TOPIC_IS_EMPTY
import company.ryzhkov.sh.util.RecallConstants.TOPIC_TOO_LONG
import company.ryzhkov.sh.util.Validator
import company.ryzhkov.sh.util.validateAsEmail
import company.ryzhkov.sh.util.validateMaxLength
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.format.annotation.DateTimeFormat
import java.util.*

@Document(collection = "recalls")
data class Recall @JsonCreator constructor(
    @field:Id val id: String? = null,

    @param:JsonProperty("author")
    val author: String,

    @param:JsonProperty("email")
    val email: String,

    @param:JsonProperty("topic")
    val topic: String,

    @param:JsonProperty("text")
    val text: String,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val created: Date = Date()
) {

    override fun toString(): String = "Author $author, topic $topic\n"
}

fun Recall.validate(): Recall =
    Validator(this)
        .check(AUTHOR_TOO_LONG) { it.author.validateMaxLength(120) }
        .check(AUTHOR_IS_EMPTY) { it.author.isNotEmpty() }
        .check(INVALID_EMAIL) { it.email.validateAsEmail() }
        .check(EMAIL_IS_EMPTY) { it.email.isNotEmpty() }
        .check(TOPIC_TOO_LONG) { it.topic.validateMaxLength(1000) }
        .check(TOPIC_IS_EMPTY) { it.topic.isNotEmpty() }
        .check(TEXT_TOO_LONG) { it.text.validateMaxLength(4500) }
        .check(TEXT_IS_EMPTY) { it.text.isNotEmpty() }
        .create()
