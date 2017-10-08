package com.wite.positionerwear.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2017/9/12.
 */

public class DBHelper extends SQLiteOpenHelper {
    private final static int VERSION = 1;
    private final static String DB_NAME = "phones.db";
    private final static String TABLE_NAME = "phone";
    private static final String TAG = "TAG";
   // private final static String CREATE_TBL = "create table phone(_id integer primary key autoincrement, name text, phonenumber text)";
    private final static String CREATE_TBL = "create table phone(_id integer primary key autoincrement, name text, phonenum text,letter text,inttime text)";


    private SQLiteDatabase db;


    //SQLiteOpenHelper子类必须要的一个构造函数
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        //必须通过super 调用父类的构造函数
        super(context, name, factory, version);
    }

    //数据库的构造函数，传递三个参数的
    public DBHelper(Context context, String name, int version){
        this(context, name, null, version);
    }

    //数据库的构造函数，传递一个参数的， 数据库名字和版本号都写死了
    public DBHelper(Context context){
        this(context, DB_NAME, null, VERSION);
    }

    // 回调函数，第一次创建时才会调用此函数，创建一个数据库
    @Override
    public void onCreate(SQLiteDatabase db) {


        this.db = db;
        Log.e(TAG, "onCreate: 创建数据库" );
        db.execSQL(CREATE_TBL);
    }

    //回调函数，当你构造DBHelper的传递的Version与之前的Version调用此函数
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.e(TAG, "onUpgrade: update Database" );
    }

    //插入方法
    public void insert(ContentValues values){
        //获取SQLiteDatabase实例
        SQLiteDatabase db = getWritableDatabase();
        //插入数据库中
        db.insert(TABLE_NAME, null, values);
        db.close();
        Log.e(TAG, "insert: 插入数据库 表名："+TABLE_NAME );

    }

    //查询方法
    public Cursor query(){
        SQLiteDatabase db = getReadableDatabase();
        //获取Cursor
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null, null);

        Log.e(TAG, "query: 查询全部数据"+TABLE_NAME );

        return c;

    }

    //查询方法  phonenum
    public Cursor queryfornumber(String[] argment){
        SQLiteDatabase db = getReadableDatabase();
        //获取Cursor
        Cursor c = db.query(TABLE_NAME, null,"phonenum=?", argment, null, null, null, null);
        Log.e(TAG, "query: 根据手机号码查询"+TABLE_NAME );
        return c;

    }

    //根据唯一标识_id  来删除数据
    public void delete(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, "_id=?", new String[]{String.valueOf(id)});

        Log.e(TAG, "delete: 很据id删除数据"+id );
    }
    public void celer(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_NAME);

        Log.e(TAG, "celer: 清空数据" );
    }









    //更新数据库的内容
    public void update(ContentValues values, String whereClause, String[]whereArgs){
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_NAME, values, whereClause, whereArgs);
        Log.e(TAG, "update:  更新表"+values);
    }

    //关闭数据库
    public void close(){
        if(db != null){
            db.close();
            Log.e(TAG, "close: 关闭数据库" );
        }
    }

}
