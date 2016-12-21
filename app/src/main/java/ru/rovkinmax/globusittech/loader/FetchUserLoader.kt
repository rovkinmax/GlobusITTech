package ru.rovkinmax.globusittech.loader

import android.content.AsyncTaskLoader
import android.content.Context
import ru.rovkinmax.globusittech.model.User
import ru.rovkinmax.globusittech.model.UserTable
import ru.rovkinmax.globusittech.presenter.MainPresenter

class FetchUserLoader(context: Context?) : AsyncTaskLoader<List<User>>(context) {

    override fun loadInBackground(): List<User> {
        val cursor = context.contentResolver.query(UserTable.INSTANCE.uri, null, null, null, null)
        val userList = arrayListOf<User>()
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    userList.add(User(cursor))
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
        }

        return MainPresenter.restoreListOrder(userList)
    }
}