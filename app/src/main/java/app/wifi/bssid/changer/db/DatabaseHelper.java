package app.wifi.bssid.changer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.io.File;

public class DatabaseHelper {
    private static final String DATABASE_PATH = "/data/data/app.wifi.bssid.changer/database/data.db";
    private static final String TABLE_SETTINGS = "settings";
    private static final String COLUMN_KEY = "key_name";
    private static final String COLUMN_VALUE = "value_data";
    private static final String COLUMN_DEFAULT = "default_data";

    private SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        File dbFile = new File(DATABASE_PATH);
        if (dbFile.getParentFile() != null && !dbFile.getParentFile().exists()) {
            dbFile.getParentFile().mkdirs();
        }
        database = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        createTable();
    }

    private void createTable() {
        String createTableSql = "CREATE TABLE IF NOT EXISTS " + TABLE_SETTINGS + " (" +
                COLUMN_KEY + " TEXT PRIMARY KEY, " +
                COLUMN_VALUE + " TEXT, " +
                COLUMN_DEFAULT + " TEXT" +
                ")";
        database.execSQL(createTableSql);
    }

    public void saveSetting(String key, String value, String defaultValue) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_KEY, key);
        values.put(COLUMN_VALUE, value);
        values.put(COLUMN_DEFAULT, defaultValue);
        database.replace(TABLE_SETTINGS, null, values);
    }

    public void updateSettingValue(String key, String value) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_VALUE, value);
        database.update(TABLE_SETTINGS, values, COLUMN_KEY + " = ?", new String[]{key});
    }

    public String getSettingValue(String key, String defaultValue) {
        String value = defaultValue;
        Cursor cursor = database.query(TABLE_SETTINGS, new String[]{COLUMN_VALUE},
                COLUMN_KEY + " = ?", new String[]{key}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int colIndex = cursor.getColumnIndex(COLUMN_VALUE);
                if (colIndex != -1) {
                    value = cursor.getString(colIndex);
                }
            }
            cursor.close();
        }
        return value;
    }

    public String getSettingDefault(String key, String defaultValue) {
        String value = defaultValue;
        Cursor cursor = database.query(TABLE_SETTINGS, new String[]{COLUMN_DEFAULT},
                COLUMN_KEY + " = ?", new String[]{key}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int colIndex = cursor.getColumnIndex(COLUMN_DEFAULT);
                if (colIndex != -1) {
                    value = cursor.getString(colIndex);
                }
            }
            cursor.close();
        }
        return value;
    }

    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}
