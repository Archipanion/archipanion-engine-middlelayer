package org.archipanion.mw.sevice.model.retrievable.attributes

interface MergingRetrievableAttribute : RetrievableAttribute {

    /**
     * Merges two attributes together into a single instance
     */
    fun merge(other: MergingRetrievableAttribute) : MergingRetrievableAttribute

}