package org.github.anisch.repos

interface CrudRepository<T> {
    suspend fun create(t: T): Long
    suspend fun read(): List<T>
    suspend fun read(id: Long): T?
    suspend fun update(t: T): T
    suspend fun delete(): List<T>
    suspend fun delete(id: Long): T?
}
