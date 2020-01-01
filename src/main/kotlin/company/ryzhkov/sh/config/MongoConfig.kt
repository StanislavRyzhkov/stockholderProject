package company.ryzhkov.sh.config

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@Configuration
@EnableReactiveMongoRepositories(basePackages = ["company.ryzhkov.sh.repository"])
class MongoConfig : AbstractReactiveMongoConfiguration() {

    override fun reactiveMongoClient(): MongoClient =
        MongoClients.create("mongodb://localhost")

    override fun getDatabaseName(): String = "kotlin"
}
