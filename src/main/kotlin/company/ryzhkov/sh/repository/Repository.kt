package company.ryzhkov.sh.repository

import company.ryzhkov.sh.entity.KeyElement
import company.ryzhkov.sh.entity.Recall
import company.ryzhkov.sh.entity.Text
import company.ryzhkov.sh.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository interface UserRepository : ReactiveMongoRepository<User, String> {

    fun findByUsername(username: String): Mono<User>

    fun findByUsernameAndStatus(username: String, status: String): Mono<User>

    fun findByEmail(email: String): Mono<User>
}

@Repository interface KeyElementRepository : ReactiveMongoRepository<KeyElement, String>

@Repository interface TextRepository : ReactiveMongoRepository<Text, String> {

    fun findByEnglishTitle(englishTitle: String): Mono<Text>

    fun findByKindOrderByCreatedDesc(kind: String): Flux<Text>

    fun findByKindOrderByCreatedDesc(kind: String, pageable: Pageable): Flux<Text>
}

@Repository interface RecallRepository : ReactiveMongoRepository<Recall, String>
