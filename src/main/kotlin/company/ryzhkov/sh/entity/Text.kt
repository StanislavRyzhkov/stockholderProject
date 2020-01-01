package company.ryzhkov.sh.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.format.annotation.DateTimeFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Document(collection = "texts")
data class Text(
    @field:Id val id: String?= null,
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val created: Date = Date(),
    val textComponents: List<TextComponent> = emptyList(),
    val replies: SortedSet<Reply> = sortedSetOf(),
    val title: String,
    val englishTitle: String,
    val kind: String
)

data class TextComponent(
    val number: Int,
    val tag: String,
    val content: String,
    val source: String
) : Comparable<TextComponent> {
    override fun compareTo(other: TextComponent): Int = number - other.number
}

data class Reply(
    val username: String,
    val content: String,
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val created: Date
) : Comparable<Reply> {
    override fun compareTo(other: Reply): Int = -created.compareTo(other.created)
}

data class ReplyStringDate(
    val username: String,
    val content: String,
    val created: String,
    val date: Date
) : Comparable<ReplyStringDate> {
    override fun compareTo(other: ReplyStringDate): Int = -this.date.compareTo(other.date)

    companion object {
        fun createInstance(reply: Reply): ReplyStringDate {
            val (username, content, created) = reply
            val sdf = SimpleDateFormat("dd-MM-yyyy")
            return ReplyStringDate(username, content, sdf.format(created), created)
        }
    }
}

data class TextFull(
    val created: String,
    val title: String,
    val englishTitle: String,
    val textComponents: List<TextComponent>,
    val replies: SortedSet<ReplyStringDate>
) {
    companion object {
        fun createInstance(text: Text): TextFull {
            val sdf = SimpleDateFormat("dd-MM-yyyy")
            val (_, created, textComponents, replies, title, englishTitle, _) = text
            val repliesWithStringDate = replies
                .map { ReplyStringDate.createInstance(it) }
                .toSortedSet()

            return TextFull(
                created = sdf.format(created),
                title = title,
                englishTitle = englishTitle,
                textComponents = textComponents,
                replies = repliesWithStringDate
            )
        }
    }
}

data class TextInfo(
    val mainImage: String,
    val firstParagraph: String,
    val title: String,
    val englishTitle: String,
    val created: String
) {
    companion object {
        fun createInstance(text: Text): TextInfo {
            val (_, created, textComponents, _, title, englishTitle, _) = text
            val sdf = SimpleDateFormat("dd-MM-yyyy")
            return TextInfo(
                mainImage = textComponents[1].source,
                firstParagraph = textComponents[3].content,
                title = title,
                englishTitle = englishTitle,
                created = sdf.format(created)
            )
        }
    }
}

data class CreateReply @JsonCreator constructor(
    @param:JsonProperty("englishTitle")
    @field:NotBlank
    @field:Size(max = 1000, message = "Некорректный ввод")
    val englishTitle: String,

    @param:JsonProperty("content")
    @field:NotBlank
    @field:Size(max = 1000, message = "Размер сообщения превышает 1000 символов")
    val content: String
)
