package org.archipanion.mw.sevice.model.input.content.element

import org.archipanion.mw.sevice.model.input.content.ContentType
import java.awt.image.BufferedImage

/**
 * A visual (image) [ContentElement].
 *
 * @author Ralph Gasser
 * @author Luca Rossetto
 * @version 1.0.0
 */
interface ImageContent: ContentElement<BufferedImage> {
    /** Width of the [BufferedImage] held by this [ContentElement]. */
    val width: Int
        get() = this.content.width

    /** Height of the [BufferedImage] held by this [ContentElement]. */
    val height: Int
        get() = this.content.height

    /** The [ContentType] of an [ImageContent] is always [ContentType.BITMAP_IMAGE]. */
    override val type: ContentType
        get() = ContentType.BITMAP_IMAGE
}