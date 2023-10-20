package me.cares.securityexam.application.domain

import jakarta.persistence.*
import java.util.UUID

@Entity
class RefreshToken(

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], optional = false)
    @JoinColumn(name = "account_id")
    val account: Account,

    var rawToken: String,

) {

    @Id
    @Column(name = "account_id", insertable = false, updatable = false)
    val id: UUID = UUID.randomUUID()

    fun rotateToken(newRawToken: String) {
        this.rawToken = newRawToken
    }

    fun isSameToken(rawToken: String): Boolean {
        return this.rawToken == rawToken
    }

}