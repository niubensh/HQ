package com.example.contentproviderpractice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "person.db"; //数据库名称  
    private static final int version = 1; //数据库版本  
      
    public DBOpenHelper(Context context) {  
        super(context, DB_NAME, null, version);  
    }  
   
    @Override  
    public void onCreate(SQLiteDatabase db) { //此方法只有在调用getWritableDatabase()或getReadableDatabase()方法时才会执行  
        String sql = "create table personData("+  
                 "_id        integer primary key," +       
                 "name      varchar(20) not null," +       
                 "age       Integer," +       
                 "introduce varchar(20) not null)";           
        db.execSQL(sql);  
    }  
    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade    
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	
    }
}
