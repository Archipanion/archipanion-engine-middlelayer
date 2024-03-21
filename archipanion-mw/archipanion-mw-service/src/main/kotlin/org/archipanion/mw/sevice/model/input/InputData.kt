package org.archipanion.mw.sevice.model.input

import kotlinx.serialization.Serializable
import org.archipanion.mw.sevice.model.input.content.element.ContentElement
import org.archipanion.mw.sevice.model.input.content.element.ImageContent
import org.archipanion.mw.sevice.model.input.content.element.TextContent
import org.archipanion.mw.sevice.model.input.content.impl.InMemoryImageContent
import org.archipanion.mw.sevice.model.input.content.impl.InMemoryTextContent
import org.archipanion.mw.sevice.model.util.BufferedImage

import java.awt.image.BufferedImage

@Serializable(with = InputDataSerializer::class)
sealed class InputData {
    abstract val type: InputType
    abstract fun toContent(): ContentElement<*>
}

@Serializable
data class TextInputData(val data: String, override val type: InputType = InputType.TEXT) : InputData() {
    override fun toContent(): TextContent = InMemoryTextContent(data)

}

@Serializable
data class VectorInputData(val data: List<Float>, override val type: InputType = InputType.VECTOR) : InputData() {


    override fun toContent(): ContentElement<*> {
        throw UnsupportedOperationException("Cannot derive content from VectorInputData")
    }

}

@Serializable
data class ImageInputData(val data: String,   override val type: InputType = InputType.VECTOR) : InputData() {

    override fun toContent(): ImageContent = InMemoryImageContent(image)

    private val image: BufferedImage by lazy { BufferedImage(data) }

}

@Serializable
data class RetrievableIdInputData(val id: String,     override val type: InputType = InputType.ID) : InputData() {



    override fun toContent(): ContentElement<*> {
        throw UnsupportedOperationException("Cannot derive content from RetrievableInputData")
    }

}
