package com.example.otherapplication.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.otherapplication.R;

public class MyAdapter extends CursorAdapter {

	public MyAdapter(Context context, Cursor c) {
		super(context, c);
	}

	public MyAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	public MyAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	private Context mContext;

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		//�����ݿ��в�ѯ�����ֶ�
		String name = cursor.getString(cursor.getColumnIndex("name"));
		//�����ݿ��в�ѯ�����ֶ�
		String phoneNumber = cursor.getString(cursor.getColumnIndex("age"));
		//�����ݿ��в�ѯ"introduce"�ֶ�
	    String introduce = cursor.getString(cursor.getColumnIndex("introduce"));

	    viewHolder.tvName.setText(name);
	    viewHolder.tvAge.setText(phoneNumber);
	    viewHolder.tvIntroduce.setText(introduce);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		
		ViewHolder viewHolder = new ViewHolder();
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.item_information, null);

		viewHolder.tvName = (TextView) view.findViewById(R.id.tv_name_content);
		viewHolder.tvAge = (TextView) view.findViewById(R.id.tv_age_content);
		viewHolder.tvIntroduce = (TextView) view.findViewById(R.id.tv_introduce_content);
		view.setTag(viewHolder);
		return view;
	}

	class ViewHolder {
		TextView tvName;
		TextView tvAge;
		TextView tvIntroduce;
	}

}
