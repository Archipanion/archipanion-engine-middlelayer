package org.vitrivr.engine.query.model.api

import kotlinx.serialization.Serializable
import org.archipanion.mw.sevice.model.context.QueryContext
import org.archipanion.mw.sevice.model.input.InputData
import org.archipanion.mw.sevice.model.operator.OperatorDescription

@Serializable
data class InformationNeedDescription(
    val inputs: Map<String, InputData>,
    val operations: Map<String, OperatorDescription>,
    val context: QueryContext,
    val output: String,
)
