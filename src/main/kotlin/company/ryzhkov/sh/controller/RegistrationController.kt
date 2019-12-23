package company.ryzhkov.sh.controller

import company.ryzhkov.sh.entity.Message
import company.ryzhkov.sh.entity.Register
import company.ryzhkov.sh.service.UserService
import company.ryzhkov.sh.util.Constants.USER_CREATED
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/api/register")
class RegistrationController @Autowired constructor(private val userService: UserService) {

    @PostMapping fun register(@Valid @RequestBody registerMono: Mono<Register>): Mono<Message> =
        registerMono
            .flatMap { userService.register(it) }
            .map { Message(USER_CREATED) }
}
