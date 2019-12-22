package company.ryzhkov.sh

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StockholderProjectApplication

fun main(args: Array<String>) {
	runApplication<StockholderProjectApplication>(*args)
}
