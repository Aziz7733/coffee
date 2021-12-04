package com.example.coffee;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class Registration extends AppCompatActivity {

    private String sendUrl = "https://mypro222.000webhostapp.com/mycode/user_oprations.php";
    private RequestQueue requestQueue;
    private final String checkEmailUrl = "https://mypro222.000webhostapp.com/mycode/check_email.php";
    private boolean isAvailable = false;
    Boolean CheckEditText;
    String name, email, password, phone, userType, confirmPassword;
    Button btnAddUser;
    EditText edtName, edtEmail, edtPassword, edtPhone, edtConfirmPassword;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        btnAddUser = findViewById(R.id.btnAddUser);
        edtEmail = findViewById(R.id.editTextUserEmail);
        edtName = findViewById(R.id.editTextUserName);
        edtPhone = findViewById(R.id.editTextUserMobile);
        edtPassword = findViewById(R.id.editTextUserPassword);
        edtConfirmPassword = findViewById(R.id.editTextUserConfirmPassword);
        spinner = (Spinner) findViewById(R.id.userType_spinner);

        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus==false && !v.equals("")){
                    email=edtEmail.getText().toString();
                    isAvailable=isEmailAvailable();
                }

            }
        });

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveUser();
            }
        });

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.UserType_Array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // An item was selected. You can retrieve the selected item using
                userType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    private void SaveUser() {
        CheckEditTextIsEmptyOrNot();
        if (CheckEditText) {
           if(!isAvailable){
               edtEmail.setError("Unavailable ");
               return;
           }
            if (!confirmPassword.equals(password)) {
                edtConfirmPassword.setError("Confirm Password Not Equal password");
                return;
            }
            if (isNetworkAvailable()) {
                addUser();
            } else {
                Toast.makeText(getApplicationContext(), "Don't Access To Internet !!!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please fill all form fields.", Toast.LENGTH_LONG).show();

        }
    }

    private void addUser() {
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {//بعد اضافة البيانات الى الجدوال والعودة بناتج الاضافة هل تم الاضافة بنجاح
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");
                    if (success) {//اذا تم الاضافة بنجاح
                        Toast.makeText(getApplicationContext(), "Registration Success", Toast.LENGTH_SHORT).show();
                        edtName.setText("");
                        edtEmail.setText("");
                        edtPassword.setText("");
                        edtPhone.setText("");
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed ", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "There is no internet connection \n Please  Check it and try again !", Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "please Check Your Internet and try Again", Toast.LENGTH_SHORT).show();

            }
        }) {//ارسال البيانات اللازمة لاضافة user
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("phone", phone);
                params.put("type", userType);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        requestQueue.add(request);


    }

    //check email is available to
    private boolean isEmailAvailable() {


        StringRequest request = new StringRequest(Request.Method.POST, checkEmailUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {//بعد اضافة البيانات الى الجدوال والعودة بناتج الاضافة هل تم الاضافة بنجاح
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");
                    if (success) {//is available
                        isAvailable = true;

                    } else {
                        edtEmail.setError("UnAvailable");
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "There is no internet connection \n Please  Check it and try again !", Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "please Check Your Internet and try Again", Toast.LENGTH_SHORT).show();

            }
        }) {
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        requestQueue.add(request);
        return isAvailable;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    //Validation Fields
    public void CheckEditTextIsEmptyOrNot() {
        name = edtName.getText().toString();
        password = edtPassword.getText().toString();
        email = edtEmail.getText().toString();
        phone = edtPhone.getText().toString();
        confirmPassword = edtConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(phone) || TextUtils.isEmpty(confirmPassword)) {
            CheckEditText = false;
        } else {
            CheckEditText = true;
        }
    }

    //التحقق من وجود الانترنت
    public boolean isNetworkAvailable() {
        boolean state = false;
        try {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            //معرفة ان الانترنت موجود بالفعل وايضا فخص حالة wifi
            if (networkInfo.isConnected() && networkInfo != null) {
                {
                    state = true;
                }
                return state;
            } else {
                return state;
            }
        } catch (NullPointerException e) {
        }
        return state;
    }

}