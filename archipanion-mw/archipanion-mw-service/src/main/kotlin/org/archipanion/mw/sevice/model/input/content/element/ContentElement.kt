package org.archipanion.mw.sevice.model.input.content.element

import org.archipanion.mw.sevice.model.input.content.Content
import org.archipanion.mw.sevice.model.input.content.ContentType

/**
 * A [Content] element is a piece of [Content] that is tied to some actual [Content].
 *
 * The types of [ContentElement] are restricted
 *
 * @author Ralph Gasser
 * @version 1.0.0
 */
sealed interface ContentElement<T>: Content {
    /**
     * Accesses the content held by  this [ContentElement].
     *
     * @return [ContentElement]
     */
    val content: T

    /** The [ContentType] of this [ContentElement]. */
    val type: ContentType
}