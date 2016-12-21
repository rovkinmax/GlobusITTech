package ru.rovkinmax.globusittech.util

import android.support.v7.widget.RecyclerView


interface OnStartDragListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}