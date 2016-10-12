package CommonLib;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.vietdms.mobile.dmslauncher.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by My PC on 28/03/2016.
 */
public class AppManager {
    private static AppManager instance = null;

    private AppManager() {
        listSortedPackages = new ArrayList<>(200);
        listRecentPackages = new ArrayList<>(200);
        listSortedStrings = new ArrayList<>(200);
        mapApps = new HashMap<>(200);
        mapRunCounts = new HashMap<>(0);
    }

    public synchronized static AppManager inst() {
        if (instance == null) {
            instance = new AppManager();
            Log.d("AppManager", "Create new instance");
        }
        return instance;
    }

    public synchronized boolean init(Context context) {
        try {
            defaultIcon = ContextCompat.getDrawable(context, R.mipmap.android);

            HashSet<String> mapHideApps = new HashSet<>();
            String packages = Model.inst().getConfigValue(Const.ConfigKeys.HideApps);
            if (packages != null) {
                String[] arrPackages = packages.split(" ");
                for (int i = 0; i < arrPackages.length; i++) {
                    mapHideApps.add(arrPackages[i]);
                }
            }

            packageManager = context.getPackageManager();
            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> availableActivities = packageManager.queryIntentActivities(i, 0);
            for (ResolveInfo ri : availableActivities) {
                if ("com.vietdms.mobile.dmslauncher".equals(ri.activityInfo.packageName)) continue;
                insertApp(ri.activityInfo.packageName, ri.loadLabel(packageManager).toString(), ri.loadIcon(packageManager), mapHideApps.contains(ri.activityInfo.packageName));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        try {
            dbHelper = new DbHelper(context);
            db = dbHelper.getWritableDatabase();
            mapRunCounts = getMapConfigValues();
            recreateListRecentPackages(0);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean recreateListRecentPackages(int maxRecentApps) {
        ArrayList<Map.Entry<String, Integer>> listEntries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : mapRunCounts.entrySet()) {
            if (mapApps.containsKey(entry.getKey())) {
                listEntries.add(entry);
            }
        }
        Collections.sort(listEntries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> lhs, Map.Entry<String, Integer> rhs) {
                return rhs.getValue() - lhs.getValue();
            }
        });
        boolean needUpdate = false;
        if (maxRecentApps > 0) {
            for (int i = 0; i < listEntries.size(); i++) {
                if (i >= listRecentPackages.size()) {
                    if (listRecentPackages.size() < maxRecentApps) {
                        needUpdate = true;
                    }
                    break;
                }
                if (!listEntries.get(i).getKey().equals(listRecentPackages.get(i))) {
                    needUpdate = true;
                    break;
                }
            }
        }
        listRecentPackages.clear();
        for (int i = 0; i < listEntries.size(); i++) {
            listRecentPackages.add(listEntries.get(i).getKey());
        }
        return needUpdate;
    }

    private class App {
        public String label;
        public Drawable icon;
        public boolean isHidden;

        public App(String label, Drawable icon, boolean isHidden) {
            this.label = label;
            this.icon = icon;
            this.isHidden = isHidden;
        }
    }

    private PackageManager packageManager;
    private Drawable defaultIcon;
    private ArrayList<String> listSortedPackages, listRecentPackages;
    private ArrayList<String> listSortedStrings;
    private HashMap<String, App> mapApps;
    private HashMap<String, Integer> mapRunCounts;

    private synchronized int insertApp(String packageName, String label, Drawable icon, boolean isHidden) {
        if (mapApps.containsKey(packageName)) return -1;
        String rvalue = Utils.strToSort(label);
        int lower = 0;
        int upper = listSortedStrings.size() - 1;
        int cur = -1;
        int compare = -1;
        while (lower <= upper) {
            cur = (lower + upper) / 2;
            compare = listSortedStrings.get(cur).compareTo(rvalue);
            if (compare < 0)
                lower = cur + 1;
            else if (compare > 0)
                upper = cur - 1;
            else
                break;
        }
        if (compare <= 0) cur++;
        listSortedStrings.add(cur, rvalue);
        listSortedPackages.add(cur, packageName);
        mapApps.put(packageName, new App(label, icon, isHidden));
        return cur;
    }

    private synchronized int removeApp(String packageName) {
        int cur = -1;
        if (mapApps.containsKey(packageName)) {
            for (int i = 0; i < listSortedPackages.size(); i++) {
                if (listSortedPackages.get(i).equals(packageName)) {
                    listSortedPackages.remove(i);
                    listSortedStrings.remove(i);
                    cur = i;
                    break;
                }
            }
            mapApps.remove(packageName);
        }
        return cur;
    }

    private synchronized boolean isModelLockApp(String packageName) {
        if (packageName == null) return false;
        String packages = Model.inst().getConfigValue(Const.ConfigKeys.LockApps);
        if (packages == null) return false;
        String[] arrPackages = packages.split(" ");
        for (int i = 0; i < arrPackages.length; i++) {
            if (arrPackages[i].equals(packageName)) return true;
        }
        return false;
    }

    private synchronized boolean isModelHideApp(String packageName) {
        if (packageName == null) return false;
        String packages = Model.inst().getConfigValue(Const.ConfigKeys.HideApps);
        if (packages == null) return false;
        String[] arrPackages = packages.split(" ");
        for (int i = 0; i < arrPackages.length; i++) {
            if (arrPackages[i].equals(packageName)) return true;
        }
        return false;
    }

    public synchronized ArrayList<String> getListPackages(boolean recentApps) {
        if (recentApps) {
            int maxRecentApps = Model.inst().getMaxRecentApps();
            ArrayList<String> listClone = new ArrayList<>();
            for (int i = 0; i < listRecentPackages.size(); i++) {
                String packageName = listRecentPackages.get(i);
                if (!mapApps.get(packageName).isHidden) {
                    listClone.add(packageName);
                    if (listClone.size() >= maxRecentApps) break;
                }
            }
            return listClone;
        } else {
            ArrayList<String> listClone = new ArrayList<>();
            for (int i = 0; i < listSortedPackages.size(); i++) {
                String packageName = listSortedPackages.get(i);
                if (!mapApps.get(packageName).isHidden) {
                    listClone.add(packageName);
                }
            }
            return listClone;
        }
    }

    public synchronized boolean isHomeScreen(String packageName, Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName.equals(packageName);
    }

    public synchronized boolean addPackage(String packageName) {
        try {
            if ("com.vietdms.mobile.dmslauncher".equals(packageName)) return false;
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            String label = applicationInfo.loadLabel(packageManager).toString();
            Drawable icon = applicationInfo.loadIcon(packageManager);
            insertApp(packageName, label, icon, isModelHideApp(packageName));
            mapRunCounts.put(packageName, 0);
            setConfigValue(packageName, 0);
            if (recreateListRecentPackages(Model.inst().getMaxRecentApps())) {
                EventPool.view().enQueue(new EventType.EventListAppResult(getListPackages(true), true));
            }
            EventPool.view().enQueue(new EventType.EventListAppResult(getListPackages(false), false));
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public synchronized boolean remPackage(String packageName) {
        removeApp(packageName);
        if (mapRunCounts.containsKey(packageName)) {
            mapRunCounts.remove(packageName);
            clrConfigValue(packageName);
            if (recreateListRecentPackages(Model.inst().getMaxRecentApps())) {
                EventPool.view().enQueue(new EventType.EventListAppResult(getListPackages(true), true));
            }
        }
        EventPool.view().enQueue(new EventType.EventListAppResult(getListPackages(false), false));
        return true;
    }

    public synchronized boolean runApp(String packageName) {
        App app = mapApps.get(packageName);
        if (app == null) return false;
        Integer runCount = mapRunCounts.get(packageName);
        if (runCount == null) {
            runCount = 0;
        }
        runCount++;
        mapRunCounts.put(packageName, runCount);
        setConfigValue(packageName, runCount);
        if (recreateListRecentPackages(Model.inst().getMaxRecentApps())) {
            EventPool.view().enQueue(new EventType.EventListAppResult(getListPackages(true), true));
        }
        return true;
    }

    public synchronized void hideApps(String packages) {
        if (packages == null) return;
        if (!packages.equals(Model.inst().getConfigValue(Const.ConfigKeys.HideApps))) {
            HashSet<String> mapHideApps = new HashSet<>();
            String[] arrPackages = packages.split(" ");
            for (int i = 0; i < arrPackages.length; i++) {
                mapHideApps.add(arrPackages[i]);
            }
            for (Map.Entry<String, App> item : mapApps.entrySet()) {
                item.getValue().isHidden = mapHideApps.contains(item.getKey());
            }
            EventPool.view().enQueue(new EventType.EventListAppResult(getListPackages(false), false));
            EventPool.view().enQueue(new EventType.EventListAppResult(getListPackages(true), true));
        }
    }

    public synchronized boolean isLocked(String packageName) {
        return isModelLockApp(packageName);
    }

    public synchronized String getLabel(String packageName) {
        App app = mapApps.get(packageName);
        if (app == null) return "";
        return app.label;
    }

    public synchronized Drawable getIcon(String packageName) {
        App app = mapApps.get(packageName);
        if (app == null || app.icon == null) return defaultIcon;
        return app.icon;
    }


    private synchronized HashMap<String, Integer> getMapConfigValues() {
        HashMap<String, Integer> map = new HashMap<>();
        try {
            Cursor cursor = db.rawQuery("select PackageName,RunCount from " + DbHelper.CONFIG_NAME, null);
            int len = cursor.getCount();
            if (len > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < len; i++) {
                    String packageName = cursor.getString(0);
                    int runCount = cursor.getInt(1);
                    map.put(packageName, runCount);
                    cursor.moveToNext();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    private synchronized boolean clrConfigValue(String pakageName) {
        try {
            db.delete(DbHelper.CONFIG_NAME, "PackageName=?", new String[]{pakageName});
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public synchronized int getConfigValue(String packageName) {
        try {
            Cursor cursor = db.rawQuery("select RunCount from " + DbHelper.CONFIG_NAME + " where PackageName=?", new String[]{packageName});
            if (cursor.getCount() == 0) return -1;
            cursor.moveToFirst();
            return cursor.getInt(0);
        } catch (Exception ex) {
            return -2;
        }
    }

    private synchronized boolean setConfigValue(String pakageName, int runCount) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("RunCount", runCount);
            if (getConfigValue(pakageName) < 0) {
                cv.put("PackageName", pakageName);
                return db.insert(DbHelper.CONFIG_NAME, null, cv) >= 0;
            } else {
                return db.update(DbHelper.CONFIG_NAME, cv, "PackageName=?", new String[]{pakageName}) > 0;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    private SQLiteDatabase db = null;
    private DbHelper dbHelper = null;

    private class DbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "edms_apps.db";
        public static final String CONFIG_NAME = "tblApps";
        public static final String SQL_CREATE_CONFIG = "create table " + CONFIG_NAME + " ("
                + "PackageName text primary key"
                + ",RunCount int not null"
                + ");";
        public static final String SQL_DELETE_CONFIG = "drop table if exists " + CONFIG_NAME + ";";

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_CONFIG);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            Log.i("DbHelper", "Change version from " + oldVersion + " to " + newVersion);
            db.execSQL(SQL_DELETE_CONFIG);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
