package com.lucky.load;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class DBUtil {
	public static final String INSERT_SQL = "insert into lottery values (?, ?, ?,?)";

	public static void main(String[] args) throws IOException {
		// initDB();
	}

	public static void initDB(SQLiteDatabase db, InputStream inputStream) throws IOException {
		System.out.println("initDB");
		InputStreamReader isr = new InputStreamReader(inputStream);
		BufferedReader br = new BufferedReader(isr);
		SQLiteStatement stmt = db.compileStatement(INSERT_SQL);
		// 15055 01 10 15 18 19 28 02 2015-05-14
		try {
			db.beginTransaction();
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#"))
					continue;
				insert(stmt, line);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			br.close();
			isr.close();
			inputStream.close();
			stmt.close();
		}
	}

	public static void insert(SQLiteStatement stmt, String line) {
		String head = line.replaceFirst("^([0-9]+)\\s+.*", "$1");
		String red = line.replaceFirst("^[0-9]+\\s+(.*)[0-9]{2}\\s+[0-9]{4}-[0-9]{2}-[0-9]{2}$",
				"$1");
		String blue = line.replaceFirst(".*([0-9]{2})\\s+[0-9]{4}-[0-9]{2}-[0-9]{2}$", "$1");
		String date = line.replaceFirst(".*([0-9]{4}-[0-9]{2}-[0-9]{2})$", "$1");
		stmt.bindString(1, head);
		stmt.bindString(2, red);
		stmt.bindString(3, blue);
		stmt.bindString(4, date);
		stmt.executeInsert();
	}

	public static int getlatestNo(SQLiteDatabase db) {
		Cursor rs = db.rawQuery("select * from lottery order by lucky_no desc limit 1", null);
		try {
			if (rs.moveToNext()) {
				int no = rs.getInt(0);
				return no;
			}
		} finally {
			rs.close();
		}
		return -1;
	}

}
