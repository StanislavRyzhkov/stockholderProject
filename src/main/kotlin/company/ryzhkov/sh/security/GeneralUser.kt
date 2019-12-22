package company.ryzhkov.sh.security

import company.ryzhkov.sh.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class GeneralUser constructor(val user: User) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> = user
        .roles
        .map { SimpleGrantedAuthority(it) }

    override fun getPassword(): String = user.password

    override fun getUsername(): String = user.username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = user.status == "ACTIVE"

    companion object {
        fun createInstance(user: User): UserDetails = GeneralUser(user)
    }
}
