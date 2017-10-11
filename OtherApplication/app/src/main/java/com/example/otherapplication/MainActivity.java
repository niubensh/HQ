package com.example.otherapplication;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.example.otherapplication.adapter.MyAdapter;
import com.example.otherapplication.bean.Student;
import com.example.otherapplication.observer.PersonOberserver;

import java.util.ArrayList;

public class MainActivity extends Activity implements OnClickListener {

	private ContentResolver contentResolver;
	private ListView lvShowInfo;
	private MyAdapter adapter;
	private Button btnInit;
	private Button btnInsert;
	private Button btnDelete;
	private Button btnUpdate;
	private Button btnQuery;
	private Cursor cursor;

	private static final String AUTHORITY = "com.wite.positionerwear.Provider";
    private static final Uri STUDENT_ALL_URI = Uri.parse("content://" + AUTHORITY + "/phone/#");
	protected static final String TAG = "MainActivity"; 
    
    private Handler handler=new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		cursor = contentResolver.query(STUDENT_ALL_URI, null, null, null,null);
    	    adapter.changeCursor(cursor);
    	};
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lvShowInfo=(ListView) findViewById(R.id.lv_show_info);
		initData();
	}

	private void initData() {
		btnInit=(Button) findViewById(R.id.btn_init);
		btnInsert=(Button) findViewById(R.id.btn_insert);
		btnDelete=(Button) findViewById(R.id.btn_delete);
		btnUpdate=(Button) findViewById(R.id.btn_update);
		btnQuery=(Button) findViewById(R.id.btn_query);
		
		btnInit.setOnClickListener(this);
		btnInsert.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
		btnUpdate.setOnClickListener(this);
		btnQuery.setOnClickListener(this);
		
		contentResolver = getContentResolver();
		//ע�����ݹ۲���
		contentResolver.registerContentObserver(STUDENT_ALL_URI,true,new PersonOberserver(handler));
		
		adapter=new MyAdapter(MainActivity.this,cursor);
		lvShowInfo.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		//��ʼ��
		case R.id.btn_init:
			ArrayList<Student> students = new ArrayList<Student>();  
	         
	        Student student1 = new Student("����ʦ",25,"һ�����ѧ�ĺ���ʦ");  
	        Student student2 = new Student("����",26,"��");  
	        Student student3 = new Student("����",27,"Ư��");  
	        Student student4 = new Student("��ܰ��",28,"��֪����ô����");  
	        Student student5 = new Student("������",29,"������");
	          
	        students.add(student1);  
	        students.add(student2);  
	        students.add(student3);  
	        students.add(student4); 
	        students.add(student5); 
	  
	        for (Student Student : students) {  
	            ContentValues values = new ContentValues();  
	            values.put("name", Student.getName());  
	            values.put("age", Student.getAge());  
	            values.put("introduce", Student.getIntroduce());  
	            contentResolver.insert(STUDENT_ALL_URI, values);  
	        }
			break;
			
		//��
		case R.id.btn_insert:
			
			Student student = new Student("С��", 26, "˧������");  
			
			//ʵ����һ��ContentValues����
	        ContentValues insertContentValues = new ContentValues();  
	        insertContentValues.put("name",student.getName());  
	        insertContentValues.put("age",student.getAge());  
	        insertContentValues.put("introduce",student.getIntroduce());  
	        
			//�����uri��ContentValues���󾭹�һϵ�д���֮��ᴫ��ContentProvider�е�insert�����У�
			//�������Զ����ContentProvider�н���ƥ�����
			contentResolver.insert(STUDENT_ALL_URI,insertContentValues);
			break;
			
		//ɾ
		case R.id.btn_delete:
			
	        //ɾ��������Ŀ
	        contentResolver.delete(STUDENT_ALL_URI, null, null); 
			//ɾ��_idΪ1�ļ�¼  
	        Uri delUri = ContentUris.withAppendedId(STUDENT_ALL_URI,1);  
	        contentResolver.delete(delUri, null, null);
			break;

		//��
		case R.id.btn_update:
			
			ContentValues contentValues = new ContentValues();
			contentValues.put("introduce","�Ը�");
			//�������ݣ���age=26����Ŀ��introduce����Ϊ"�Ը�"��ԭ��age=26��introduceΪ"��".
			//���ɵ�UriΪ��content://com.example.studentProvider/student/26
			Uri updateUri = ContentUris.withAppendedId(STUDENT_ALL_URI,26);
			contentResolver.update(updateUri,contentValues, null, null);
		
			break;

		//��
		case R.id.btn_query:
			Cursor cursor = contentResolver.query(STUDENT_ALL_URI, null, null, null,null);
			adapter=new MyAdapter(MainActivity.this,cursor);
			lvShowInfo.setAdapter(adapter);
			cursor = contentResolver.query(STUDENT_ALL_URI, null, "phonenum=?", new String[]{"4008308300"},null);
			if(cursor==null){
				Log.e(TAG, "-----------------查不到-------------------" );

			}else{
						Log.e(TAG, "-----------------查到了-------------------+"+cursor.getCount());



            while(cursor.moveToNext()){
				Log.e(TAG, "------------------" );
				Log.e(TAG, "数据是+"+	cursor.getString(cursor.getColumnIndex("name")) );
             	Log.e(TAG, "数据是+"+	cursor.getString(cursor.getColumnIndex("_id")) );
	            Log.e(TAG, "数据是+"+	cursor.getString(cursor.getColumnIndex("phonenum")) );
                   }


			}


			adapter.changeCursor(cursor);
			break;
		}
	}
}
