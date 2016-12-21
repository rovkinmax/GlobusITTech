package ru.rovkinmax.globusittech.provider

import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.text.TextUtils
import java.util.*

class SQLiteCreateTable(private val name: String) {

    companion object {
        private const val ROW_ID = BaseColumns._ID + " INTEGER PRIMARY KEY "
        private const val INTEGER = " INTEGER"
        private const val REAL = " REAL"
        private const val TEXT = " TEXT"
        private const val BLOB = " BLOB"
        private const val DEFAULT_VALUE = " DEFAULT "
    }

    private val columns = ArrayList<String>()
    private val constraints = ArrayList<String>()
    private val indexes = ArrayList<String>()

    @JvmOverloads
    fun rowId(conflictClause: Int = SQLiteDatabase.CONFLICT_REPLACE): SQLiteCreateTable = column(ROW_ID + SQLiteUtils.getConflictAlg(conflictClause))

    fun textColumn(name: String): SQLiteCreateTable = column(name + TEXT)

    fun textColumn(name: String, defaultValue: String): SQLiteCreateTable = column(name + TEXT + DEFAULT_VALUE + defaultValue)

    fun intColumn(name: String): SQLiteCreateTable = column(name + INTEGER)

    fun intColumn(name: String, defaultValue: Int): SQLiteCreateTable = column(name + INTEGER + DEFAULT_VALUE + defaultValue)

    fun realColumn(name: String): SQLiteCreateTable = column(name + REAL)

    fun realColumn(name: String, defaultValue: Double): SQLiteCreateTable = column(name + REAL + DEFAULT_VALUE + defaultValue)

    fun blobColumn(name: String): SQLiteCreateTable = column(name + BLOB)

    fun column(definition: String): SQLiteCreateTable {
        columns.add(definition)
        return this
    }

    fun unique(vararg columns: String): SQLiteCreateTable = unique(SQLiteDatabase.CONFLICT_REPLACE, *columns)

    fun unique(conflictClause: Int, vararg columns: String): SQLiteCreateTable {
        constraints.add("UNIQUE (" + TextUtils.join(SQLiteQuery.COMMA, columns) + ") " + SQLiteUtils.getConflictAlg(conflictClause))
        return this
    }

    fun index(vararg columns: String): SQLiteCreateTable {
        if (columns.isNotEmpty()) {
            indexes.add(String.format(Locale.US, "CREATE INDEX IF NOT EXISTS %s_idx%d ON %s(%s)",
                    name, indexes.size + 1, name,
                    TextUtils.join(SQLiteQuery.COMMA, columns)))
        }
        return this
    }

    fun constraint(constraint: String): SQLiteCreateTable {
        constraints.add(constraint)
        return this
    }

    fun execute(db: SQLiteDatabase) {
        val query = StringBuilder(512)
        query.append("CREATE TABLE IF NOT EXISTS ").append(name).append("(")
                .append(TextUtils.join(SQLiteQuery.COMMA, columns))
        if (constraints.isEmpty().not())
            query.append(SQLiteQuery.COMMA).append(TextUtils.join(SQLiteQuery.COMMA, constraints))
        query.append(");")
        db.execSQL(query.toString())
        columns.clear()
        constraints.clear()
    }
}

fun createTable(name: String, func: (SQLiteCreateTable.() -> (Unit))): Unit = func(SQLiteCreateTable(name))