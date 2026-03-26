package com.example.myrayon.data.db
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myrayon.model.*
import kotlin.text.insert

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, "myrayon.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "email TEXT," +
                    "address TEXT," +
                    "role TEXT)"
        )
        db.execSQL(
            "CREATE TABLE requests (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "userId INTEGER," +
                    "district TEXT," +
                    "text TEXT," +
                    "status TEXT)"
        )
        db.execSQL(
            "CREATE TABLE messages (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "userId INTEGER," +
                    "text TEXT," +
                    "timestamp LONG)"
        )
        db.execSQL(
            "CREATE TABLE votes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "question TEXT," +
                    "agree INTEGER," +
                    "disagree INTEGER," +
                    "abstain INTEGER)"
        )
        db.execSQL(
            "CREATE TABLE news (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT, " +
                    "content TEXT)"
        )
        db.execSQL(
            "CREATE TABLE districts (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun addUser(name: String, email: String, address: String, role: String = "User") {
        val cv = ContentValues()
        cv.put("name", name)
        cv.put("email", email)
        cv.put("address", address)
        cv.put("role", role)
        writableDatabase.insert("users", null, cv)
    }

    fun getUser(id: Int): User? {
        val cursor = readableDatabase.rawQuery("SELECT * FROM users WHERE id=?", arrayOf(id.toString()))
        if (cursor.moveToFirst()) {
            val user = User(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                role = cursor.getString(4))
            cursor.close()
            return user
        }
        cursor.close()
        return null
    }

    fun getUserByEmail(email: String): User? {
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM users WHERE email=?",
            arrayOf(email)
        )

        if (cursor.moveToFirst()) {
            val user = User(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4) // role
            )
            cursor.close()
            return user
        }
        cursor.close()
        return null
    }

    fun addRequest(userId: Int, text: String) {
        val cv = ContentValues()
        cv.put("userId", userId)
        cv.put("text", text)
        cv.put("status", "New")
        writableDatabase.insert("requests", null, cv)
    }

    fun getAllRequests(): List<Request> {
        val list = mutableListOf<Request>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM requests", null)
        while (cursor.moveToNext()) {
            list.add(Request(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getString(3),
                status = cursor.getString(4)))
        }
        cursor.close()
        return list
    }

    fun updateRequestStatus(id: Int, status: String): Boolean {
        val cv = ContentValues()
        cv.put("status", status)
        val rows = writableDatabase.update("requests", cv, "id=?", arrayOf(id.toString()))
        return rows > 0
    }

    fun addMessage(userId: Int, text: String) {
        val cv = ContentValues()
        cv.put("userId", userId)
        cv.put("text", text)
        cv.put("timestamp", System.currentTimeMillis())
        writableDatabase.insert("messages", null, cv)
    }

    fun getAllMessagesWithUsers(): List<Message> {
        val query = """
        SELECT messages.id, users.name, messages.text, messages.timestamp
        FROM messages
        JOIN users ON users.id = messages.userId
        ORDER BY timestamp ASC
    """
        val cursor = readableDatabase.rawQuery(query, null)
        val list = mutableListOf<Message>()

        while (cursor.moveToNext()) {
            list.add(
                Message(
                    id = cursor.getInt(0),
                    userName = cursor.getString(1),
                    text = cursor.getString(2),
                    timestamp = cursor.getLong(3)
                )
            )
        }
        cursor.close()
        return list
    }


    fun addVote(question: String) {
        val cv = ContentValues()
        cv.put("question", question)
        cv.put("agree", 0)
        cv.put("disagree", 0)
        cv.put("abstain", 0)
        writableDatabase.insert("votes", null, cv)
    }

    fun vote(id: Int, choice: String) {
        val voteColumn = when (choice) {
            "agree" -> "agree"
            "disagree" -> "disagree"
            else -> "abstain"
        }
        writableDatabase.execSQL("UPDATE votes SET $voteColumn = $voteColumn + 1 WHERE id=?", arrayOf(id))
    }

    fun getAllVotes(): List<Vote> {
        val list = mutableListOf<Vote>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM votes", null)
        while (cursor.moveToNext()) {
            list.add(Vote(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4)))
        }
        cursor.close()
        return list
    }

    fun addNews(title: String, content: String) {
        val cv = ContentValues()
        cv.put("title", title)
        cv.put("content", content)
        writableDatabase.insert("news", null, cv)
    }

    fun getAllNews(): List<News> {
        val list = mutableListOf<News>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM news", null)
        while (cursor.moveToNext()) {
            list.add(News(cursor.getInt(0), cursor.getString(1), cursor.getString(2)))
        }
        cursor.close()
        return list
    }
}
