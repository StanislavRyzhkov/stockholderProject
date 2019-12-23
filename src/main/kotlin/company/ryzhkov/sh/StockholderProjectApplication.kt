package company.ryzhkov.sh

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootApplication
class StockholderProjectApplication : ApplicationRunner {

	override fun run(args: ApplicationArguments?) {
	}

	@Bean fun encoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()
}

fun main(args: Array<String>) {
	runApplication<StockholderProjectApplication>(*args)
}
