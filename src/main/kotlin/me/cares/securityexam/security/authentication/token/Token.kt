package me.cares.securityexam.security.authentication.token

enum class Token(
    val secretKey: String,
    val expirationTimeInSecond: Long,
) {

    ACCESS(
        secretKey = "accessTokenSecretaccessTokenSecretaccessTokenSecretaccessTokenSecret",
        expirationTimeInSecond = 7 * 24 * 60 * 60
    ),

    REFRESH(
        secretKey = "refreshTokenSecretrefreshTokenSecretrefreshTokenSecretrefreshTokenSecret",
        expirationTimeInSecond = 7 * 24 * 60 * 60
    )

}