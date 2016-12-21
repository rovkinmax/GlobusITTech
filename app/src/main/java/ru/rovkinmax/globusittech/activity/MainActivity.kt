package ru.rovkinmax.globusittech.activity

import android.content.Loader
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import kotlinx.android.synthetic.main.activity_main.*
import ru.rovkinmax.globusittech.R
import ru.rovkinmax.globusittech.adapter.UserAdapter
import ru.rovkinmax.globusittech.loader.FetchUserLoader
import ru.rovkinmax.globusittech.loader.LoaderProvider
import ru.rovkinmax.globusittech.loader.SaveLoader
import ru.rovkinmax.globusittech.loader.UpdateLoader
import ru.rovkinmax.globusittech.model.User
import ru.rovkinmax.globusittech.presenter.MainPresenter
import ru.rovkinmax.globusittech.util.ItemTouchHelperAdapter
import ru.rovkinmax.globusittech.util.OnStartDragListener
import ru.rovkinmax.globusittech.util.SimpleItemTouchHelperCallback
import ru.rovkinmax.globusittech.view.MainView
import java.util.*

class MainActivity : AppCompatActivity(), ItemTouchHelperAdapter, OnStartDragListener, LoaderProvider<List<User>>, MainView {


    private var itemTouchHelper: ItemTouchHelper? = null
    private lateinit var userAdapter: UserAdapter
    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userAdapter = UserAdapter(arrayListOf(), this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = userAdapter
        val callback = SimpleItemTouchHelperCallback(this)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper?.attachToRecyclerView(recyclerView)
        presenter = MainPresenter(this, loaderManager, this)
        presenter.loadData()
    }


    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        presenter.swapList(userAdapter.userList, fromPosition, toPosition)
        return true
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper?.startDrag(viewHolder)
    }

    override fun notifyItemMoved(fromPosition: Int, toPosition: Int) {
        userAdapter.notifyItemMoved(fromPosition, toPosition)
    }

    override fun dispatchDataset(data: List<User>) {
        userAdapter.userList = ArrayList(data)
        userAdapter.notifyDataSetChanged()
    }

    override fun provideLoader(id: Int, data: List<User>): Loader<List<User>> {
        return when (id) {
            R.id.apply_loader -> UpdateLoader(data, applicationContext)
            R.id.fetch_loader -> FetchUserLoader(applicationContext)
            R.id.save_loader -> SaveLoader(data, applicationContext)
            else -> throw IllegalArgumentException("unknown loader id = $id")
        }
    }
}
