package com.example.coffee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class addCoffee extends AppCompatActivity {

    private String sendUrl="https://mypro222.000webhostapp.com/mycode/add_coffee.php";
    private RequestQueue requestQueue;
    private String owner_id;

    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    Location_Tracker gps;

    Button btnAddCoffee;
    EditText edtName;
    EditText txtLatitude,txtLongitude;

    String latiString,longitString,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coffee);
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        btnAddCoffee=findViewById(R.id.btnAddCoffee);
        edtName=findViewById(R.id.editTextCoffeeName);
        txtLatitude=findViewById(R.id.TextLatitudeCoffee);
        txtLongitude=findViewById(R.id.TextLongitude);
        //get user Id
        SharedPreferences sh=getSharedPreferences("USER_LOGIN",MODE_PRIVATE);
        owner_id=sh.getString("id","");

        btnAddCoffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveCoffee();
            }
        });

        getLoction();
    }

    private void getLoction() {
        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // we will create a class object
        gps = new Location_Tracker(addCoffee.this);
        // Now we will check if GPS is enabled
        if (gps.canGetLocation()) {

            latiString = String.valueOf(gps.getLatitude());
            longitString = String.valueOf(gps.getLongitude());
            txtLongitude.setText(longitString);
            txtLatitude.setText(latiString);
        }
        else {
            gps.showSettings();
        }
    }

    private void SaveCoffee() {
        latiString=txtLatitude.getText().toString();
        longitString=txtLongitude.getText().toString();
        name=edtName.getText().toString();

        if(latiString.equals("") ||longitString.equals("") ||name.equals("")){
            Toast.makeText(getApplicationContext(),"Please Fill All fields!",Toast.LENGTH_LONG).show();
        return;
        }
        addCoffee();
    }

    private void addCoffee() {
        StringRequest request=new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {//بعد اضافة البيانات الى الجدوال والعودة بناتج الاضافة هل تم الاضافة بنجاح
                try {
                    JSONObject jObj = new JSONObject(response);
                   boolean success = jObj.getBoolean("success");
                    if (success) {//اذا تم الاضافة بنجاح
                        Toast.makeText(getApplicationContext(), "Add New Coffee SuccessFully", Toast.LENGTH_SHORT).show();
                        edtName.setText("");
                        txtLatitude.setText("");
                        txtLongitude.setText("");
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "There is no internet connection please  Check it and try again !", Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Please Check of Access to Internet", Toast.LENGTH_SHORT).show();

            }
        }){//ارسال البيانات اللازمة لاضافة user
            public Map<String,String> getParams(){
                Map<String, String> params=new HashMap<String, String>();
                params.put("name",name);
                params.put("latitude",latiString);
                params.put("longitude",longitString);
                params.put("owner",owner_id);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000,1,1.0f));
        requestQueue.add(request);


    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoction();
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
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}