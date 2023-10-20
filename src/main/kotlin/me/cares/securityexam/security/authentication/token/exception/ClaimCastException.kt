package me.cares.securityexam.security.authentication.token.exception

import kotlin.reflect.KClass

class ClaimCastException(
    claimName: String,
    castType: KClass<*>
) : ClassCastException(
    "$claimName 클래임을 ${castType.simpleName} 타입으로 가져올 수 없습니다."
) {
}