package company.ryzhkov.sh.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "keys")
data class KeyElement(
    @field:Id val id: String? = null,
    val secretString: String
)
