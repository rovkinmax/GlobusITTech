package ru.rovkinmax.globusittech.provider

import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri

abstract class SQLiteTable(val authority: String, val name: String) {
    companion object {

        private const val CONTENT = "content"
        private const val PATH_SEPARATOR = "/"

        fun makeUri(authority: String, table: String): Uri {
            return Uri.Builder()
                    .scheme(CONTENT)
                    .opaquePart(PATH_SEPARATOR)
                    .authority(authority)
                    .appendPath(table)
                    .build()
        }
    }

    val uri: Uri by lazy { makeUri(authority, name) }

    fun query(db: SQLiteDatabase, columns: Array<String>?, where: String?, whereArgs: Array<String>?, orderBy: String?): Cursor? {
        return SQLiteQuery()
                .table(name)
                .columns(*columns ?: emptyArray())
                .where(where ?: "")
                .withArgs(*whereArgs ?: emptyArray())
                .orderBy(orderBy ?: "")
                .select(db)
    }

    fun search(db: SQLiteDatabase, columns: Array<String>?, query: String?, orderBy: String?): Cursor? {
        throw UnsupportedOperationException(name)
    }

    fun insert(db: SQLiteDatabase, values: ContentValues): Long {
        return SQLiteQuery()
                .table(name)
                .withValues(values)
                .insert(db)
    }

    fun update(db: SQLiteDatabase, values: ContentValues, where: String?,
               whereArgs: Array<String>?): Int {
        return SQLiteQuery()
                .table(name)
                .withValues(values)
                .where(where ?: "")
                .withArgs(*whereArgs ?: emptyArray())
                .update(db)
    }

    fun delete(db: SQLiteDatabase, where: String?, whereArgs: Array<String>?): Int {
        return SQLiteQuery()
                .table(name)
                .where(where ?: "")
                .withArgs(*whereArgs ?: emptyArray())
                .delete(db)
    }

    abstract fun onCreate(db: SQLiteDatabase)

    fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        dropTable(db)
        onCreate(db)
    }

    private fun dropTable(db: SQLiteDatabase) = db.execSQL("DROP TABLE IF EXISTS $name;")

    fun onInsertComplete(cr: ContentResolver, rowId: Long) {
        cr.notifyChange(uri, null, false)
    }

    fun onBulkInsertComplete(cr: ContentResolver, affectedRows: Int) {
        cr.notifyChange(uri, null, false)
    }

    fun onUpdateComplete(cr: ContentResolver, affectedRows: Int) {
        cr.notifyChange(uri, null, false)
    }

    fun onDeleteComplete(cr: ContentResolver, affectedRows: Int) {
        cr.notifyChange(uri, null, false)
    }


}
