package com.wite.positionerwear.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Data;
import android.text.TextUtils;
import android.util.Log;

import com.wite.positionerwear.model.ContactData;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/10/12.
 */

public class ContactsAccessUtil {

    final static String TAG = "ContactsAccess";
    final static String PhoneAccountName = "Phone";
    final static String SIMAccountName = "SIM";

    // 读取联系人信息
    public static List<ContactData> getPhoneContacts(Context context, List<ContactData> list, boolean bSort) {
        if (list == null)
            list = new ArrayList<ContactData>();

        Cursor cursor = context.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                null,
                RawContacts.ACCOUNT_NAME + "=?",
                new String[]{"Phone"}, null);
        while (cursor.moveToNext()) {
            int indexId = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(indexId);
            int indexDisplayName = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            String name = cursor.getString(indexDisplayName);

            Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            while (phones.moveToNext()) {
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                ContactData data = new ContactData();
                data.setId(contactId);
                data.setContactName(name);
                data.setNumber(phoneNumber);
                list.add(data);
            }
            phones.close();
        }
        cursor.close();
        return list;
    }

    public static List<ContactData> getSIMContacts(Context context, List<ContactData> list, boolean bSort) {
        if (list == null)
            list = new ArrayList<ContactData>();
        //  CharacterParser characterParser = CharacterParser.getInstance();
        ContentResolver resolver = context.getContentResolver();
        // 获取Sims卡联系人
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, null, null, null, null);
        //"sort_key asc");

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                for (int i = 0; i < phoneCursor.getColumnCount(); i++) {
                    String columnName = phoneCursor.getColumnName(i);
                    String value = phoneCursor.getString(i);
                    Log.e("", "i: " + columnName + " value: " + value);
                }
                // 得到手机号码
