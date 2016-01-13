package com.lucky.load;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.StrictMode;

import com.lucky.Helper.DatabaseHelper;

public class JSoupUtil {

	public static void refreshHistory(DatabaseHelper helper) throws IOException {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		SQLiteDatabase db = helper.getWritableDatabase();
		SQLiteStatement stmt = null;
		try {
			int no = DBUtil.getlatestNo(db);
			if (no == -1)
				return;
			stmt = db.compileStatement(DBUtil.INSERT_SQL);
			db.beginTransaction();
			insert(stmt, no);

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			if (stmt != null)
				stmt.close();
			db.close();
		}
	}

	public static void insert(SQLiteStatement stmt, int no) throws IOException {
		Document doc = Jsoup.connect(
				"http://datachart.500.com/ssq/history/newinc/history.php?end=" + no).get();
		Elements newsHeadlines = doc.select("#tdata tr");
		for (Element e : newsHeadlines) {
			String line = e.getAllElements().get(0).text();
			line = line.replaceFirst("(^([0-9]+\\s){8}).*([0-9]{4}-[0-9]+-[0-9]+)$", "$1 $3")
					.replaceAll("\\s+", " ");
			if (line.matches("^\\s*" + no + "\\s+.*"))
				break;
			DBUtil.insert(stmt, line);

		}

	}

}
