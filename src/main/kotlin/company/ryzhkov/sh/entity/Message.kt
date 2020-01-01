package company.ryzhkov.sh.entity

import org.springframework.web.bind.support.WebExchangeBindException
import java.io.Serializable

data class Message(val text: String?) : Serializable {
    companion object {
        fun accessDenied(): Message = Message("Доступ запрещен")

        fun validationExcMessage(e: WebExchangeBindException) = Message(
            e.bindingResult.fieldErrors[0].defaultMessage
        )
    }
}
