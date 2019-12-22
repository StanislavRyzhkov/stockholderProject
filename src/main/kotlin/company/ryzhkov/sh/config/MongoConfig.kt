package company.ryzhkov.sh.config

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@Configuration
@EnableReactiveMongoRepositories(basePackages = ["company.ryzhkov.sh.repository"])
class MongoConfig : AbstractReactiveMongoConfiguration() {

    @Value("\${db.host}")
    lateinit var dbHost: String

    @Value("\${db.name}")
    lateinit var dbName: String

    @Bean override fun reactiveMongoClient(): MongoClient = MongoClients.create(dbHost)

    override fun getDatabaseName(): String = dbName
}
