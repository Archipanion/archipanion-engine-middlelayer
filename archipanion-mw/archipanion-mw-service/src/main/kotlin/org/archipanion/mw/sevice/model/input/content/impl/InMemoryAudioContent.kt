package org.archipanion.mw.sevice.model.input.content.impl

import org.archipanion.mw.sevice.model.input.content.element.AudioContent
import java.nio.ShortBuffer

/**
 * A naive in-memory implementation of the [AudioContent] interface.
 *
 * Warning: Usage of [InMemoryAudioContent] may lead to out-of-memory situations in large extraction pipelines.
 *
 * @author Ralph Gasser
 * @version 1.0.0
 */
data class InMemoryAudioContent(override val channel: Int, override val samplingRate: Int, override val content: ShortBuffer) :
    AudioContent
