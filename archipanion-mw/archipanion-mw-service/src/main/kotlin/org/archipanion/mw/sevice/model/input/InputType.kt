package org.archipanion.mw.sevice.model.input

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class InputType {
    @SerialName("TEXT")
    TEXT,
    @SerialName("IMAGE")
    IMAGE,
    @SerialName("VECTOR")
    VECTOR,
    @SerialName("ID")
    ID
}