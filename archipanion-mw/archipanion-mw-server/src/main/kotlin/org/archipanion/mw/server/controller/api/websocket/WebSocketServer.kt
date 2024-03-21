package org.archipanion.mw.server.controller.api.websocket

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration


class WebSocketServer (val port: Int) {
    fun start() {
        embeddedServer(Netty, port = 8080) {

            install(WebSockets) {
                pingPeriod = java.time.Duration.ofMinutes(1)
            }

            routing {
                webSocket("/message") {
                    send("You are connected!")
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()
                        send("You said: $receivedText")
                    }
                }
            }
        }.start(wait = true)
    }
}