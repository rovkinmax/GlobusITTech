package ru.rovkinmax.globusittech.provider

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.text.TextUtils

class SQLiteQuery {

    companion object {
        const val COMMA = ", "
        private var AND = " AND "
        private var OR = " OR "
    }

    private val columns = arrayListOf<String>()

    private val whereArgs = arrayListOf<String>()

    private val orderBy = arrayListOf<String>()

    private val values = ContentValues()

    private var tables: String? = null

    private var where: StringBuilder? = null


    fun table(vararg tables: String): SQLiteQuery {
        this.tables = tables.joinTo(StringBuilder(), separator = COMMA).toString()
        return this
    }

    fun columns(vararg columns: String): SQLiteQuery {
        this.columns.addAll(columns)
        return this
    }

    fun columns(alias: String, vararg columns: String): SQLiteQuery {
        if (columns.isNotEmpty()) {
            columns.forEach { column ->
                if (column.contains("."))
                    this.columns.add(column)
                else
                    this.columns.add(alias + column)
            }
        } else
            this.columns.add(alias + "*")

        return this
    }

    fun withValue(key: String, value: Any?): SQLiteQuery {
        when (value) {
            value == null -> values.putNull(key)
            is String -> values.put(key, value as String?)
            is Byte -> values.put(key, value as Byte?)
            is Short -> values.put(key, value as Short?)
            is Int -> values.put(key, value as Int?)
            is Long -> values.put(key, value as Long?)
            is Float -> values.put(key, value as Float?)
            is Double -> values.put(key, value as Double?)
            is Boolean -> values.put(key, value as Boolean?)
            is ByteArray -> values.put(key, value as ByteArray?)
            else -> throw IllegalArgumentException("bad value type: " + value?.javaClass?.name)
        }
        return this
    }

    fun withValues(values: ContentValues): SQLiteQuery {
        this.values.putAll(values)
        return this
    }

    fun where(where: String): SQLiteQuery {
        if (where.isNotBlank())
            this.where = StringBuilder(where)
        return this
    }

    fun andWhere(where: String): SQLiteQuery {
        if (where.isNotBlank())
            provideWhere().append(AND).append(where)
        return this
    }

    fun orWhere(where: String): SQLiteQuery {
        if (where.isNotBlank())
            provideWhere().append(OR).append(where)
        return this
    }

    fun withArgs(vararg whereArgs: String): SQLiteQuery {
        if (whereArgs.isNotEmpty())
            this.whereArgs.addAll(whereArgs)
        return this
    }

    fun orderBy(orderBy: String): SQLiteQuery {
        if (orderBy.isNotBlank())
            this.orderBy.add(orderBy)
        return this
    }

    fun select(db: SQLiteDatabase): Cursor? {
        val columns = if (columns.isEmpty()) null else columns.toTypedArray()
        val where = where?.toString()
        val whereArgs = if (whereArgs.isEmpty()) null else whereArgs.toTypedArray()
        val orderBy = if (orderBy.isEmpty()) null else TextUtils.join(COMMA, orderBy)
        return db.query(tables, columns, where, whereArgs, null, null, orderBy)
    }

    fun insert(db: SQLiteDatabase): Long {
        return db.insert(tables, BaseColumns._ID, values)
    }

    fun update(db: SQLiteDatabase): Int {
        val where = where?.toString()
        val whereArgs = if (whereArgs.isEmpty()) null else whereArgs.toTypedArray()
        return db.update(tables, values, where, whereArgs)
    }

    fun delete(db: SQLiteDatabase): Int {
        val where = where?.toString()
        val whereArgs = if (whereArgs.isEmpty()) null else whereArgs.toTypedArray()
        return db.delete(tables, where, whereArgs)
    }

    private fun provideWhere(): StringBuilder {
        if (where == null)
            where = StringBuilder()
        return where!!
    }

}