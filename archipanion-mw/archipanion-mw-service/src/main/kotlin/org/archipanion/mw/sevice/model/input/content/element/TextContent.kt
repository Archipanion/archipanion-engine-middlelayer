package org.archipanion.mw.sevice.model.input.content.element

import org.archipanion.mw.sevice.model.input.content.ContentType

/**
 * A textual [ContentElement].
 *
 * @author Ralph Gasser
 * @author Luca Rossetto
 * @version 1.0.0
 */
interface TextContent: ContentElement<String> {
    /** Length of the [String] held by this [TextContent]. */
    val length: Int
        get() = this.content.length

    /** The [ContentType] of an [TextContent] is always [ContentType.TEXT]. */
    override val type: ContentType
        get() = ContentType.TEXT
}