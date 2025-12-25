package ninja.jetstream

import kotlinx.serialization.Serializable
import java.io.File
import java.sql.Statement
import javax.swing.plaf.nimbus.State

@Serializable
data class Submission(
    val id: Int,
    val userId: Int,
    val userName: String,
    val problemId: Int,
    val problemName: String,
    val status: SubmissionStatus,
    val score: Int,
    val tests: Int,
)

object SubmissionService {
    fun all(): List<Submission> {
        Connection().use {
            val rs = it.query("""
                SELECT s.id, s.user AS user_id, u.name AS user_name, s.problem AS problem_id, p.name AS problem_name, s.status, COUNT(t.id) AS score, p.tests FROM submissions s 
                LEFT JOIN tests t ON s.id = t.submission AND t.status = ${TestStatus.ACCEPTED.ordinal}
                LEFT JOIN users u ON s.user = u.id
                LEFT JOIN problems p ON s.problem = p.id
                GROUP BY s.id, s.user, s.problem, s.status
                ORDER BY s.id DESC
            """)

            val res = mutableListOf<Submission>()
            while (rs.next()) {
                res.add(Submission(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("user_name"),
                    rs.getInt("problem_id"),
                    rs.getString("problem_name"),
                    SubmissionStatus.entries[rs.getInt("status")],
                    rs.getInt("score"),
                    rs.getInt("tests")
                ))
            }

            return res
        }
    }

    fun getProblemId(id: Int): Int {
        Connection().use {
            val rs = it.query("SELECT problem FROM submissions WHERE id = $id")
            rs.next()
            return rs.getInt("problem")
        }
    }

    fun getTestCount(id: Int): Int {
        Connection().use {
            val rs = it.query("SELECT p.tests FROM submissions s INNER JOIN problems p ON s.problem = p.id WHERE s.id = $id")
            rs.next()
            return rs.getInt("tests")
        }
    }

    private fun addTests(id: Int) {
        Connection().use {
            for (i in 1..getTestCount(id)) {
                it.update("INSERT INTO tests (submission, test, status, time) VALUES ($id, $i, ${TestStatus.UNEVALUATED.ordinal}, 0)")
            }
        }
    }

    /**
     * @return submission id
     */
    fun add(userId: Int, problemId: Int, code: String): Int {
        Connection().use {
            it.update("INSERT INTO submissions (user, problem, status) VALUES ($userId, $problemId, ${SubmissionStatus.NOTHING.ordinal})")
            val rs = it.query("SELECT last_insert_rowid() AS id")
            rs.next()
            val id = rs.getInt("id")

            addTests(id)

            val file = File("./submissions/$id.cpp")
            file.writeText(code)

            return id
        }
    }

    fun markStatus(id: Int, status: SubmissionStatus) {
        Connection().use {
            it.update("UPDATE submissions SET status = ${status.ordinal} WHERE id = $id")
        }
    }
}