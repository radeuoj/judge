package ninja.jetstream

import java.sql.Statement
import kotlin.text.split

object UserService {
    fun login(name: String, password: String): Int {
        Connection().use {
            val rs = it.query("SELECT id FROM users WHERE name = '$name' AND password = '$password'")
            return if (!rs.next()) 0
            else rs.getInt("id")
        }
    }

    fun login(auth: String?): Int {
        if (auth == null) return 0
        val (name, password) = auth.split(":")
        return login(name, password)
    }
}