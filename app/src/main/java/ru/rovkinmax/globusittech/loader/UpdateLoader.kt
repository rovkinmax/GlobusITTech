package ru.rovkinmax.globusittech.loader

import android.content.AsyncTaskLoader
import android.content.ContentProviderOperation
import android.content.Context
import ru.rovkinmax.globusittech.model.User
import ru.rovkinmax.globusittech.model.UserTable

class UpdateLoader(private val userList: List<User>, context: Context) : AsyncTaskLoader<List<User>>(context) {

    override fun loadInBackground(): List<User> {
        val operations = arrayListOf<ContentProviderOperation>().apply { addAll(userList.map { newUpdateOperation(it) }) }
        context.contentResolver.applyBatch(UserTable.INSTANCE.authority, operations)
        return userList
    }

    fun newUpdateOperation(user: User): ContentProviderOperation {
        return ContentProviderOperation.newUpdate(UserTable.INSTANCE.uri)
                .withSelection("${UserTable.Columns.ID}=?", arrayOf(user.id.toString()))
                .withValues(user.toPositionValues())
                .build()
    }
}