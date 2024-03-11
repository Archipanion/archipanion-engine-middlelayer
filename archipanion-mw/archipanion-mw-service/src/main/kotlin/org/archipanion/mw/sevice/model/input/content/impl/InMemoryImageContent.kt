package org.archipanion.mw.sevice.model.input.content.impl

import org.archipanion.mw.sevice.model.input.content.element.ImageContent
import java.awt.image.BufferedImage

/**
 * A naive in-memory implementation of the [ImageContent] interface.
 *
 * Warning: Usage of [InMemoryImageContent] may lead to out-of-memory situations in large extraction pipelines.
 *
 * @author Luca Rossetto.
 * @version 1.0.0
 */
data class InMemoryImageContent(override val content: BufferedImage) : ImageContent
