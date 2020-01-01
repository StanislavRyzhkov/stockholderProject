package company.ryzhkov.sh.entity

import org.springframework.web.bind.support.WebExchangeBindException

data class Message(val text: String?) {
    companion object {
        fun accessDenied(): Message = Message("Доступ запрещен")

        fun validationExcMessage(e: WebExchangeBindException) = Message(
            e.bindingResult.fieldErrors[0].defaultMessage
        )
    }
}
