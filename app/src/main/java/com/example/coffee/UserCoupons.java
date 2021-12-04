package com.example.coffee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class UserCoupons extends AppCompatActivity {

    MySQLiteHelper myDb;
    ListView ls;
    private ArrayList<HashMap<String, String>> mCommentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_coupons);

        //Initializing variables
        myDb=new MySQLiteHelper(this);

        ls=findViewById(R.id.lsvCoupons);
        mCommentList= new ArrayList<HashMap<String, String>>();

    }
    //this method fill listView
    private void updateList() {

        ListAdapter adapter = new SimpleAdapter(this, mCommentList,
                R.layout.list_style_coupons, new String[]{"coffee", "endDate"},
                new int[]{R.id.txtUserCoffeeCoup, R.id.txtUserCoffeeCoupDate});
        ls.setAdapter(adapter);
    }

    //get data from SQLite
    private void doLoadCoupons(){
        mCommentList.clear();
        Cursor cursor=myDb.fetchAllData("coupons");

        if (cursor.moveToFirst()){
            do {
                String name = cursor.getString(1);
                String end = cursor.getString(3);
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("coffee", name);
                map.put("endDate","End Date:"+ end);

                mCommentList.add(map);
            }while (cursor.moveToNext());
        }
        updateList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),UserHome.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doLoadCoupons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setVisible(true);
        switch (item.getItemId()) {

            case R.id.action_logOut:
                SharedPreferences sh=getSharedPreferences("USER_LOGIN", MODE_PRIVATE);
                SharedPreferences.Editor myEdit=sh.edit();
                myEdit.clear();
                myEdit.apply();
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
                break;
            case R.id.action_Home:
                startActivity(new Intent(getApplicationContext(),UserHome.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDb.close();
    }
}