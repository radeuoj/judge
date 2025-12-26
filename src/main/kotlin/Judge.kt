package ninja.jetstream

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.LinkedList
import java.util.Queue
import kotlin.time.Duration.Companion.seconds

object Judge {
    private val queue: Queue<Int> = LinkedList()

    fun run() {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                if (queue.isEmpty()) {
                    delay(1.seconds)
                    continue
                }

                val submissionId = queue.remove()
                evaluateSubmission(submissionId)
            }
        }
    }

    fun addSubmission(submissionId: Int) {
        queue.add(submissionId)
        println(queue)
    }

    fun initFolders() {
        val submissions = File("./submissions")
        submissions.mkdirs()
        val box = File("./box")
        box.mkdirs()
    }

    /**
     * @return if the compilation was successful or not
     */
    private fun compileCode(what: String, where: String): Boolean {
        val compiler = Process("g++ -o $where $what")
        return compiler.wait() == 0
    }

    private fun evaluateSubmission(submissionId: Int) {
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

    private fun runTests(submissionId: Int, problemId: Int, testCount: Int) {
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

    private fun cleanSubmission(submissionId: Int) {
        val code = File("./box/$submissionId")
        code.delete()
        SubmissionService.markStatus(submissionId, SubmissionStatus.EVALUATED)
    }
}