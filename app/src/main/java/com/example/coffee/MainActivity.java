package com.example.coffee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
Button btnAddCoffee,btnAddChallenge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAddCoffee=findViewById(R.id.btnNewCoffee);

        btnAddChallenge=findViewById(R.id.btnAddChallenge);
        btnAddCoffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),addCoffee.class));
                finish();
            }
        });
        btnAddChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AddChallenge.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.owner_menu, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }
}