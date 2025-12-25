package ninja.jetstream

import java.lang.Process

class Process {
    var process: Process

    constructor(name: String) {
        process = exec(name)
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
}

fun exec(cmd: String): Process {
    if (System.getProperty("os.name").contains("Windows")) {
        return Runtime.getRuntime().exec("wsl -e $cmd")
    }

    return Runtime.getRuntime().exec(cmd)
}