package company.ryzhkov.sh.service

import company.ryzhkov.sh.entity.KeyElement
import company.ryzhkov.sh.repository.KeyElementRepository
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.stereotype.Service
import reactor.core.scheduler.Schedulers
import java.util.*
import javax.annotation.PostConstruct

@Service class KeyService @Autowired constructor(
    private val keyElementRepository: KeyElementRepository,
    private val applicationArguments: ApplicationArguments
) {

    private val log: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(KeyService::class.java)

    @PostConstruct fun createKey() {
        if ("--key" in applicationArguments.sourceArgs) {
            val key = Keys.secretKeyFor(SignatureAlgorithm.HS256)
            val bytes = key.encoded
            val secretString = Base64.getEncoder().encodeToString(bytes)
            val keyElement = KeyElement(secretString = secretString)
            keyElementRepository
                .insert(keyElement)
                .subscribeOn(Schedulers.parallel())
                .subscribe { log.info("Key element created") }
        }
    }
}
