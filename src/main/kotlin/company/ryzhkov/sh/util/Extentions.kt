package company.ryzhkov.sh.util

import arrow.core.Either
import arrow.core.Option
import arrow.core.Some
import company.ryzhkov.sh.entity.Message
import reactor.core.publisher.Mono

fun <A> A.toOption(): Option<A> = Some(this)

fun <A> String.toEitherLeft(): Either<Message, A> = Either.left(Message(this))

fun <A> A.toEitherRight(): Either<Message, A> = Either.right(this)

fun <A> String.toMonoEitherLeft(): Mono<Either<Message, A>> = Mono
    .just(Either.left(Message(this)))
