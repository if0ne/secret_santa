package components

import auth
import components.basic.ButtonColor
import components.basic.ButtonType
import components.basic.santaButton
import getSessionInformation
import getUserSessions

import kotlinx.css.*
import kotlinx.css.properties.TextDecoration

import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.*
import react.router.dom.*
import shared_models.model.User
import styled.*
import styled.styledDiv

import kotlinx.coroutines.*
import shared_models.model.Session
import shared_models.request.AuthenticationRequest
import shared_models.request.UserSessionInfoRequest
import shared_models.response.UserInfoAboutSessionResponse

val mainScope = MainScope()

external interface AppProps: RProps {

}

data class AppState(var logged: Boolean, var cachedUser: User?) : RState

interface GameId : RProps {
    var id: Int
}

class Application : RComponent<AppProps, AppState>() {

    private fun RBuilder.link(href: String, text: String) {
        routeLink(href, className = "nav-link") {
            styledDiv {
                css {
                    //classes = mutableListOf("nav-link")
                    +ComponentStyles.navbarLink
                }
                +text
            }
        }
    }

    private fun RBuilder.navbar() {
        styledNav {
            css {
                classes = mutableListOf("navbar", "navbar-expand-lg")
                backgroundColor = Color("#A6974B")
                color = Color.white
            }

            styledDiv {
                css {
                    classes = mutableListOf("container")
                    maxWidth = 820.px
                }
                routeLink(to = "/", className = "navbar-brand") {
                    styledDiv {
                        css {
                            color = Color.white
                            fontWeight = FontWeight.bold
                            textDecoration = TextDecoration.none
                        }

                        css.hover {
                            color = Color("#322C40")
                        }

                        +"Тайный Санта"
                    }
                }

                if (!state.logged) {
                    ul(classes = "navbar-nav ms-md-auto") {
                        li(classes = "nav-item") {
                            link("/login","Войти")
                        }
                        li(classes = "nav-item") {
                            link("/rules","Правила")
                        }
                    }

                    routeLink("/login") {
                        santaButton {
                            text = "Создать"
                            disabled = false
                            color = ButtonColor.ORANGE
                            buttonType = ButtonType.DEFAULT
                        }
                    }
                } else {
                    ul(classes = "navbar-nav ms-md-auto") {
                        li(classes = "nav-item") {
                            link("/games","Мои игры")
                        }
                        li(classes = "nav-item") {
                            link("/rules","Правила")
                        }
                    }
                }
            }
        }
    }

    override fun RBuilder.render() {
        browserRouter {
            styledDiv {
                css {
                    margin(0.px)
                    padding(0.px)
                    fontFamily = "'Roboto', sans-serif"
                }

                navbar()
                styledDiv {
                    css {
                        classes = mutableListOf("container")
                        marginTop = 40.px
                        maxWidth = 820.px
                    }

                    switch {
                        route("/rules", strict = true) {
                            child(Rules::class) {}
                        }
                        if (!state.logged) {
                            route("/", exact = true) {
                                child(Welcome::class) {}
                            }
                            route("/login", strict = true) {
                                child(Login::class) {
                                    attrs.logginCallback = { login, password, changeLoginState ->
                                        mainScope.launch {
                                            var cachedUser: User? = null
                                            try {
                                                cachedUser = auth(AuthenticationRequest(login,password))
                                            } catch (ex: Exception) {}
                                            setState(AppState(cachedUser != null, cachedUser))
                                            changeLoginState()
                                        }
                                    }
                                    attrs.user = state.cachedUser
                                }
                            }
                            route("/signup", strict = true) {
                                child(Signup::class) {}
                            }
                        } else {
                            route("/", exact = true) {
                                child(Profile::class) {
                                    attrs.user = state.cachedUser!!
                                    attrs.logoutCallback = {
                                        setState(AppState(false, null))
                                    }
                                    attrs.changeProfileCallback = {
                                        setState(AppState(true, it))
                                    }
                                }
                            }
                            route("/games", strict = true) {
                                child(GameList::class) {
                                    attrs.user = state.cachedUser!!
                                }
                            }
                            route("/create_game", strict = true) {
                                child(GameCreation::class) {
                                    attrs.user = state.cachedUser!!
                                }
                            }
                            route<GameId>("/game/:id") {
                                child(GameView::class) {
                                    attrs.user = state.cachedUser!!
                                    attrs.gameId = it.match.params.id
                                }
                            }
                        }
                        redirect(to = "/")
                    }
                }
            }
        }
    }
}