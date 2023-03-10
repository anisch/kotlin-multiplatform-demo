package org.github.anisch.serial

import io.ktor.http.*
import io.ktor.resources.*
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Person(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("given_name")
    val givenName: String,
    @SerialName("birthday")
    val birthDay: LocalDate,
    @SerialName("insurance_number")
    val insuranceNumber: String,
)
