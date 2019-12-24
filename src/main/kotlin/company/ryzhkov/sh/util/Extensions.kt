package company.ryzhkov.sh.util

import company.ryzhkov.sh.entity.User
import company.ryzhkov.sh.security.GeneralUser
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails

fun UserDetails.fix(): User = (this as GeneralUser).user

fun Authentication.fix(): User = (this.principal as UserDetails).fix()
