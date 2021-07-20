package ru.tinkoff.sanata.shared_models.status

import kotlinx.serialization.Serializable

@Serializable
enum class AuthenticationStatusCode {
    USER_NAME_NOT_EXISTS,
    INVALID_PASSWORD,
}