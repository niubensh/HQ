package com.example.contentproviderpractice;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class PeopleContentProvider extends ContentProvider {

	//这里的AUTHORITY就是我们在AndroidManifest.xml中配置的authorities，这里的authorities可以随便写
	private static final String AUTHORITY = "com.example.studentProvider";
	//匹配成功后的匹配码
	private static final int MATCH_ALL_CODE = 100;
	private static final int MATCH_ONE_CODE = 101;
	private static UriMatcher uriMatcher;
	private SQLiteDatabase db;
	private DBOpenHelper openHelper;
	private Cursor cursor = null;
	//数据改变后指定通知的Uri
    private static final Uri NOTIFY_URI = Uri.parse("content://" + AUTHORITY + "/student");

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
		uriMatcher.addURI(AUTHORITY, "student", MATCH_ALL_CODE);// 匹配记录集合
		uriMatcher.addURI(AUTHORITY, "student/#", MATCH_ONE_CODE);// 匹配单条记录
	}

	@Override
	public boolean onCreate() {

		openHelper = new DBOpenHelper(getContext());
		db = openHelper.getWritableDatabase();
		return false;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		switch (uriMatcher.match(uri)) {
		/**
		 * 这里如果匹配是uriMatcher.addURI(AUTHORITY, "student",
		 * MATCH_SUCCESS_CODE);中的Uri，则我们可以在这里对这个ContentProvider中的数据库
		 * 进行删除等操作。这里如果匹配成功，我们将删除所有的数据
		 */
		case MATCH_ALL_CODE:
			int count=db.delete("personData", null, null);
			if(count>0){
				notifyDataChanged();
				return count;
			}
			break;
		/**
		 * 这里如果匹配是uriMatcher.addURI(AUTHORITY,
		 * "student/#",MATCH_ONE_CODE);中的Uri，则说明我们要操作单条记录
		 */
		case MATCH_ONE_CODE:
			// 这里可以做删除单条数据的操作。
			break;
		default:
			throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
		}
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	/**
	 * 插入 使用UriMatch的实例中的match方法对传过来的 Uri进行匹配。 这里通过ContentResolver传过来一个Uri，
	 * 用这个传过来的Uri跟在ContentProvider中静态代码块中uriMatcher.addURI加入的Uri进行匹配
	 * 根据匹配的是否成功会返回相应的值，在上述静态代码块中调用uriMatcher.addURI(AUTHORITY,
	 * "student",MATCH_CODE)这里的MATCH_CODE
	 * 就是匹配成功的返回值，也就是说假如返回了MATCH_CODE就表示这个Uri匹配成功了
	 * ，我们就可以按照我们的需求就行操作了,这里uriMatcher.addURI(AUTHORITY,
	 * "person/data",MATCH_CODE)加入的Uri为：
	 * content://com.example.studentProvider/student
	 * ，如果传过来的Uri跟这个Uri能够匹配成功，就会按照我们设定的步骤去执行相应的操作
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {

		int match=uriMatcher.match(uri);
		if(match!=MATCH_ALL_CODE){
			throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
		}
		
		long rawId = db.insert("personData", null, values);
		Uri insertUri = ContentUris.withAppendedId(uri, rawId);
		if(rawId>0){
			notifyDataChanged();
			return insertUri;
		}
		return null;
		
	}

	/**
	 * 查询 如果uri为
	 * content://com.example.studentProvider/student则能匹配成功，然后我们可以按照需求执行匹配成功的操作
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		switch (uriMatcher.match(uri)) {
		/**
		 * 如果匹配成功，就根据条件查询数据并将查询出的cursor返回
		 */
		case MATCH_ALL_CODE:
			cursor = db.query("personData", null, null, null, null, null, null);
			break;
		case MATCH_ONE_CODE:
			// 根据条件查询一条数据。。。。
			break;
		default:
			throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
		}

		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		switch (uriMatcher.match(uri)) {
		case MATCH_ONE_CODE:
			long age = ContentUris.parseId(uri);
			selection = "age = ?";
			selectionArgs = new String[] { String.valueOf(age) };
			int count = db.update("personData", values, selection,selectionArgs);
			if(count>0){
				notifyDataChanged();
			}
			break;
		case MATCH_ALL_CODE:
			// 如果有需求的话，可以对整个表进行操作
			break;
		default:
			throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
		}
		return 0;
	}
	
	//通知指定URI数据已改变  
    private void notifyDataChanged() {  
        getContext().getContentResolver().notifyChange(NOTIFY_URI, null);         
    }
	
}
