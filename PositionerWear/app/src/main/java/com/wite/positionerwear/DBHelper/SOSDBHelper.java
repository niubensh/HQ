package com.wite.positionerwear.DBHelper;

/**
 * Created by Administrator on 2017/9/20.
 */

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wite.positionerwear.R;
import com.wite.positionerwear.service.Item;

import java.util.ArrayList;


public class SOSDBHelper extends SQLiteOpenHelper {
    public final static String dbName = "SosDB";
    public static final String TABLE_SOS_CONTACT = "SosContact";
    public static final String TABLE_SMS_CONTENT = "SmsContent";
    public final static int VERSION = 1;
    public static SQLiteDatabase db = null;
    private final static String PREFERENCE_NAME = "com.sos.preference";

    public static Context mContext=null;

    public SOSDBHelper(Context context) {
        super(context, dbName, null, VERSION);
        mContext=context;
    }

    public SOSDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
        mContext=context;
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public static void closeDatabase() {
        if (db != null) {
            db.close();
        }
    }

    public boolean addItem(Item item) {
        openDatabase();
        ContentValues values = new ContentValues();
        values.put("name", item.getName());
        values.put("number", item.getNumber());
        long resultValue = db.insert(TABLE_SOS_CONTACT, null, values);
        closeDatabase();
        return resultValue > 0;
    }

    public boolean deletedItem(String name) {
        openDatabase();
        int resultValue = -1;
        if (name == null)
            resultValue = db.delete(TABLE_SOS_CONTACT, "", null);
        else
            resultValue = db.delete(TABLE_SOS_CONTACT, "name=?", new String[]{name});
        closeDatabase();
        return resultValue > 0;
    }


    public ArrayList<Item> getItemList() {
        openDatabase();
        Cursor cursor = null;
        ArrayList<Item> itemList = null;
        try {
            cursor = db.query(TABLE_SOS_CONTACT, null, null, null, null, null," id ASC ");
            if (cursor == null) {
                return null;
            }

            itemList = new ArrayList<Item>();
            while (cursor.moveToNext()) {
                Item item = new Item();
                item.setName(cursor.getString(cursor.getColumnIndex("name")));
                item.setNumber(cursor.getString(cursor.getColumnIndex("number")));
                itemList.add(item);
            }
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return itemList;
    }

    public boolean hasItemData() {
        openDatabase();
        Cursor cursor = db.query(TABLE_SOS_CONTACT, null, null, null, null,
                null, null);
        if (cursor == null) {
            return false;
        }

        while (cursor.moveToNext()) {
            return true;
        }
        return false;
    }

    private void onCreatSMSContentStingDb(SQLiteDatabase db){

        String sql = "create table "+TABLE_SMS_CONTENT+" (id text primary key,content text)";
        String smsContent="SOS Help!";

        if(mContext!=null)
        {
            smsContent=mContext.getResources().getString(R.string.str_default_sos_sms_content);
        }

        db.execSQL(sql);
        ContentValues value = new ContentValues();
        value.put("id", "0");
        value.put("content", smsContent);
        db.insert(TABLE_SMS_CONTENT, null, value);
    }

    public String getSMSContent(String index){
        String content = null;
        openDatabase();
        if (!db.isOpen()) {
            openDatabase();
        }
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_SMS_CONTENT, null, null, null, null, null,
                    null);
            if (cursor == null) {
                return null;
            }
//		itemList = new ArrayList<Item>();
            while (cursor.moveToNext()) {
                if(index.equals(cursor.getString(cursor.getColumnIndex("id"))))
                    content =  cursor.getString(cursor.getColumnIndex("content"));
            }
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase();
        }
        return content;
    }

    public void updateSting(String index,String content){
        openDatabase();
        if (!db.isOpen()) {
            openDatabase();
        }
        ContentValues values;
        try {
            if (content != null) {
                values = new ContentValues();
                values.put("id", index);
                values.put("content", content);
                db.update(TABLE_SMS_CONTENT, values, "id==" + index, null);
            }
        } catch (Exception e) {
        } finally {
            closeDatabase();
        }

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLE_SOS_CONTACT + "("
                + "id integer primary key autoincrement,name text , number text)";
        db.execSQL(sql);
        onCreatSMSContentStingDb(db);
        closeDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        String collectionSql = "drop table if exists " + TABLE_SOS_CONTACT;
        db.execSQL(collectionSql);
        onCreate(db);
    }

    static public boolean isSosEnable(Context context)
    {
        SharedPreferences preferences= context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        boolean bSos= preferences.getBoolean("bsos",false);//default sos value is true
        return true;
    }

    static public void setSosEnable(Context context,boolean sosEnable)
    {
        SharedPreferences preferences= context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean("bsos", sosEnable);
        editor.commit();
    }

}

