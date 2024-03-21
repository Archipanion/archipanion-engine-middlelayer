package org.archipanion.mw.sevice.model.input

import kotlinx.serialization.Serializable


@Serializable
data class Inputs (
    val inputs: Map<String, InputData>
)