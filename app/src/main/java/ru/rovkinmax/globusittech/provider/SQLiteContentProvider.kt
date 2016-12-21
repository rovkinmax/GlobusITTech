package ru.rovkinmax.globusittech.provider

import android.content.*
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import java.util.*

abstract class SQLiteContentProvider : ContentProvider() {

    companion object {

        private val MIME_DIR = "vnd.android.cursor.dir/"
        private val MIME_ITEM = "vnd.android.cursor.item/"
    }

    private var schema: SQLiteSchema? = null

    override fun onCreate(): Boolean {
        schema = onCreateSchema()
        return true
    }

    override fun query(uri: Uri, columns: Array<String>?, where: String?,
                       whereArgs: Array<String>?, orderBy: String?): Cursor? {
        val table = schema!!.acquireTable(uri)
        val match = SQLiteUriMatcher.match(uri)
        when (match) {
            SQLiteUriMatcher.MATCH_ID -> return selectFrom(table, columns, BaseColumns._ID + "=?",
                    arrayOf(uri.lastPathSegment), orderBy)
            SQLiteUriMatcher.MATCH_SEARCH -> return searchIn(table, columns, SQLiteUtils.getSearchQuery(uri), orderBy)
            else -> return selectFrom(table, columns, where, whereArgs, orderBy)
        }
    }

    override fun getType(uri: Uri): String? {
        val table = schema!!.acquireTable(uri)
        if (SQLiteUriMatcher.match(uri) == SQLiteUriMatcher.MATCH_ID) {
            return MIME_ITEM + table.name
        }
        return MIME_DIR + table.name
    }

    override fun insert(uri: Uri, values: ContentValues): Uri? {
        val table = schema!!.acquireTable(uri)
        val match = SQLiteUriMatcher.match(uri)
        if (match == SQLiteUriMatcher.MATCH_ID) {
            val rowId = uri.lastPathSegment
            val affectedRows = updateSet(table, values, BaseColumns._ID + "=?", arrayOf(rowId))
            if (affectedRows > 0) {
                return Uri.withAppendedPath(table.uri, rowId)
            }
        }
        return insertInto(table, values)
    }

    override fun delete(uri: Uri, where: String?, whereArgs: Array<String>?): Int {
        val table = schema!!.acquireTable(uri)
        val match = SQLiteUriMatcher.match(uri)
        if (match == SQLiteUriMatcher.MATCH_ID) {
            return deleteFrom(table, BaseColumns._ID + "=?", arrayOf(uri.lastPathSegment))
        }
        return deleteFrom(table, where, whereArgs)
    }

    override fun update(uri: Uri, values: ContentValues, where: String?,
                        whereArgs: Array<String>?): Int {
        val table = schema!!.acquireTable(uri)
        val match = SQLiteUriMatcher.match(uri)
        if (match == SQLiteUriMatcher.MATCH_ID) {
            return updateSet(table, values, BaseColumns._ID + "=?", arrayOf(uri.lastPathSegment))
        }
        return updateSet(table, values, where, whereArgs)
    }

    override fun bulkInsert(uri: Uri, bulkValues: Array<ContentValues>): Int {
        val table = schema!!.acquireTable(uri)
        val db = schema!!.writableDatabase
        var affectedRows = 0
        db.beginTransactionNonExclusive()
        try {
            for (values in bulkValues) {
                val lastInsertRowId = table.insert(db, values)
                if (lastInsertRowId > 0) {
                    ++affectedRows
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        table.onBulkInsertComplete(context!!.contentResolver, affectedRows)
        return affectedRows
    }

    @Throws(OperationApplicationException::class)
    override fun applyBatch(operations: ArrayList<ContentProviderOperation>): Array<ContentProviderResult> {
        val db = schema!!.writableDatabase
        db.beginTransactionNonExclusive()
        val results: Array<ContentProviderResult>
        try {
            results = super.applyBatch(operations)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return results
    }

    protected abstract fun onCreateSchema(): SQLiteSchema

    private fun selectFrom(table: SQLiteTable, columns: Array<String>?, where: String?,
                           whereArgs: Array<String>?, orderBy: String?): Cursor? {
        val cursor = table.query(schema!!.readableDatabase, columns, where, whereArgs, orderBy)
        cursor?.setNotificationUri(context!!.contentResolver, table.uri)
        return cursor
    }

    private fun searchIn(table: SQLiteTable, columns: Array<String>?, query: String?,
                         orderBy: String?): Cursor? {
        val cursor = table.search(schema!!.readableDatabase, columns, query, orderBy)
        cursor?.setNotificationUri(context!!.contentResolver, table.uri)
        return cursor
    }

    private fun insertInto(table: SQLiteTable, values: ContentValues): Uri {
        val rowId = table.insert(schema!!.writableDatabase, values)
        if (rowId > 0) {
            table.onInsertComplete(context!!.contentResolver, rowId)
        }
        return ContentUris.withAppendedId(table.uri, rowId)
    }

    private fun updateSet(table: SQLiteTable, values: ContentValues, where: String?,
                          whereArgs: Array<String>?): Int {
        val affectedRows = table.update(schema!!.writableDatabase, values, where, whereArgs)
        if (affectedRows > 0) {
            table.onUpdateComplete(context!!.contentResolver, affectedRows)
        }
        return affectedRows
    }

    private fun deleteFrom(table: SQLiteTable, where: String?, whereArgs: Array<String>?): Int {
        val affectedRows = table.delete(schema!!.writableDatabase, where, whereArgs)
        if (affectedRows > 0) {
            table.onDeleteComplete(context!!.contentResolver, affectedRows)
        }
        return affectedRows
    }
}