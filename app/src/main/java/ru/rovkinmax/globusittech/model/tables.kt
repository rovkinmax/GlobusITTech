package ru.rovkinmax.globusittech.model

import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import ru.rovkinmax.globusittech.provider.SQLiteProvider
import ru.rovkinmax.globusittech.provider.SQLiteTable
import ru.rovkinmax.globusittech.provider.createTable

class UserTable private constructor() : SQLiteTable(SQLiteProvider.AUTHORITY, "user") {
    companion object {
        val INSTANCE = UserTable()
    }

    override fun onCreate(db: SQLiteDatabase) {
        createTable(name) {
            rowId()
            textColumn(Columns.NAME)
            intColumn(Columns.PREV, -1)
            intColumn(Columns.NEXT, -1)
            execute(db)
        }
    }

    interface Columns : BaseColumns {
        companion object {
            const val ID = "_id"
            const val NAME = "name"
            const val NEXT = "next"
            const val PREV = "prev"
        }
    }
}
