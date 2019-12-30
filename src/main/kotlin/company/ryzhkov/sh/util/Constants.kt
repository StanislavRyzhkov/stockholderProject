package company.ryzhkov.sh.util

object UsernameConstants {
    const val USERNAME_TOO_LONG = "Слишком длинное имя пользователя (не более 64 символов)"
    const val USERNAME_FIELD_IS_EMPTY = "Не заполнено поле \"Имя пользователя\""
    const val INVALID_USERNAME_OR_PASSWORD = "Неправильное имя пользователя или пароль"
}

object PasswordConstants {
    const val PASSWORDS_DO_NOT_MATCH = "Пароли не совпадают"
    const val PASSWORD_TOO_LONG = "Слишком длинный пароль (не более 64 символов)"
    const val PASSWORD_TOO_SHORT = "Слишком короткий пароль (не менее 5 символов)"
    const val PASSWORD_UPDATED = "Пароль изменен"
    const val INVALID_PASSWORD = "Неправильный пароль"
}

object EmailConstants {
    const val EMAIL_FIELD_IS_EMPTY = "Не заполнено поле \"email\""
    const val EMAIL_FIELD_TOO_LONG = "Слишком длинное поле \"email\""
    const val INVALID_EMAIL = "Некорректный email!"
    const val EMAIL_ALREADY_EXISTS = "Пользователь с таким email существует"
}

object UserConstants {
    const val USER_CREATED = "Успешная регистрация"
    const val USER_UPDATED = "Пользователь изменен"
    const val USER_DELETED = "Пользователь удален"
    const val USER_NOT_FOUND = "Пользователь не найден"
    const val USER_ALREADY_EXISTS = "Пользователь с таким именем уже существует"
}

object FirstNameConstants {
    const val FIRST_NAME_TOO_LONG = "Имя не должно быть более 100 символов"
}

object SecondNameConstants {
    const val SECOND_NAME_TOO_LONG = "Фамилия не должна быть более 100 символов"
}

object PhoneNumberConstants {
    const val INVALID_PHONE_NUMBER_FORMAT = "Некорректный формат номера телефона"
}

object CreateReplyConstants {
    const val INVALID_INPUT = "Некорректный ввод"
    const val INVALID_REPLY = "Размер сообщения превышает 1000 символов"
    const val REPLY_CREATED = "Комментарий создан"
}

object RecallConstants {
    const val AUTHOR_TOO_LONG = "Слишком длинное имя"
    const val AUTHOR_IS_EMPTY = "Не заполнено имя"

    const val TOPIC_TOO_LONG = "Слишком длинная тема"
    const val TOPIC_IS_EMPTY = "Не заполнено поле тема"

    const val EMAIL_IS_EMPTY = "Не заполнено поле email"

    const val TEXT_TOO_LONG = "Слишком длинный текст"
    const val TEXT_IS_EMPTY = "Не заполнено поле текст"

    const val RECALL_CREATED = "Мы получили Ваш отзыв"
}

object TextConstants {
    const val TEXT_NOT_FOUND = "Текст не найден"
}

object AccessConstants {
    const val ACCESS_DENIED = "Доступ запрещен"
}

object AdminConstants {
    const val ADMIN_USERNAME = "admin"
    const val ADMIN_EMAIL = "rvmail@mail.ru"
    const val ADMIN_PASSWORD = "admin"
}
