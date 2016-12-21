package ru.rovkinmax.globusittech.view

import ru.rovkinmax.globusittech.model.User

interface MainView {
    fun notifyItemMoved(fromPosition: Int, toPosition: Int)

    fun dispatchDataset(data: List<User>)

}