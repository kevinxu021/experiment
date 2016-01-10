package com.lucky.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RecUtil {
	private Random r = new Random();

	public Set<String> gen(DatabaseHelper helper, int rows) {
		Set<String> set = new HashSet<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		try {
			String latestRed = getLatestRed(db);
			while (set.size() < rows) {
				String red = genRed();
				if (!validateRed(red, set, latestRed))
					continue;
				String sql = "select count(1) from lottery where red_val='" + red + "'";
				Cursor rs = db.rawQuery(sql, null);
				int cnt = 0;
				if (rs.moveToNext()) {
					cnt = rs.getInt(0);
				}
				rs.close();
				if (cnt > 0) {
					continue;
				}
				// gen blue
				String blue = "";
				int blueInt = 0;
				sql = "select blue_val from lottery order by lucky_no desc limit 2";
				rs = db.rawQuery(sql, null);
				int a = 0;
				int b = 0;
				if (rs.moveToNext()) {
					a = rs.getInt(0);
				}
				if (rs.moveToNext()) {
					b = rs.getInt(0);
				}
				int abs = Math.abs(a - b);
				while (true) {
					blueInt = genBlue();
					if (abs < 10 && Math.abs(blueInt - a) < abs) {
						continue;
					} else if (blueInt == a) {
						continue;
					}
					break;
				}
				if (blueInt < 10) {
					blue += "0";
				}
				blue += blueInt;
				set.add(red + " <" + blue + ">");
				rs.close();
			}
		} finally {
			db.close();
		}
		return set;
	}

	private String getLatestRed(SQLiteDatabase db) {
		Cursor rs = db.rawQuery("select red_val from lottery order by lucky_no desc limit 1", null);
		try {
			if (rs.moveToNext())
				return rs.getString(0);
			else
				return "";
		} finally {
			rs.close();
		}
	}

	private boolean validateRed(String red, Set<String> set, String latestRed) {
		red = red.replaceAll("\\s+", "\\\\s*|\\\\s*");
		int length = latestRed.replaceAll(red, " ").replaceAll("\\s+", "").length();
		if (length <= 6) {
			return false;
		}

		length = 0;
		for (String line : set) {
			length = line.replaceAll(red, " ").replaceAll("\\s+", "").length();
			if (length <= 6) {
				return false;
			}
		}

		return true;
	}

	private String genRed() {
		List<Integer> reds = new ArrayList<Integer>();
		int next = 0;
		while (reds.size() < 6) {
			next = r.nextInt(33) + 1;
			if (!reds.contains(next)) {
				reds.add(next);
			}
		}

		Collections.sort(reds);
		String red = "";
		for (int val : reds) {
			if (val < 10) {
				red += 0;
			}
			red += val + " ";
		}
		red = red.trim();
		return red;
	}

	private int genBlue() {
		return r.nextInt(16) + 1;
	}

}
