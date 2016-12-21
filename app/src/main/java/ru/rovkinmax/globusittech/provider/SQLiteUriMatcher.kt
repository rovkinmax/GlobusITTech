package ru.rovkinmax.globusittech.provider

import android.database.sqlite.SQLiteException
import android.net.Uri
import android.text.TextUtils

object SQLiteUriMatcher {

    const val MATCH_ALL = 1
    const val MATCH_ID = 2
    const val MATCH_SEARCH = 3

    fun match(uri: Uri): Int {
        val pathSegments = uri.pathSegments
        val pathSegmentsSize = pathSegments.size
        if (pathSegmentsSize == 1) {
            if (SQLiteUtils.getSearchQuery(uri).isNullOrBlank().not())
                return MATCH_SEARCH
            return MATCH_ALL
        } else if (pathSegmentsSize == 2 && TextUtils.isDigitsOnly(pathSegments[1]))
            return MATCH_ID
        throw SQLiteException("Unknown uri '$uri'")
    }
}

