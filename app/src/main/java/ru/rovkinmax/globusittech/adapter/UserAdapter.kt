package ru.rovkinmax.globusittech.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.rovkinmax.globusittech.R
import ru.rovkinmax.globusittech.model.User
import ru.rovkinmax.globusittech.util.OnStartDragListener

class UserAdapter(var userList: MutableList<User>, val onStartDragListener: OnStartDragListener) : RecyclerView.Adapter<UserHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val inflater = LayoutInflater.from(parent.context)
        return UserHolder(inflater.inflate(R.layout.item_user, parent, false))
    }

    override fun onBindViewHolder(holder: UserHolder?, position: Int) {
        holder?.itemView?.setOnLongClickListener {
            onStartDragListener.onStartDrag(holder)
            return@setOnLongClickListener true
        }
        holder?.bindView(userList[position])
    }

    override fun getItemCount(): Int = userList.size
}

class UserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val userName by lazy { itemView.findViewById(R.id.tvName) as TextView }

    fun bindView(user: User) {
        userName.text = user.name
    }
}