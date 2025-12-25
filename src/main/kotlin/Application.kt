package ninja.jetstream

import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    log.info("Dev mode: $developmentMode")
    log.info("OS: ${System.getProperty("os.name")}")

    initFolders()
    configureDatabase()
    configureStatusPages()
    configureContentNegotiation()
    configureRouting()
}
