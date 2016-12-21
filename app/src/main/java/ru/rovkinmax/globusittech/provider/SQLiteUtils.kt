package ru.rovkinmax.globusittech.provider

import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.SparseArray

object SQLiteUtils {
    private val SEARCH_QUERY_KEY = "search"
    private val CONFLICT_ALG = SparseArray<String>().apply {
        put(SQLiteDatabase.CONFLICT_REPLACE, "ON CONFLICT REPLACE")
        put(SQLiteDatabase.CONFLICT_ABORT, "ON CONFLICT ABORT")
        put(SQLiteDatabase.CONFLICT_IGNORE, "ON CONFLICT IGNORE")
        put(SQLiteDatabase.CONFLICT_ROLLBACK, "ON CONFLICT ABORT")
        put(SQLiteDatabase.CONFLICT_FAIL, "ON CONFLICT FAIL")
        put(SQLiteDatabase.CONFLICT_NONE, "ON CONFLICT NONE")
    }

    internal fun getSearchQuery(uri: Uri): String {
        val searchQuery = uri.getQueryParameter(SEARCH_QUERY_KEY)
        if (searchQuery.isNullOrBlank()) {
            return ""
        }
        val escapedSearchQuery = StringBuilder(searchQuery.length + 4)
        DatabaseUtils.appendEscapedSQLString(escapedSearchQuery, searchQuery)
        return escapedSearchQuery.toString()
    }

    internal fun getConflictAlg(alg: Int): String {
        return CONFLICT_ALG.get(alg, CONFLICT_ALG.get(0))
    }
}