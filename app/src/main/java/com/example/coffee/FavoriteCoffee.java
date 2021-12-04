package com.example.coffee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class FavoriteCoffee extends AppCompatActivity {

    MySQLiteHelper myDb;
    ListView ls;
    private ArrayList<HashMap<String, String>> mCommentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_coffee);
        myDb=new MySQLiteHelper(this);

        ls=findViewById(R.id.lsvFavorite);
        mCommentList= new ArrayList<HashMap<String, String>>();

        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {
                HashMap<String, String> row = (HashMap<String, String>) adapterview.getItemAtPosition(position);
                String Id = row.get("id");
                int favoriteDelete=Integer.parseInt(Id);
                //delete Your Favorite
                myDb.deleteFavorite(favoriteDelete);
                doLoadSQLiteFavorite();
            }
        });
    }

    //this method fill listView
    private void updateList() {

        ListAdapter adapter = new SimpleAdapter(this, mCommentList,
                R.layout.list_favorite, new String[]{"name", "id"},
                new int[]{R.id.txtCoffeeFavorite, R.id.txtCoffeeFavoriteId});
        ls.setAdapter(adapter);
    }

    //get data from SQLite
    private void doLoadSQLiteFavorite(){
        mCommentList.clear();
        Cursor cursor=myDb.fetchAllData("favorites");

        if (cursor.moveToFirst()){
            do {
            String id = cursor.getString(0);
            String name = cursor.getString(1);
            HashMap<String, String> map = new HashMap<String, String>();

            map.put("name", name);
            map.put("id", id);

            mCommentList.add(map);
        }while (cursor.moveToNext());
        }
        updateList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doLoadSQLiteFavorite();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
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