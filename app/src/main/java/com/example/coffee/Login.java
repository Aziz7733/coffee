package com.example.coffee;

import android.Manifest;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

public class Login extends AppCompatActivity {



    String sendUrl = "https://mypro222.000webhostapp.com/mycode/login.php";
    private RequestQueue requestQueue;

    EditText Email, Password;
    Button btnLog;
    TextView txtSignUp;
    String EmailHolder, PasswordHolder;
    Boolean CheckEditText;
    private String id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //
        requestQueue= Volley.newRequestQueue(getApplicationContext());

        Email = findViewById(R.id.edtLoginEmail);
        Password = findViewById(R.id.edtloginPassword);
        txtSignUp = findViewById(R.id.btnSignUp);
        btnLog =  findViewById(R.id.btnLogIn);


        btnLog.setOnClickListener(v -> {
            CheckEditTextIsEmptyOrNot();
            if (CheckEditText)
            {
                if (isNetworkAvailable())
                {
                    GetUserTypeFromUserTbl();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Don't Access To Internet !!!", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Please fill all form fields.", Toast.LENGTH_LONG).show();

            }
        });
        txtSignUp.setOnClickListener(v -> {
            Intent intent1 = new Intent(getApplicationContext(), Registration.class);
            startActivity(intent1);
        });
    }

    private void GetUserTypeFromUserTbl() {
        StringRequest request=new StringRequest(Request.Method.POST, sendUrl, response -> {//بعد اضافة البيانات الى الجدوال والعودة بناتج الاضافة هل تم الاضافة بنجاح
            try {
                JSONObject jObj = new JSONObject(response);
                boolean success = jObj.getBoolean("success");
                if (success) {//When user login successfully
                    id=jObj.getString("id");
                    Toast.makeText(getApplicationContext(), "SuccessFully Login", Toast.LENGTH_SHORT).show();
                    Email.setText("");
                    Password.setText("");
                    String type=jObj.getString("type");

                    SharedPreferences sh=getSharedPreferences("USER_LOGIN", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit=sh.edit();
                    myEdit.putString("name",jObj.getString("name"));
                    myEdit.putString("id",id);
                    myEdit.putString("type",type);

                    myEdit.commit();

                    if(type.equals("OwnerCoffee")){
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                    else{
                        startActivity(new Intent(getApplicationContext(),UserHome.class));
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid Email or password "+id, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "There is no internet connection \nPlease  Check it and try again !", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Pleas Check Your Intern and Try again", Toast.LENGTH_SHORT).show();

            }
        }){//ارسال البيانات اللازمة لتسجيل الدخول user
            public Map<String,String> getParams(){
                Map<String, String> params=new HashMap<String, String>();
                params.put("email",EmailHolder);
                params.put("password",PasswordHolder);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000,1,1.0f));
        requestQueue.add(request);

    }

    public void CheckEditTextIsEmptyOrNot() {

        EmailHolder = Email.getText().toString();
        PasswordHolder = Password.getText().toString();
        if (TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(PasswordHolder) ) {
            CheckEditText = false;
        } else {
            CheckEditText = true;
        }
    }
    //التحقق من وجود الانترنت
    public boolean isNetworkAvailable() {
        boolean state=false;
        try {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo =  manager.getActiveNetworkInfo();
            //معرفة ان الانترنت موجود بالفعل وايضا فخص حالة wifi
            if (networkInfo.isConnected() &&networkInfo !=null) {
                {
                    state = true;
                }
                return state;
            } else {
               return  state;
            }
        } catch (NullPointerException e) {
        }
        return state;
    }


    @Override
    protected void onStart() {
        super.onStart();
     //get session
        SharedPreferences sh=getSharedPreferences("USER_LOGIN",MODE_PRIVATE);
        String type=sh.getString("type","");

        if(type.equals(""))
            return;
        if(type.equals("OwnerCoffee")){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        else{
            startActivity(new Intent(getApplicationContext(),UserHome.class));
            finish();
        }



    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}