//     String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //Phone._ID,
//       Phone.DISPLAY_NAME, Phone.NUMBER, "number"
                //FIXME 2.2 与 4.0 不一样。
                int numberIndex = phoneCursor.getColumnIndex(Phone.NUMBER);
                if (numberIndex == -1) {
                    numberIndex = phoneCursor.getColumnIndex("number"); // Android2.2
                }
                String phoneNumber = phoneCursor.getString(numberIndex);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber)) {
                    continue;
                }
                // 得到联系人名称
                int nameIndex = phoneCursor.getColumnIndex(Phone.DISPLAY_NAME);
                if (nameIndex == -1)
                    nameIndex = phoneCursor.getColumnIndex("name");// Android2.2
                String contactName = phoneCursor.getString(nameIndex);//phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                //Sim卡中没有联系人头像
                ContactData data = new ContactData();
                data.setId(phoneCursor.getString(phoneCursor.getColumnIndex(Phone._ID))); //(phoneCursor.getString(PHONES_ID_INDEX));
                data.setContactName(contactName);
                data.setNumber(phoneNumber);

                list.add(data);
            }

            phoneCursor.close();
        }

        return list;
    }

    public static long insertSIMContact(Context context, ContactData contact) {
        ContentValues values = new ContentValues();
        Uri uri = Uri.parse("content://icc/adn");
        values.clear();
        values.put("tag", contact.getContactName());
        values.put("number", contact.getNumber());
        ContentResolver resolver = context.getContentResolver();
//        Uri newSimContactUri = resolver.insert(uri, values); //Android4.0
        ///////////////////////////////////////////////////////
        //for android 2.2
        Uri newSimContactUri = null;
        try {
            newSimContactUri = resolver.insert(uri, values);
        } catch (Exception e) {
            e.printStackTrace();

//            values.clear();
////            values.put("name", contact.getContactName());
//            values.put("number", contact.getNumber());
//            newSimContactUri = resolver.insert(uri, values);
        }


        if (newSimContactUri != null) {
            long id = ContentUris.parseId(newSimContactUri);
            contact.setId(id + "");
            return id;
        } else
            return -1;
    }

    public static boolean updateSIMContact(Context context, ContactData oldContact, ContactData newContact) {
        Uri uri = Uri.parse("content://icc/adn");
        ContentValues values = new ContentValues();
        values.put("tag", oldContact.getContactName());
        values.put("number", oldContact.getNumber());
        values.put("newTag", newContact.getContactName());
        values.put("newNumber", newContact.getNumber());
        int rc = context.getContentResolver().update(uri, values, null, null);
        if (rc > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean insertPhoneContact(Context context, ContactData contact) {
        /**
         * 首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获取系统返回的rawContactId
         * 这时后面插入data表的依据，只有执行空值插入，才能使插入的联系人在通讯录里面可见
         */
        Uri rcUri;
        ContentValues values = new ContentValues();

//            ContentResolver resolver = context.getContentResolver();
        //首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获取系统返回的rawContactId
        values.put(RawContacts.ACCOUNT_NAME, PhoneAccountName);
        values.put(RawContacts.ACCOUNT_TYPE, "null");
        values.put(ContactsContract.Contacts.DISPLAY_NAME, contact.getContactName());
        Uri rawContactUri = context.getContentResolver().insert(RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        contact.setId(rawContactId + "");

        //往data表入姓名数据
        values.clear();
        values.put(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactId);
        values.put(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);//内容类型
        values.put(StructuredName.GIVEN_NAME, contact.getContactName());
        rcUri = context.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);

        if (rcUri != null) {
            //往data表入电话数据
            values.clear();
            values.put(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactId);
            values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
            values.put(Phone.NUMBER, contact.getNumber());
            values.put(Phone.TYPE, Phone.TYPE_WORK);
            rcUri = context.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);

        }

        return (rcUri != null);
    }

    public static boolean updatePhoneContact(Context context, ContactData contact) {

        ContentValues values = new ContentValues();

        values.clear();
        values.put(ContactsContract.Contacts.DISPLAY_NAME, contact.getContactName());
        int rc1 = context.getContentResolver().update(RawContacts.CONTENT_URI, values,
                ContactsContract.Contacts._ID + "=?", new String[]{contact.getId()});

        values.clear();
        values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
        values.put(Phone.NUMBER, contact.getNumber());
        int rc2 = context.getContentResolver().update(android.provider.ContactsContract.Data.CONTENT_URI, values,
                Data.RAW_CONTACT_ID + "=?", new String[]{contact.getId() + ""});
        return (rc1 > 0 || rc2 > 0) ? true : false;
    }

    /**
     * 根据contactId删除联系人数据
     */
    public static int deletePhoneContact(Context context, String name, String contactId) {
        ContentResolver resolver = context.getContentResolver();
        int rc1 = 0, rc2 = 0;
        //删除data表中数据
        String where = ContactsContract.Data.CONTACT_ID + " =?";
        String[] whereparams = new String[]{contactId};
        rc1 = resolver.delete(ContactsContract.Data.CONTENT_URI, where, whereparams);

        //删除rawContact表中数据
        where = ContactsContract.RawContacts.CONTACT_ID + " =?";
        whereparams = new String[]{contactId};
        rc2 = resolver.delete(ContactsContract.RawContacts.CONTENT_URI, where, whereparams);

        return (rc1 > 0 && rc2 > 0) ? (rc1 + rc2) : 0;
    }


    public static int deleteSIMContact(Context context, ContactData contact) throws Exception {
        Log.e("", "deleteSIMContact name:" + contact.getContactName() + " id:" + contact.getId());
        Uri uri = Uri.parse("content://icc/adn");
        ContentResolver resolver = context.getContentResolver();
        int rc = resolver.delete(uri, "tag=" + contact.getContactName() + " AND number=" + contact.getNumber(), null);
        Log.e("", "rc2 = " + rc);
        return rc;

    }








//批量删除
    public void clearContact(Context mContext ) {
        ContentResolver cr = mContext.getContentResolver();
        // 查询contacts表的所有记录
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);
        // 如果记录不为空
        if (cursor.getCount() > 0) {
            // 游标初始指向查询结果的第一条记录的上方，执行moveToNext函数会判断
            // 下一条记录是否存在，如果存在，指向下一条记录。否则，返回false。
            while (cursor.moveToNext()) {
                //  String rawContactId = "";
                // 从Contacts表当中取得ContactId
//                  String id = cursor.getString(cursor
//                          .getColumnIndex(ContactsContract.Contacts._ID));

                String name = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


                //根据姓名求id
                Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");

                Cursor cursor1 = cr.query(uri, new String[]{Data._ID},"display_name=?", new String[]{name}, null);
                if(cursor1.moveToFirst()){
                    int id = cursor1.getInt(0);
                    //根据id删除data中的相应数据
                    cr.delete(uri, "display_name=?", new String[]{name});
                    uri = Uri.parse("content://com.android.contacts/data");
                    cr.delete(uri, "raw_contact_id=?", new String[]{id+""});
                }
            }
        }
    }


}

