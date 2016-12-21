package ru.rovkinmax.globusittech.loader

import android.content.Loader
import ru.rovkinmax.globusittech.model.User

interface LoaderProvider<T> {
    companion object {
        const val KEY_DATA = "data"
    }

    fun provideLoader(id: Int, data: List<User>): Loader<T>
}