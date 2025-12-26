package ninja.jetstream

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.http.content.react
import io.ktor.server.http.content.singlePageApplication
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

            get("/submissions/{submissionId}") {
                val submissionId = call.parameters["submissionId"]!!.toInt()
                val submission = SubmissionService.getBig(submissionId)
                if (submission == null) call.respond(HttpStatusCode.NotFound)
                else call.respond(submission)
            }

            get("/problems") {
                call.respond(ProblemService.all())
            }

            get("/problems/{problemId}/statement") {
                val problemId = call.parameters["problemId"]!!.toInt()
                call.respondText(ProblemService.statement(problemId))
            }

            post("/problems/{problemId}/submit") {
                val problemId = call.parameters["problemId"]!!.toInt()
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
                Judge.addSubmission(submissionId)
            }
        }

        singlePageApplication {
            react("web/dist")
        }
    }
}
