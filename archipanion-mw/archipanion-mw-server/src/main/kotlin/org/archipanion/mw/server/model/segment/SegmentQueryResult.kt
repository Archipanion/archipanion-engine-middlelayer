package org.archipanion.mw.server.model.segment

import kotlinx.serialization.Serializable
import org.archipanion.mw.server.model.segment.Segment

@Serializable
data class SegmentQueryResult (
    val queryId: String,
    val content: List<Segment>,
    val messageType: String,
)