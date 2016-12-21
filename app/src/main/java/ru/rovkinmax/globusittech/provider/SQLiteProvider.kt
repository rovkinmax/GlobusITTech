package ru.rovkinmax.globusittech.provider

import ru.rovkinmax.globusittech.BuildConfig
import ru.rovkinmax.globusittech.model.UserTable

class SQLiteProvider : SQLiteContentProvider() {

    companion object {
        val AUTHORITY = BuildConfig.APPLICATION_ID
        val DATABASE_NAME = "bd.db"
        private val DATABASE_VERSION = 1
    }

    override fun onCreateSchema(): SQLiteSchema {
        return SQLiteSchema(context!!, DATABASE_NAME, DATABASE_VERSION)
                .register(UserTable.INSTANCE)
    }
}

