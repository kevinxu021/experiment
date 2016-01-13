package com.lucky.lottery;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lucky.Helper.DatabaseHelper;
import com.lucky.Helper.RecUtil;
import com.lucky.load.DBUtil;
import com.lucky.load.JSoupUtil;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity implements OnClickListener {
    private RecUtil recommands = new RecUtil();
    private static final String SETTING_PREF = "Lotter.setting.properties";
    private static final int COMPLETED = 0;
    AlertDialog ad = null;
    Handler handler = null;
            /**
             * The {@link android.support.v4.view.PagerAdapter} that will provide
             * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
             * derivative, which will keep every loaded fragment in memory. If this
             * becomes too memory intensive, it may be best to switch to a
             * {@link android.support.v4.app.FragmentStatePagerAdapter}.
             */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new DatabaseHelper(this);

        this.findViewById(R.id.main_current_val).setOnClickListener(this);
        this.findViewById(R.id.main_his_val).setOnClickListener(this);
        this.findViewById(R.id.main_rec_val).setOnClickListener(this);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == COMPLETED) {
                    MainActivity.this.updateLatest();
                    MainActivity.this.updateRecommands();
                    MainActivity.this.updateHistory();
                    ad.dismiss();
                }
            }
        };
        ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.loading);
        ad = new AlertDialog.Builder(this).setView(iv)
                .create();
    }

    private void updateLatest() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor rs = db.rawQuery("select * from lottery order by lucky_no desc limit 1", null);
        try {
            String str = null;
            if (rs.moveToNext()) {
                int lucky_no = rs.getInt(0);
                String red = rs.getString(1);
                String blue = rs.getString(2);
                String date = rs.getString(3);
                str = red + "<" + blue + "> \n" + date + " " + lucky_no;
            }
            TextView currVal = (TextView) this.findViewById(R.id.main_current_val);
            currVal.setText(str);
        } finally {
            rs.close();
            db.close();
        }
    }

    private void updateHistory() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor rs = db.rawQuery("select * from lottery order by lucky_no desc limit 30", null);
        try {
            String str = "";
            while (rs.moveToNext()) {
                int lucky_no = rs.getInt(0);
                String red = rs.getString(1);
                String blue = rs.getString(2);
                String date = rs.getString(3);
                str += red + " <" + blue + "> " + date + " " + lucky_no + "\n";
            }
            TextView currVal = (TextView) this.findViewById(R.id.main_his_val);
            currVal.setText(str);
        } finally {
            rs.close();
            db.close();
        }

    }

    private void updateRecommands() {
        Set<String> rs = this.recommands.gen(helper, 5);
        String vals = "";
        for (String val : rs) {
            vals += val + "\n";
        }
        TextView currVal = (TextView) this.findViewById(R.id.main_rec_val);
        currVal.setText(vals);

    }

    private boolean initialize(DatabaseHelper helper) throws IOException {
        SQLiteDatabase db = helper.getWritableDatabase();
        SharedPreferences settings = this.getSharedPreferences(SETTING_PREF, 0);
        boolean initFlag = settings.getBoolean("init_flag", false);
        if (!initFlag) {
            DBUtil.initDB(db);
            settings.edit().putBoolean("init_flag", true).commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh_history) {
            ad.show();
                new Thread(){
                    public void run() {
                        try {
                        MainActivity.this.initialize(helper);

                        JSoupUtil.refreshHistory(MainActivity.this.helper);
                            Message msg = new Message();
                            msg.what = COMPLETED;
                            handler.sendMessage(msg);
                        } catch (IOException e) {
                           e.printStackTrace();
                        }finally {

                        }
                    }
                }.start();


            return true;
        } else if (id == R.id.action_refresh) {
            updateRecommands();
            Toast.makeText(this, "Update successfully.", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_history) {
            updateHistory();
            Toast.makeText(this, "Update successfully.", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeDialog(DialogInterface di){
        di.dismiss();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class
            // below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.main_current_val) {
            this.updateLatest();
            this.updateHistory();
            Toast.makeText(this, "开奖信息已更新至最新！", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.main_rec_val) {
            this.updateRecommands();
            Toast.makeText(this, "已重新推荐五组号码！", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.main_his_val) {

        }
    }

}
