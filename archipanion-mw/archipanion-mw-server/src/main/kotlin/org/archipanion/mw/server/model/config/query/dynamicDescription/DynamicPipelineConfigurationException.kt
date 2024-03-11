package org.archipanion.mw.server.model.config.query.dynamicDescription

class DynamicPipelineConfigurationException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}