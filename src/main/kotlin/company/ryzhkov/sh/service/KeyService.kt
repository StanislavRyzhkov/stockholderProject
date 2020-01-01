package company.ryzhkov.sh.service

import company.ryzhkov.sh.entity.KeyElement
import company.ryzhkov.sh.repository.KeyElementRepository
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.boot.ApplicationArguments
import java.util.*

class KeyService (
    private val keyElementRepository: KeyElementRepository,
    private val applicationArguments: ApplicationArguments
) {

    private val log: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(KeyService::class.java)

    fun createKey() {
        if ("--key" in applicationArguments.sourceArgs) {
            val key = Keys.secretKeyFor(SignatureAlgorithm.HS256)
            val bytes = key.encoded
            val secretString = Base64.getEncoder().encodeToString(bytes)
            val keyElement = KeyElement(secretString = secretString)
            keyElementRepository
                .insert(keyElement)
                .subscribe { log.info("Key element created") }
        }
    }
}
