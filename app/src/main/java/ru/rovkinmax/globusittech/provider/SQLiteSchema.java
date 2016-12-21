package ru.rovkinmax.globusittech.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SQLiteSchema extends SQLiteOpenHelper {

    private final Map<String, SQLiteTable> tableMap = new ConcurrentHashMap<>();

    public SQLiteSchema(@NonNull Context context, int version) {
        super(context, null, null, version);
    }

    public SQLiteSchema(@NonNull Context context, @Nullable String name, int version) {
        super(context, name, null, version);
    }

    public SQLiteSchema register(@NonNull SQLiteTable table) {
        tableMap.put(table.getName(), table);
        return this;
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.beginTransactionNonExclusive();
        try {
            for (final SQLiteTable table : tableMap.values()) {
                table.onCreate(db);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransactionNonExclusive();
        try {
            for (final SQLiteTable table : tableMap.values()) {
                table.onUpgrade(db, oldVersion, newVersion);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @NonNull
    SQLiteTable acquireTable(@NonNull Uri uri) {
        return acquireTable(uri.getPathSegments().get(0));
    }

    @NonNull
    SQLiteTable acquireTable(@NonNull String name) {
        final SQLiteTable table = tableMap.get(name);
        if (table != null) {
            return table;
        }
        throw new SQLiteException("No such table '" + name + "'");
    }

}

