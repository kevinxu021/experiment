package com.lucky.Helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "lucky_lottery";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {

            SQLiteStatement stmt; // SQLiteStatement


            String sql01 = "create table lottery (lucky_no int primary key, red_val text not null,blue_val text not null, lucky_day timestamp)";
            db.execSQL(sql01); // ִ��SQL���,����Student��

            // stmt =
            // db.compileStatement("insert into lottery values (?, ?, ?)");
			/*
			 * ��compileStatement������װSQL���,�����ظ�����SQL���, ��?��Ϊ�������Խ����������������,���᷽ʽ��
			 * bindString�����ַ���,bindLong�������� �˴���forѭ��,һ��һ����������,��һ��һ��ִ��SQL���
			 */
            // for (String[] studentname : STUDENTS) {
            // stmt.bindString(1, studentname[0]);
            // stmt.bindString(2, studentname[1]);
            // stmt.acquireReference();
            // stmt.executeInsert();
            // }

            db.setTransactionSuccessful(); // ���ø÷��������������ݲ�����ʱ�ɽ����ݻع�
        } finally {
            db.endTransaction(); // �ر�����
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}