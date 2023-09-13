package org.github.anisch

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import mui.icons.material.Star
import mui.material.*
import mui.material.styles.TypographyVariant
import mui.system.sx
import org.github.anisch.NetworkState.*
import react.*
import react.dom.aria.AriaAutoComplete
import react.dom.aria.ariaAutoComplete
import react.dom.html.ReactHTML.form
import react.dom.onChange
import web.cssom.Display
import web.cssom.JustifyContent
import web.html.HTMLInputElement
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

    Box {
        sx {
            display = Display.grid
            justifyContent = JustifyContent.left
        }
        component = form
        ariaAutoComplete = AriaAutoComplete.none

        Typography {
            variant = TypographyVariant.h4
            +"Neue Person anlegen:"
        }

        TextField {
            label = ReactNode("Name")
            type = InputType.text
            size = Size.small
            margin = FormControlMargin.normal
            value = name
            onChange = { element ->
                val target = element.target as HTMLInputElement
                name = target.value
            }
        }

        TextField {
            label = ReactNode("Vorname")
            type = InputType.text
            size = Size.small
            margin = FormControlMargin.normal
            value = givenName
            onChange = { element ->
                val target = element.target as HTMLInputElement
                givenName = target.value
            }
        }

        TextField {
            label = ReactNode("Geburtstag")
            type = InputType.date
            size = Size.small
            margin = FormControlMargin.normal
            value = birthDay ?: ""
            onChange = { element ->
                val target = element.target as HTMLInputElement
                birthDay =
                    if (target.value.isBlank()) null
                    else LocalDate.parse(target.value)
            }
        }

        TextField {
            label = ReactNode("Versicherungsnummer")
            type = InputType.text
            size = Size.small
            margin = FormControlMargin.normal
            value = insuranceNumber
            onChange = { element ->
                val target = element.target as HTMLInputElement
                insuranceNumber = target.value
            }
        }
        Button {
            variant = ButtonVariant.outlined
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

    Typography {
        variant = TypographyVariant.h1
        +"Hier stehen einige Personen....."
    }
    List {
        ListItem {
            ListItemIcon {
                Star()
            }
            +when (singleData) {
                is IsLoading -> "Lade Person"
                is Error -> "Fehler beim Laden der Person"
                is NoData -> "Die Person wurde nicht gefunden"
                is Success -> singleData.data.toString()
                else -> ""
            }
        }
        ListItem {
            ListItemIcon {
                Star()
            }
            +when (noSingleData) {
                is IsLoading -> "Lade Person"
                is Error -> "Fehler beim Laden der Person"
                is NoData -> "Die Person wurde nicht gefunden"
                is Success -> singleData.data.toString()
                else -> ""
            }
        }

        when (data) {
            is IsLoading -> ListItem {
                ListItemIcon {
                    Star()
                }
                +"Lade alle Personen"
            }

            is Error -> ListItem {
                ListItemIcon {
                    Star()
                }
                +"Fehler beim Laden der Personen"
            }

            is Success -> data.data!!.forEach { p ->
                ListItem {
                    ListItemIcon {
                        Star()
                    }
                    key = p.id.toString()
                    +"$p"
                }
            }

            else -> {}
        }
    }
}
