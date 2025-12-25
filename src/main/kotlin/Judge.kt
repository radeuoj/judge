package ninja.jetstream

import java.io.File

fun initFolders() {
    val submissions = File("./submissions")
    submissions.mkdirs()
    val box = File("./box")
    box.mkdirs()
}

/**
 * @return if the compilation was successful or not
 */
fun compileCode(what: String, where: String): Boolean {
    val compiler = Process("g++ -o $where $what")
    return compiler.wait() == 0
}

fun evaluateSubmission(submissionId: Int) {
    if (!compileCode("./submissions/$submissionId.cpp", "./box/$submissionId")) {
        println("compiler error $submissionId")
        SubmissionService.markStatus(submissionId, SubmissionStatus.COMPILER_ERROR)
        return
    }

    val problemId = SubmissionService.getProblemId(submissionId)
    val testCount = SubmissionService.getTestCount(submissionId)
    runTests(submissionId, problemId, testCount)
    cleanSubmission(submissionId)
}

fun runTests(submissionId: Int, problemId: Int, testCount: Int) {
    Connection().use {
        for (i in 1..testCount) {
            val input = File("./problems/$problemId/tests/$i.in").readText()
            val ok = File("./problems/$problemId/tests/$i.ok").readText()

            val code = Process("./box/$submissionId")
            code.input(input)
            val output = code.output()
            code.wait()

            val pass = ok.replace(Regex("\\s"), "") == output.replace(Regex("\\s"), "")
            val status = if (pass) TestStatus.ACCEPTED else TestStatus.WRONG_ANSWER

            it.update("UPDATE tests SET status = ${status.ordinal} WHERE submission = $submissionId AND test = $i")
        }
    }
}

fun cleanSubmission(submissionId: Int) {
    val code = File("./box/$submissionId")
    code.delete()
    SubmissionService.markStatus(submissionId, SubmissionStatus.EVALUATED)
}