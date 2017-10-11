package com.example.contentproviderpractice;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class PeopleContentProvider extends ContentProvider {

	//�����AUTHORITY����������AndroidManifest.xml�����õ�authorities�������authorities�������д
	private static final String AUTHORITY = "com.example.studentProvider";
	//ƥ��ɹ����ƥ����
	private static final int MATCH_ALL_CODE = 100;
	private static final int MATCH_ONE_CODE = 101;
	private static UriMatcher uriMatcher;
	private SQLiteDatabase db;
	private DBOpenHelper openHelper;
	private Cursor cursor = null;
	//���ݸı��ָ��֪ͨ��Uri
    private static final Uri NOTIFY_URI = Uri.parse("content://" + AUTHORITY + "/student");

	//�ھ�̬����������Ҫƥ��� Uri
	static {
		//ƥ�䲻�ɹ�����NO_MATCH(-1)
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		/**
		 * uriMatcher.addURI(authority, path, code); ����
		 * authority��������(����Ψһ��ʾһ��ContentProvider,�����Ҫ���嵥�ļ��е�authorities������ͬ)
		 * path:·��·��(����������ʾ����Ҫ���������ݣ�·���Ĺ���Ӧ����ҵ�����)
		 * code:����ֵ(����ƥ��uri��ʱ����Ϊƥ��ɹ��ķ���ֵ)
		 */
		uriMatcher.addURI(AUTHORITY, "student", MATCH_ALL_CODE);// ƥ���¼����
		uriMatcher.addURI(AUTHORITY, "student/#", MATCH_ONE_CODE);// ƥ�䵥����¼
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
		 * �������ƥ����uriMatcher.addURI(AUTHORITY, "student",
		 * MATCH_SUCCESS_CODE);�е�Uri�������ǿ�������������ContentProvider�е����ݿ�
		 * ����ɾ���Ȳ������������ƥ��ɹ������ǽ�ɾ�����е�����
		 */
		case MATCH_ALL_CODE:
			int count=db.delete("personData", null, null);
			if(count>0){
				notifyDataChanged();
				return count;
			}
			break;
		/**
		 * �������ƥ����uriMatcher.addURI(AUTHORITY,
		 * "student/#",MATCH_ONE_CODE);�е�Uri����˵������Ҫ����������¼
		 */
		case MATCH_ONE_CODE:
			// ���������ɾ���������ݵĲ�����
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
	 * ���� ʹ��UriMatch��ʵ���е�match�����Դ������� Uri����ƥ�䡣 ����ͨ��ContentResolver������һ��Uri��
	 * �������������Uri����ContentProvider�о�̬�������uriMatcher.addURI�����Uri����ƥ��
	 * ����ƥ����Ƿ�ɹ��᷵����Ӧ��ֵ����������̬������е���uriMatcher.addURI(AUTHORITY,
	 * "student",MATCH_CODE)�����MATCH_CODE
	 * ����ƥ��ɹ��ķ���ֵ��Ҳ����˵���緵����MATCH_CODE�ͱ�ʾ���Uriƥ��ɹ���
	 * �����ǾͿ��԰������ǵ�������в�����,����uriMatcher.addURI(AUTHORITY,
	 * "person/data",MATCH_CODE)�����UriΪ��
	 * content://com.example.studentProvider/student
	 * �������������Uri�����Uri�ܹ�ƥ��ɹ����ͻᰴ�������趨�Ĳ���ȥִ����Ӧ�Ĳ���
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
	 * ��ѯ ���uriΪ
	 * content://com.example.studentProvider/student����ƥ��ɹ���Ȼ�����ǿ��԰�������ִ��ƥ��ɹ��Ĳ���
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		switch (uriMatcher.match(uri)) {
		/**
		 * ���ƥ��ɹ����͸���������ѯ���ݲ�����ѯ����cursor����
		 */
		case MATCH_ALL_CODE:
			cursor = db.query("personData", null, null, null, null, null, null);
			break;
		case MATCH_ONE_CODE:
			// ����������ѯһ�����ݡ�������
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
			// ���������Ļ������Զ���������в���
			break;
		default:
			throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
		}
		return 0;
	}
	
	//ָ֪ͨ��URI�����Ѹı�  
    private void notifyDataChanged() {  
        getContext().getContentResolver().notifyChange(NOTIFY_URI, null);         
    }
	
}
