package me.cares.securityexam.persistence

import org.springframework.data.repository.CrudRepository

inline fun <reified T, ID : Any> CrudRepository<T, ID>.findByIdOrThrow(id: ID): T =
    findById(id).orElseThrow {throw EntityNotExistException(T::class, id.toString()) }