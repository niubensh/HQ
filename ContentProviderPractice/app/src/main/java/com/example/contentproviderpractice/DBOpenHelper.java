package com.example.contentproviderpractice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "person.db"; //���ݿ�����  
    private static final int version = 1; //���ݿ�汾  
      
    public DBOpenHelper(Context context) {  
        super(context, DB_NAME, null, version);  
    }  
   
    @Override  
    public void onCreate(SQLiteDatabase db) { //�˷���ֻ���ڵ���getWritableDatabase()��getReadableDatabase()����ʱ�Ż�ִ��  
        String sql = "create table personData("+  
                 "_id        integer primary key," +       
                 "name      varchar(20) not null," +       
                 "age       Integer," +       
                 "introduce varchar(20) not null)";           
        db.execSQL(sql);  
    }  
    //���DATABASE_VERSIONֵ����Ϊ2,ϵͳ�����������ݿ�汾��ͬ,�������onUpgrade    
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	
    }
}
