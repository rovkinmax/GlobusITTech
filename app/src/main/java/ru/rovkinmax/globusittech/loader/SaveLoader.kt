package ru.rovkinmax.globusittech.loader

import android.content.AsyncTaskLoader
import android.content.ContentProviderOperation
import android.content.Context
import ru.rovkinmax.globusittech.model.User
import ru.rovkinmax.globusittech.model.UserTable

class SaveLoader(private val userList: List<User>, context: Context) : AsyncTaskLoader<List<User>>(context) {

    override fun loadInBackground(): List<User> {
        val operations = arrayListOf<ContentProviderOperation>().apply { addAll(userList.map { newInsertOperation(it) }) }
        context.contentResolver.applyBatch(UserTable.INSTANCE.authority, operations)
        return userList
    }

    fun newInsertOperation(user: User): ContentProviderOperation {
        return ContentProviderOperation.newInsert(UserTable.INSTANCE.uri)
                .withValues(user.toValues())
                .build()
    }
}