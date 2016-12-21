package ru.rovkinmax.globusittech.presenter

import android.app.LoaderManager
import android.content.Loader
import android.os.Bundle
import ru.rovkinmax.globusittech.R
import ru.rovkinmax.globusittech.loader.LoaderProvider
import ru.rovkinmax.globusittech.model.User
import ru.rovkinmax.globusittech.view.MainView
import java.util.*

class MainPresenter(private val view: MainView, private val loaderManager: LoaderManager,
                    val loaderProvider: LoaderProvider<List<User>>) : LoaderManager.LoaderCallbacks<List<User>> {

    companion object {

        fun restoreListOrder(list: List<User>): List<User> {
            val restored = arrayListOf<User>()
            if (list.isNotEmpty()) {
                var item = list.first { it.prev == -1 }
                restored.add(item)
                while (list.size != restored.size) {
                    item = list.firstOrNull { it.id == item.next } ?: list.first { it.next == -1 }
                    restored.add(item)
                }
            }
            return restored
        }
    }

    fun swapList(list: MutableList<User>, fromPosition: Int, toPosition: Int) {
        val listForUpdate = arrayListOf<User>()
        val item = list[fromPosition]
        listForUpdate.add(item)

        val newLeft = if (toPosition == list.size - 1) list[toPosition] else if (toPosition > 0) list[toPosition - 1] else null
        newLeft?.let { listForUpdate.add(it) }

        val newRight = if (toPosition < list.size - 1) list[toPosition] else null
        newRight?.let { listForUpdate.add(it) }

        newLeft?.next = item.id
        newRight?.prev = item.id

        item.prev = newLeft?.id ?: -1
        item.next = newRight?.id ?: -1

        val oldLeft = if (fromPosition > 0) list[fromPosition - 1] else null
        oldLeft?.let { listForUpdate.add(it) }

        val oldRight = if (fromPosition < list.size - 1) list[fromPosition + 1] else null
        oldRight?.let { listForUpdate.add(it) }

        oldLeft?.next = oldRight?.id ?: -1
        oldRight?.prev = oldLeft?.id ?: -1

        list.remove(item)
        list.add(toPosition, item)
        view.notifyItemMoved(fromPosition, toPosition)

        dispatchListForUpdateDataBase(listForUpdate)
    }

    fun dispatchListForUpdateDataBase(list: List<User>) {
        val bundle = bundleWithUserList(list)
        loaderManager.restartLoader(R.id.apply_loader, bundle, this).forceLoad()
    }

    private fun bundleWithUserList(list: List<User>): Bundle {
        val bundle = Bundle().apply { putParcelableArrayList(LoaderProvider.KEY_DATA, ArrayList(list)) }
        return bundle
    }

    override fun onLoadFinished(loader: Loader<List<User>>, data: List<User>) {
        when (loader.id) {
            R.id.fetch_loader ->
                if (data.isEmpty()) {
                    val list = generateUser()
                    view.dispatchDataset(list)
                    loaderManager.restartLoader(R.id.save_loader, bundleWithUserList(list), this).forceLoad()
                } else view.dispatchDataset(data)
        }
    }

    private fun generateUser(): MutableList<User> {
        val range = 0..20
        return range.mapTo(arrayListOf<User>()) { position ->
            User().apply {
                id = position
                prev = position - 1
                next = if (range.contains(position + 1)) position + 1 else -1
                name = "Name $position"
            }
        }
    }

    override fun onLoaderReset(loader: Loader<List<User>>?) {

    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<User>> {
        val userList: List<User> = args?.getParcelableArrayList(LoaderProvider.KEY_DATA) ?: arrayListOf()
        return loaderProvider.provideLoader(id, userList)
    }

    fun loadData() {
        loaderManager.restartLoader(R.id.fetch_loader, Bundle(), this).forceLoad()
    }
}