package CommonLib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.vietdms.mobile.dmslauncher.BuildConfig;

/**
 * Created by My PC on 03/03/2016.
 */
public class SystemLog {
    private static SystemLog instance = null;
    private SystemLog() { }
    public synchronized static SystemLog inst(){
        if (instance == null) {
            instance = new SystemLog();
            Log.d("SystemLog", "Create new instance");
        }
        return instance;
    }
    private DBHelper dbHelper = null;
    public boolean init(Context context) {
        try {
            dbHelper = new DBHelper(context);
            addLog(Type.AppStart, null);
        }
        catch (Exception ex) {
            Log.w("SystemLog_init", ex.toString());
            return false;
        }
        return true;
    }


    public enum Type{
        PhoneStart,
        PhoneStop,
        AppStart,
        AppStop,
        AppCrash,
        SwitchOn3G,
        SwitchOff3G,
        SwitchOnGps,
        SwitchOffGps,
        SwitchOnAirplaneMode,
        SwitchOffAirplaneMode,
        SwitchOnBatterySaver,
        SwitchOffBatterySaver,
        Exception,
        Error,
        Warning,
        SwitchOnWifi,
        SwitchOffWifi,
    }

    private static class AsyncAddLog extends AsyncTask<String, Void, String> {
        public AsyncAddLog(Context context, Type type, String note) {
            this.context = context;
            this.type = type;
            this.note = note;
        }
        private Context context;
        private Type type;
        private String note;
        @Override
        protected String doInBackground(String... params) {
            try {
                DBHelper dbHelper = new DBHelper(context);
                long id = dbHelper.addLog(type, note);
                Log.i("SystemLog_addLog", id + " " + type.toString() + " " + note);
                dbHelper.close();
            }
            catch (Exception ex) {
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
        }
    }
    public static boolean addLog(Context context, Type type, String note) {
        try {
            Log.i("SystemLog_addLog", type.toString() + " " + note);
            AsyncAddLog asyncAddLog = new AsyncAddLog(context, type, note);
            asyncAddLog.execute();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addLog(Type type, String note) {
        long id = dbHelper.addLog(type, note);
        Log.i("SystemLog_addLog", id + " " + type.toString() + " " + note);
        return id > 0;
    }
    public void setAck(int[] idsOK) {
        dbHelper.setAck(idsOK);
    }
    public SystemLogItem[] getAllUnAck(int n) {
        return dbHelper.getAllUnAck(n);
    }
    private static class DBHelper {
        public DBHelper(Context context) {
            dbHelper = new DbHelper(context);
            db = dbHelper.getWritableDatabase();
            Log.i("SystemLog_DBHelper", "database initialized");
        }

        public synchronized void close() {
            try {
                dbHelper.close();
                db.close();
            }
            catch (Exception ex) {
            }
        }

        public synchronized long addLog(Type type, String note) {
            ContentValues cv = new ContentValues();
            cv.put("Date", Model.getServerTime());
            cv.put("Type", type.ordinal());
            cv.put("Note", note);
            cv.put("AppVersion", BuildConfig.VERSION_CODE);
            long rowId = db.insert(DbHelper.TABLE_NAME, null, cv);
            return rowId;
        }

        public synchronized SystemLogItem[] getAllUnAck(int n) {
            Cursor cursor = db.rawQuery("select * from " + DbHelper.TABLE_NAME + " order by RowID desc limit " + n, null);
            SystemLogItem[] result = new SystemLogItem[cursor.getCount()];
            if (result.length > 0) {
                cursor.moveToFirst();
                for (int i = result.length - 1; i >= 0; i--) {
                    SystemLogItem logItem = new SystemLogItem();
                    logItem.rowID = cursor.getInt(cursor.getColumnIndex("RowID"));
                    logItem.date = cursor.getLong(cursor.getColumnIndex("Date"));
                    logItem.type = cursor.getInt(cursor.getColumnIndex("Type"));
                    logItem.note = cursor.getString(cursor.getColumnIndex("Note"));
                    logItem.appVersion = cursor.getInt(cursor.getColumnIndex("AppVersion"));
                    result[i] = logItem;
                    cursor.moveToNext();
                }
            }
            return result;
        }

        public synchronized void setAck(int[] idsOK) {
            if (idsOK == null || idsOK.length == 0) return;
            StringBuilder sb = new StringBuilder();
            sb.append(" RowID=");
            sb.append(idsOK[0]);
            for (int i = 1; i < idsOK.length; i++) {
                sb.append(" or RowID=");
                sb.append(idsOK[i]);
            }
            db.execSQL("delete from " + DbHelper.TABLE_NAME + " where" + sb.toString());
        }

        private SQLiteDatabase db = null;
        private DbHelper dbHelper = null;
        private class DbHelper extends SQLiteOpenHelper {
            // If you change the database schema, you must increment the database version.
            public static final int DATABASE_VERSION = 3;
            public static final String DATABASE_NAME = "edms_log.db";
            public static final String TABLE_NAME = "tblSystemLog";
            public static final String SQL_CREATE_ENTRIES = "create table " + TABLE_NAME + " ("
                    + "RowID integer primary key autoincrement"
                    + ",Date bigint"
                    + ",Type int"
                    + ",Note ntext"
                    + ",AppVersion int"
                    + ");";
            public static final String SQL_DELETE_ENTRIES = "drop table if exists " + TABLE_NAME + ";";

            public DbHelper(Context context) {
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
            }
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(SQL_CREATE_ENTRIES);
            }
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                // This database is only a cache for online data, so its upgrade policy is
                // to simply to discard the data and start over
                Log.i("SystemLog", "Change version from " + oldVersion + " to " + newVersion);
                db.execSQL(SQL_DELETE_ENTRIES);
                onCreate(db);
            }
            @Override
            public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                onUpgrade(db, oldVersion, newVersion);
            }
        }
    }
}
