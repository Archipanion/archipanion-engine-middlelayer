package org.archipanion.mw.sevice.model.input

import kotlinx.serialization.Serializable
import org.archipanion.mw.sevice.model.input.content.element.ContentElement
import org.archipanion.mw.sevice.model.input.content.element.ImageContent
import org.archipanion.mw.sevice.model.input.content.element.TextContent
import org.archipanion.mw.sevice.model.input.content.impl.InMemoryImageContent
import org.archipanion.mw.sevice.model.input.content.impl.InMemoryTextContent
import org.archipanion.mw.sevice.model.util.BufferedImage

import java.awt.image.BufferedImage

@Serializable
sealed class InputData {
    abstract val type: InputType

    abstract fun toContent() : ContentElement<*>

}

@Serializable
data class TextInputData(val data: String) : InputData() {
    override val type = InputType.TEXT

    override fun toContent(): TextContent = InMemoryTextContent(data)

}

@Serializable
data class VectorInputData(val data: List<Float>) : InputData(){
    override val type = InputType.VECTOR

    override fun toContent(): ContentElement<*> {
        throw UnsupportedOperationException("Cannot derive content from VectorInputData")
    }

}

@Serializable
data class ImageInputData(val data: String) : InputData() {
    override val type = InputType.VECTOR
    override fun toContent(): ImageContent = InMemoryImageContent(image)

    private val image: BufferedImage by lazy { BufferedImage(data) }

}

@Serializable
data class RetrievableIdInputData(val id: String) : InputData() {

    override val type = InputType.ID

    override fun toContent(): ContentElement<*> {
        throw UnsupportedOperationException("Cannot derive content from RetrievableInputData")
    }

}
