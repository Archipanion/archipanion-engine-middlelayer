package org.archipanion.Model.Segment

import kotlinx.serialization.Serializable

@Serializable
data class SegmentQueryResult (
    val queryId: String,
    val content: List<Segment>,
    val messageType: String,
)