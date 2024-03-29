package org.archipanion.mw.sevice.model.util


import org.archipanion.mw.sevice.model.source.MediaType
import org.archipanion.mw.sevice.model.source.file.MimeType
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.imageio.ImageIO

fun BufferedImage.getRGBArray(): IntArray = this.getRGB(0, 0, this.width, this.height, null, 0, this.width)

fun BufferedImage.setRGBArray(array: IntArray) = this.setRGB(0, 0, this.width, this.height, array, 0, this.width)

fun BufferedImage.toDataURL(mimeType: MimeType = MimeType.PNG): String {
    require(mimeType.mediaType == MediaType.IMAGE) {"MimeType needs to be an image type"}
    val out = ByteArrayOutputStream()
    ImageIO.write(this, mimeType.fileExtension, out)
    return "data:${mimeType.mimeType};base64,${Base64.getEncoder().encodeToString(out.toByteArray())}"
}

fun BufferedImage(dataUrl: String) : BufferedImage {
    val base64 = dataUrl.substringAfter("base64,")
    val bytes = Base64.getDecoder().decode(base64)
    return ImageIO.read(ByteArrayInputStream(bytes))
}