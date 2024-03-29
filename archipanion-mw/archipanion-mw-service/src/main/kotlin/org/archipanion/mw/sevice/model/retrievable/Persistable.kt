package org.archipanion.mw.sevice.model.retrievable

import java.util.*

/**
 * A [Persistable] object that can be stored using the persistence layer.
 *
 * @author Luca Rossetto
 * @author Ralph Gasser
 * @version 1.0.0
 */
interface Persistable {
    /** The [UUID] identifying this [Persistable]. */
    val id: UUID

    /** Flag indicating, whether this [Persistable] is transient or persistent. */
    val transient: Boolean
}