package org.archipanion.model.segment

import kotlinx.serialization.Serializable

@Serializable
data class Segment(
    val segmentId: String,
    val objectId: String,
    val start: Long,
    val end: Long,
    val startabs: Long,
    val endabs: Long,
    val count: Long,
    val sequenceNumber: Long,
)
