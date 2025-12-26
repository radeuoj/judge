package ninja.jetstream

import java.lang.Process

class Process {
    var process: Process

    constructor(cmd: String) {
        process = exec(cmd)
    }

    /**
     * @return error code
     */
    fun wait(): Int {
        return process.waitFor()
    }

    fun input(what: String) {
        process.outputStream.bufferedWriter().use {
            it.write(what)
            it.flush()
        }
    }

    fun output(): String {
        return process.inputStream.bufferedReader().readText()
    }

    fun error(): String {
        return process.errorStream.bufferedReader().readText()
    }
}

fun exec(cmd: String): Process {
    if (System.getProperty("os.name").contains("Windows")) {
        return Runtime.getRuntime().exec("wsl -u root -e $cmd")
    }

    return Runtime.getRuntime().exec(cmd)
}