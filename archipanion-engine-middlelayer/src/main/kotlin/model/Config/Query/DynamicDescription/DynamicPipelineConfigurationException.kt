package org.archipanion.model.Config.Query.DynamicDescription

class DynamicPipelineConfigurationException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}