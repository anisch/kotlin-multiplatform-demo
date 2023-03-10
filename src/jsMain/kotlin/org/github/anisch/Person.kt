package org.github.anisch

import csstype.Display
import csstype.px
import emotion.react.css
import io.github.oshai.KotlinLogging
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.github.anisch.NetworkState.*
import react.*
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h4
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ul
import web.html.InputType
import org.github.anisch.resources.Person as RPerson
import org.github.anisch.serial.Person as SPerson

private val log = KotlinLogging.logger { }

external interface PersonProps : Props {
    var post: NetworkState<Long>
    var setPostState: (NetworkState<Long>) -> Unit
}

val PersonComponent = FC<Props> {
    var postState by useState<NetworkState<Long>>(Init())

    PersonForm {
        post = postState
        setPostState = { state -> postState = state }
    }
    PersonListComponent {
        post = postState
        setPostState = { state -> postState = state }
    }
}

val PersonForm = FC<PersonProps> { props ->
    var name by useState("")
    var givenName by useState("")
    var birthDay by useState<LocalDate?>(null)
    var insuranceNumber by useState("")

    div {
        h4 {
            +"Neue Person anlegen:"
        }

        label {
            +"Name:"
        }
        input {
            css {
                marginBottom = 5.px
                display = Display.block
            }
            type = InputType.text
            value = name
            onChange = { event ->
                name = event.target.value
            }
        }

        label {
            +"Vorname:"
        }
        input {
            css {
                marginBottom = 5.px
                display = Display.block
            }
            type = InputType.text
            value = givenName
            onChange = { event ->
                givenName = event.target.value
            }
        }

        label {
            +"Geburtstag:"
        }
        input {
            css {
                marginBottom = 5.px
                display = Display.block
            }
            type = InputType.date
            value = birthDay ?: ""
            onChange = { event ->
                birthDay =
                    if (event.target.value.isBlank()) null
                    else LocalDate.parse(event.target.value)
            }
        }

        label {
            +"Versicherungsnummer:"
        }
        input {
            css {
                marginBottom = 5.px
                display = Display.block
            }
            type = InputType.text
            value = insuranceNumber
            onChange = { event ->
                insuranceNumber = event.target.value
            }

        }
        button {
            css {
                marginTop = 10.px
            }
            onClick = {
                scope.launch {
                    log.info { "Send Person to server" }
                    props.setPostState(IsLoading())

                    val result = try {
                        val response = client.post(RPerson()) {
                            contentType(ContentType.Application.Json)
                            setBody(SPerson(0, name, givenName, birthDay!!, insuranceNumber))
                        }
                        log.info { response }
                        val result: Long = response.body()
                        Success(result)
                    } catch (ex: Exception) {
                        log.error(ex) { ex }
                        Error(cause = ex)
                    }
                    if (result is Success) {
                        name = ""
                        givenName = ""
                        insuranceNumber = ""
                        birthDay = null
                    }

                    props.setPostState(result)
                }
            }
            +"Anlegen"
        }
    }
}

val PersonListComponent = FC<PersonProps> { props ->
    var data by useState<NetworkState<List<SPerson>>>(Init())
    var singleData by useState<NetworkState<SPerson>>(Init())
    var noSingleData by useState<NetworkState<SPerson>>(Init())

    useEffect(props.post) {
        if (props.post is Success) {
            scope.launch {
                log.info { "Load all Persons from Server" }
                data = IsLoading()
                data = try {
                    val response = client.get(RPerson())
                    log.info { response }
                    val result: List<SPerson> = response.body()
                    Success(result)
                } catch (ex: Exception) {
                    log.error(ex) { ex }
                    Error(cause = ex)
                } finally {
                    props.setPostState(Init())
                }
            }
        }
    }

    useEffectOnce {
        scope.launch {
            log.info { "Load all Persons from Server" }
            data = IsLoading()
            data = try {
                val response = client.get(RPerson())
                log.info { response }
                val result: List<SPerson> = response.body()
                Success(result)
            } catch (ex: Exception) {
                log.error(ex) { ex }
                Error(cause = ex)
            }
        }
    }

    useEffectOnce {
        scope.launch {
            log.info { "Load Person 1 from Server" }
            singleData = IsLoading()
            singleData = try {
                val response = client.get(RPerson.Id(id = 1L))
                log.info { response }
                if (response.status == HttpStatusCode.NotFound) {
                    NoData()
                } else {
                    val result: SPerson = response.body()
                    Success(result)
                }
            } catch (ex: Exception) {
                log.error(ex) { ex }
                Error(cause = ex)
            }
        }
    }

    useEffectOnce {
        scope.launch {
            log.info { "Load Person 42 from Server" }
            noSingleData = IsLoading()
            noSingleData = try {
                val response = client.get(RPerson.Id(id = 42L))
                log.info { response }
                if (response.status == HttpStatusCode.NotFound) {
                    NoData()
                } else {
                    val result: SPerson = response.body()
                    Success(result)
                }
            } catch (ex: Exception) {
                log.error(ex) { ex }
                Error(cause = ex)
            }
        }
    }

    h1 {
        +"Hier stehen einige Personen....."
    }
    ul {
        li {
            +when (singleData) {
                is IsLoading -> "Lade Person"
                is Error -> "Fehler beim Laden der Person"
                is NoData -> "Die Person wurde nicht gefunden"
                is Success -> singleData.data.toString()
                else -> ""
            }
        }
        li {
            +when (noSingleData) {
                is IsLoading -> "Lade Person"
                is Error -> "Fehler beim Laden der Person"
                is NoData -> "Die Person wurde nicht gefunden"
                is Success -> singleData.data.toString()
                else -> ""
            }
        }

        when (data) {
            is IsLoading -> li { +"Lade alle Personen" }
            is Error -> li { +"Fehler beim Laden der Personen" }
            is Success -> data.data!!.forEach { p ->
                li {
                    key = p.id.toString()
                    +"$p"
                }
            }

            else -> {}
        }
    }
}
