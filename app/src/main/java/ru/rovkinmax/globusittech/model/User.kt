package ru.rovkinmax.globusittech.model

import android.content.ContentValues
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import ru.rovkinmax.globusittech.model.UserTable.Columns

data class User(var id: Int = 0, var prev: Int = 0, var next: Int = 0, var name: String = "") : Parcelable {

    companion object {
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this() {
        with(parcel) {
            id = readInt()
            prev = readInt()
            next = readInt()
            name = readString()
        }
    }

    constructor(cursor: Cursor) : this() {
        id = cursor.getInt(cursor.getColumnIndex(Columns.ID))
        prev = cursor.getInt(cursor.getColumnIndex(Columns.PREV))
        next = cursor.getInt(cursor.getColumnIndex(Columns.NEXT))
        name = cursor.getString(cursor.getColumnIndex(Columns.NAME))
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.let { parcel ->
            parcel.writeInt(id)
            parcel.writeInt(prev)
            parcel.writeInt(next)
            parcel.writeString(name)
        }
    }

    override fun describeContents(): Int = 0

    fun toValues(): ContentValues {
        return ContentValues().apply {
            put(Columns.ID, id)
            put(Columns.NAME, name)
            put(Columns.PREV, prev)
            put(Columns.NEXT, next)
        }
    }

    fun toPositionValues(): ContentValues {
        return ContentValues().apply {
            put(Columns.PREV, prev)
            put(Columns.NEXT, next)
        }
    }


}
