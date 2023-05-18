package org.github.anisch

import emotion.react.css
import mui.material.Size
import mui.material.TextField
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.onChange
import react.useState
import web.cssom.px
import web.cssom.rgb
import web.html.HTMLInputElement
import web.html.InputType

external interface WelcomeProps : Props {
    var name: String
}

val Welcome = FC<WelcomeProps> { props ->
    var name by useState(props.name)
    div {
        css {
            padding = 5.px
            backgroundColor = rgb(8, 97, 22)
            color = rgb(56, 246, 137)
        }
        +"Hello, $name"
    }
    TextField {
        css {
            marginTop = 5.px
            marginBottom = 5.px
            fontSize = 14.px
        }
        type = InputType.text
        value = name
        size = Size.small
        onChange = { element ->
            val target = element.target as HTMLInputElement
            name = target.value
        }
    }
    PersonComponent {}
}
