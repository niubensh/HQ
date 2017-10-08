package com.wite.positionerwear.Provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.wite.positionerwear.DBHelper.DBHelper;

/**
 * Created by Administrator on 2017/9/27.
 */

public class PeopleContentProvider extends ContentProvider {


    //这里的AUTHORITY就是我们在AndroidManifest.xml中配置的authorities，这里的authorities可以随便写
    private static final String AUTHORITY = "com.wite.positionerwear.Provider";
    //匹配成功后的匹配码
    private static final int MATCH_ALL_CODE = 100;
    private static final int MATCH_ONE_CODE = 101;
    private static UriMatcher uriMatcher;
    private SQLiteDatabase db;
    private DBHelper mDbHelper;
    private Cursor cursor = null;
    //数据改变后指定通知的Uri
    private static final Uri NOTIFY_URI = Uri.parse("content://" + AUTHORITY + "/phone");


    //在静态代码块中添加要匹配的 Uri
    static {
        //匹配不成功返回NO_MATCH(-1)
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        /**
         * uriMatcher.addURI(authority, path, code); 其中
         * authority：主机名(用于唯一标示一个ContentProvider,这个需要和清单文件中的authorities属性相同)
         * path:路径路径(可以用来表示我们要操作的数据，路径的构建应根据业务而定)
         * code:返回值(用于匹配uri的时候，作为匹配成功的返回值)
         */
        uriMatcher.addURI(AUTHORITY, "phone", MATCH_ALL_CODE);// 匹配记录集合
        uriMatcher.addURI(AUTHORITY, "phone/#", MATCH_ONE_CODE);// 匹配单条记录
    }


    @Override
    public boolean onCreate() {
        mDbHelper=new DBHelper(getContext());
        db=mDbHelper.getWritableDatabase();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

     switch (uriMatcher.match(uri)){
         /**
          * 如果匹配成功，就根据条件查询数据并将查询出的cursor返回
          */

         case MATCH_ALL_CODE:
             cursor=mDbHelper.query();
             break;
         case MATCH_ONE_CODE:
             String[] a=new String[]{s};
             cursor=mDbHelper.queryfornumber(a);
             break;
         default:
             throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());

     }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
