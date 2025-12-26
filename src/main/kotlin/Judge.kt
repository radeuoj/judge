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
        val output = compiler.output()
        val error = compiler.error()
        val res = compiler.wait()

        if (res != 0) {
            println(output)
            println(error)
        }

        return res == 0
    }

    private fun evaluateSubmission(submissionId: Int) {
        val box = initIsolate()

        if (!compileCode("./submissions/$submissionId.cpp", "$box/$submissionId")) {
            println("compiler error $submissionId")
            SubmissionService.markStatus(submissionId, SubmissionStatus.COMPILER_ERROR)
            return
        }

        val problemId = SubmissionService.getProblemId(submissionId)
        val testCount = SubmissionService.getTestCount(submissionId)
        val problemLimits = ProblemService.limits(problemId)
        copyTestInputFiles(box, problemId, testCount)
        runTests(submissionId, problemId, testCount, problemLimits, box)
        cleanSubmission(submissionId)
    }

    /**
     * @return location of box
     */
    private fun initIsolate(): String {
        val isolate = Process("isolate --init")
        val box = isolate.output()
        isolate.wait()

        return "$box/box".replace(Regex("\\s"), "")
    }

    private fun copyTestInputFiles(box: String, problemId: Int, testCount: Int) {
        for (i in 1..testCount) {
//            val from = File("./problems/$problemId/tests/$i.in")
//            val to = File("$box/$i.in")
//            from.copyTo(to)
            Process("cp ./problems/$problemId/tests/$i.in $box/$i.in")
        }
    }

    private fun readTestOutput(testId: Int, box: String): String {
        val cat = Process("cat $box/$testId.out")
        return cat.output()
    }

    fun getTimeFromIsolateError(isolateError: String): Int {
        return (isolateError.substring(4).split(" ")[0].toFloat() * 1000).toInt()
    }

    private fun runTests(submissionId: Int, problemId: Int, testCount: Int, problemLimits: ProblemLimits, box: String) {
        Connection().use {
            for (i in 1..testCount) {
                val code = Process("isolate --run -i $i.in -o $i.out -t ${problemLimits.time} -m ${problemLimits.memory} -- ./$submissionId")
                val isolateError = code.error().trim()
                val isolateStatus = code.wait()

                println("$i $isolateStatus $isolateError")

                val ok = File("./problems/$problemId/tests/$i.ok").readText()
                val output = readTestOutput(i, box)

                val pass = ok.replace(Regex("\\s"), "") == output.replace(Regex("\\s"), "")
                val status = if (isolateStatus != 0) TestStatus.ERROR
                else if (pass) TestStatus.ACCEPTED
                else TestStatus.WRONG_ANSWER

                if (status == TestStatus.ERROR) {
                    it.update("UPDATE tests SET status = ${status.ordinal}, error = \"$isolateError\" WHERE submission = $submissionId AND test = $i")
                } else {
                    val time = getTimeFromIsolateError(isolateError)
                    it.update("UPDATE tests SET status = ${status.ordinal}, time = $time WHERE submission = $submissionId AND test = $i")
                }
            }
        }
    }

    private fun cleanSubmission(submissionId: Int) {
//        val code = File("./box/$submissionId")
//        code.delete()
        SubmissionService.markStatus(submissionId, SubmissionStatus.EVALUATED)
    }
}