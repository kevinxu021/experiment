package com.lucky.Helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Handler;
import android.os.Message;

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
            db.execSQL(sql01); // 执行SQL语句,创建Student表

            // stmt =
            // db.compileStatement("insert into lottery values (?, ?, ?)");
			/*
			 * 用compileStatement方法封装SQL语句,可以重复编译SQL语句, 用?作为参数可以将后续数据连结进来,连结方式：
			 * bindString连结字符串,bindLong连接数据 此处用for循环,一行一行连接数据,再一行一行执行SQL语句
			 */
            // for (String[] studentname : STUDENTS) {
            // stmt.bindString(1, studentname[0]);
            // stmt.bindString(2, studentname[1]);
            // stmt.acquireReference();
            // stmt.executeInsert();
            // }

            db.setTransactionSuccessful(); // 调用该方法，当处理数据不完整时可将数据回滚
        } finally {
            db.endTransaction(); // 关闭事务
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}