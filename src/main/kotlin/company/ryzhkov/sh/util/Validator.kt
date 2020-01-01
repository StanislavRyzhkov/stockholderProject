package company.ryzhkov.sh.util

import company.ryzhkov.sh.exception.ValidationException

class Validator<A>(private val obj: A) {

    fun check(message: String, f: (A) -> Boolean): Validator<A> {
        if (f(obj)) return this
        else throw ValidationException(message)
    }

    fun create(): A = this.obj
}
