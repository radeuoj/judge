package ninja.jetstream

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receiveText
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("/api") {
            get("/") {
                call.respondText("Hello World!")
            }

            post("/echo") {
                call.respondText(call.receiveText())
            }

            post("/login") {
                val id = UserService.login(call.request.headers["Authorization"])
                call.respond(if (id != 0) HttpStatusCode.OK else HttpStatusCode.Unauthorized)
            }

            get("/submissions") {
                call.respond(SubmissionService.all())
            }

            get("/problems") {
                call.respond(ProblemService.all())
            }

            get("/problems/{problem}/statement") {
                val problemId = call.parameters["problem"]!!.toInt()
                call.respondText(ProblemService.statement(problemId))
            }

            post("/problems/{problem}/submit") {
                val problemId = call.parameters["problem"]!!.toInt()
                val code = call.receiveText()
                val userId = UserService.login(call.request.headers["Authorization"]).let {
                    if (it == 0) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@post
                    }
                    it
                }

                val submissionId = SubmissionService.add(userId, problemId, code)
                call.respond(HttpStatusCode.OK)
                evaluateSubmission(submissionId)
            }
        }

//        post("/run") {
//            saveCode(call.receiveText(), "./box/code.cpp")
//
//            compileCode("./box/code.cpp", "./box/code")?.let {
//                call.respondText(it, status = HttpStatusCode.BadRequest)
//                return@post
//            }
//
//            val code = Code("./box/code")
//            code.wait()
//            call.respondText(code.output())
//        }

//        post("/problems/{problem}/submit") {
//            val problem = call.parameters["problem"]
//            val code = call.receiveText()
//
//            statement.executeUpdate("INSERT INTO submissions (problem, status) VALUES ($problem, ${SubmissionStatus.NOTHING.ordinal})")
//            val submissionRs = statement.executeQuery("SELECT * FROM submissions ORDER BY id DESC LIMIT 1")
//            submissionRs.next()
//            val submissionId = submissionRs.getInt("id")
//
//            saveCode(code, "./submissions/$submissionId.cpp")
//
//            compileCode("./submissions/$submissionId.cpp", "./box/$submissionId")?.let {
//                statement.executeUpdate("UPDATE submissions SET status = ${SubmissionStatus.COMPILER_ERROR.ordinal} WHERE id = $submissionId")
//
//                saveCode(it, "./submissions/${submissionId}_compiler_error.txt")
//                call.respondText("compiler error", status = HttpStatusCode.BadRequest)
//                return@post
//            }
//
//            val problemRs = statement.executeQuery("SELECT tests FROM problems WHERE id = $problem")
//            problemRs.next()
//            val tests = problemRs.getInt("tests")
//
//            var values = ""
//            for (i in 1..tests) {
//                values += "($submissionId, $i, ${TestStatus.UNEVALUATED.ordinal}, 0), "
//            }
//
//            statement.executeUpdate("INSERT INTO tests (submission, test, status, time) VALUES ${values.dropLast(2)}")
//
//            var accepted = 0
//            for (i in 1..tests) {
//                val input = File("./problems/$problem/tests/$i.in").readText()
//                val ok = File("./problems/$problem/tests/$i.ok").readText()
//
//                val code = Code("./box/$submissionId")
//                code.input(input)
//                val output = code.output()
//                code.wait()
//
//                val pass = ok.replace(Regex("\\s"), "") == output.replace(Regex("\\s"), "")
//                val status = if (pass) TestStatus.ACCEPTED.ordinal else TestStatus.WRONG_ANSWER.ordinal
//
//                if (pass) accepted++
//                statement.executeUpdate("UPDATE tests SET status = $status WHERE submission = $submissionId AND test = $i")
//            }
//
//            val exec = File("./box/$submissionId")
//            exec.delete()
//            statement.executeUpdate("UPDATE submissions SET status = ${SubmissionStatus.EVALUATED.ordinal} WHERE id = $submissionId")
//
//            call.respondText("$accepted/$tests")
//        }
    }
}
