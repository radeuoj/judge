package ninja.jetstream

import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class Problem(
    val id: Int,
    val name: String,
    val score: Int,
    val tests: Int,
)

data class ProblemLimits(
    val time: Float,
    val memory: Int,
)

object ProblemService {
    fun all(): List<Problem> {
        Connection().use {
            val rs = it.query("SELECT * FROM problems ORDER BY id")

            val res = mutableListOf<Problem>()
            while (rs.next()) {
                res.add(Problem(
                    rs.getInt("id"),
                    rs.getString("name"),
                    0,
                    rs.getInt("tests")
                ))
            }

            return res
        }
    }

    fun statement(id: Int): String {
        val file = File("./problems/$id/statement.txt")
        return file.readText()
    }

    fun limits(id: Int): ProblemLimits {
        Connection().use {
            val rs = it.query("SELECT time, memory FROM problems WHERE id = $id")
            rs.next()
            return ProblemLimits(rs.getInt("time").toFloat() / 1_000, rs.getInt("memory"))
        }
    }
}