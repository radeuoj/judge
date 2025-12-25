package ninja.jetstream

import io.ktor.server.application.*
import java.lang.AutoCloseable
import java.sql.DriverManager
import java.sql.ResultSet

const val DATABASE_URL = "jdbc:sqlite:data.db"

fun Application.configureDatabase() {
    Connection().use {
        it.update("""
            CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, name VARCHAR(50), password VARCHAR(50));
            CREATE TABLE IF NOT EXISTS problems (id INTEGER PRIMARY KEY, name VARCHAR(50), time INTEGER, memory INTEGER, tests INTEGER);
            CREATE TABLE IF NOT EXISTS submissions (id INTEGER PRIMARY KEY, user INTEGER, problem INTEGER, status INTEGER);
            CREATE TABLE IF NOT EXISTS tests (id INTEGER PRIMARY KEY, submission INTEGER, test INTEGER, status INTEGER, time INTEGER);
        """)
    }
}

class Connection : AutoCloseable {
    val connection = DriverManager.getConnection(DATABASE_URL)
    val statement = connection.createStatement()

    fun query(sql: String): ResultSet {
        return statement.executeQuery(sql)
    }

    fun update(sql: String) {
        statement.executeUpdate(sql)
    }

    override fun close() {
        statement.close()
        connection.close()
    }
}