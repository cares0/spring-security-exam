package me.cares.securityexam.persistence

import kotlin.reflect.KClass

class EntityNotExistException(
    entityClassToFind: KClass<*>,
    identifier: String
) : IllegalArgumentException(
    "[$identifier] 식별자에 해당하는 [${entityClassToFind.simpleName}] 엔티티를 찾을 수 없습니다."
) {
}