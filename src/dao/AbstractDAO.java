package com.cnpc.bgp.seismiccrew.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

import dao.SqlHelper;
import dao.core.TypeConverter;
import utils.ReflectionUtil;

public abstract class AbstractDAO <T> {

    private static final int DATABASE_VERSION = 1;

    public class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String dbPath) {
            super(context, dbPath, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(getCreateTableSql());
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    private DatabaseHelper _dbHelper;
    private SQLiteDatabase _db;
    private SqlHelper _sqlHelper;

    public AbstractDAO(Context context) {
        _sqlHelper = new SqlHelper(context, getSqlFileName());
        _dbHelper = new DatabaseHelper(context, context.getExternalFilesDir(null).getPath() + "/database/" + getDbFileName());
//        _dbHelper = new DatabaseHelper(context, Environment.getExternalStorageDirectory() + "/database/" + getDbFileName());
    }

    public abstract String getTableName();
    public abstract String getDbFileName();
    public abstract String getSqlFileName();
    public abstract String getCreateTableSql();
    public abstract Class getEntityClass();

    public AbstractDAO open() throws SQLException {
        try {
            _db = _dbHelper.getWritableDatabase();
        } catch (Exception e ) {
            Logger.e(e.getMessage());
            throw e;
        }
        return this;
    }

    public void close() {
        _dbHelper.close();
    }

    public String getSql(String sqlid) {
        return _sqlHelper.getSql(sqlid);
    }

    public String getParsedSql(String sqlid, Map<String, Object> param) {
        return _sqlHelper.getParsedSql(sqlid, param);
    }

    public String getParsedSql(String sqlid, Object param) {
        return _sqlHelper.getParsedSql(sqlid, param);
    }

    public DatabaseHelper get_dbHelper() {
        return _dbHelper;
    }

    public SQLiteDatabase get_db() {
        return _db;
    }

    public SqlHelper get_sqlHelper() {
        return _sqlHelper;
    }

    public interface Callback {
        void dosth();
    }

    public void withOpenDb(Callback d) {
        if (_db == null || !_db.isOpen()) {
            try {
                open();
                d.dosth();
            } finally {
                close();
            }
        } else {
            d.dosth();
        }
    }

    public void withTransaction(Callback d) {
        withOpenDb(()->{
            if (!_db.inTransaction()) {
                try {
                    _db.beginTransaction();
                    d.dosth();
                    _db.setTransactionSuccessful();
                } finally {
                    _db.endTransaction();
                }
            } else {
                d.dosth();
            }
        });
    }

    public Long insert(T sv) {
        if (sv == null) return null;
        ContentValues args = ReflectionUtil.object2ContentValue(sv);
        return _db.insert(getTableName(), null, args);
    }

    public boolean update(T svList) {
        if (svList == null) return false;
        _db.execSQL(_sqlHelper.getParsedSql("update", svList));
        return true;
    }

    public boolean update(List<T> svList) {
        if (svList == null) return false;
        for (T sv : svList) {
            _db.execSQL(_sqlHelper.getParsedSql("update", sv));
        }
        return true;
    }

    public boolean deleteById(long rowId) {
        return _db.delete(getTableName(), "id=" + rowId, null) > 0;
    }

    public void dropTable() {
        _db.execSQL("DROP TABLE IF EXISTS " + getTableName());
    }

    public boolean exists(T sv) {
        Cursor cursor = _db.rawQuery(_sqlHelper.getParsedSql("selectByCond", sv), null);
        return cursor.getCount() > 0;
    }

    public List<T> selectAll() {
        Cursor cursor = _db.rawQuery(_sqlHelper.getSql("selectAll"), null);
//        Cursor cursor = _db.query("record", null, null, null, null, null, null);
        List<T> tasks = new TypeConverter<T>(cursor).toBeans(getEntityClass());
        return tasks;
    }

    public T selectById(long id) {
        String[] selectionArgs = new String[] { String.valueOf(id) };
        Cursor cursor = _db.query(getTableName(), null, "id=?", selectionArgs, null, null, null);
        List<T> tasks = new TypeConverter<T>(cursor).toBeans(getEntityClass());
        return tasks != null && tasks.size() > 0 ? tasks.get(0) : null;
    }

    public List<T> selectByCond(T query) {
        Cursor cursor = _db.rawQuery(_sqlHelper.getParsedSql("selectByCond", query), null);
        List<T> tasks = new TypeConverter<T>(cursor).toBeans(getEntityClass());
        return tasks;
    }
}
