package com.example.coffee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddChallenge extends AppCompatActivity {

    private String sendUrl = "https://mypro222.000webhostapp.com/mycode/add_Challenge.php";

    //Url get all Coffee shop and put it in Spinner for chose one to add challenge to it
    private String getUrl="https://mypro222.000webhostapp.com/mycode/getOwnerCoffee.php";

    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";

    private ArrayList<HashMap<String, String>> mCommentSpinner;
    private JSONArray mJsonArrayDataSpn = null;

    private RequestQueue requestQueue;
    private String QHolder, AHolder,CoffeeHolder,O1Holder,O2Holder,O3Holder,userId;
    private Boolean CheckEditText;

    private EditText edtQ,edtA,edtO1,edtO2,edtO3;
    private Spinner spnCoffee;
    private Button btnAddChallenge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_challenge);

        requestQueue= Volley.newRequestQueue(getApplicationContext());
        mCommentSpinner = new ArrayList<HashMap<String, String>>();

        edtQ = findViewById(R.id.edtChallengeQ);
        edtA = findViewById(R.id.edtChallengeA);
        edtO1=findViewById(R.id.edtChallengeO1);
        edtO2=findViewById(R.id.edtChallengeO2);
        edtO3=findViewById(R.id.edtChallengeO3);

        SharedPreferences sh=getSharedPreferences("USER_LOGIN",MODE_PRIVATE);
         userId=sh.getString("id","");


        btnAddChallenge = findViewById(R.id.btnSaveChallenge);
        spnCoffee = findViewById(R.id.spnChoseCoffee);

        getDataToSpn();

        spnCoffee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = view.findViewById(R.id.txtGetCoffeeId);
                CoffeeHolder = textView.getText().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnAddChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckEditTextIsEmptyOrNot();
                if (CheckEditText)
                {
                    if (isNetworkAvailable())
                    {
                        newChallenge();
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
            }
        });
    }

   //ارسال البيانات الى السيرفر
    private void newChallenge() {
        StringRequest request=new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {//بعد اضافة البيانات الى الجدوال والعودة بناتج الاضافة هل تم الاضافة بنجاح
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");
                    if (success) {//اذا تم الاضافة بنجاح
                        Toast.makeText(getApplicationContext(), "SuccessFully Add Challenge", Toast.LENGTH_SHORT).show();
                        edtA.setText("");
                        edtQ.setText("");
                        edtO1.setText("");
                        edtO2.setText("");
                        edtO3.setText("");
                    } else {
                        Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Check Your Internet and Try Again !", Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "There is no internet connection\n Please  Check it and try again !", Toast.LENGTH_SHORT).show();

            }
        }){//ارسال البيانات اللازمة لاضافة Challenge
            public Map<String,String> getParams(){
                Map<String, String> params=new HashMap<String, String>();
                params.put("Q", QHolder);
                params.put("A",AHolder);
                params.put("Option1",O1Holder);
                params.put("Option2",O2Holder);
                params.put("Option3",O3Holder);
                params.put("coffee_name",CoffeeHolder);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000,1,1.0f));
        requestQueue.add(request);

    }

    //التاكد من ادخال البيانات في الواجهة validation
    public void CheckEditTextIsEmptyOrNot() {

        QHolder = edtQ.getText().toString();
        AHolder = edtA.getText().toString();
        O1Holder=edtO1.getText().toString();
        O2Holder=edtO2.getText().toString();
        O3Holder=edtO3.getText().toString();

        if (TextUtils.isEmpty(QHolder) || TextUtils.isEmpty(AHolder) ||TextUtils.isEmpty(O1Holder) ||TextUtils.isEmpty(O2Holder)
                ||TextUtils.isEmpty(O3Holder) ||TextUtils.isEmpty(CoffeeHolder)) {
            CheckEditText = false;
        } else {
            CheckEditText = true;
        }
    }

    //فحص هل التلفون متصل بالانترنت
    public boolean isNetworkAvailable() {
        boolean state;
        try {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (manager != null) {
                {
                    state = true;
                    networkInfo = manager.getActiveNetworkInfo();
                }
                return networkInfo != null && networkInfo.isConnected();
            } else {
                state = false;
            }
        } catch (NullPointerException e) {
            state = false;
        }
        return state;
    }

    //this 2 methods for fill spinner data from database
    private void getDataToSpn() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    mJsonArrayDataSpn = new JSONArray(response);
                    for (int i = 0; i < mJsonArrayDataSpn.length(); i++) {
                        JSONObject data = mJsonArrayDataSpn.getJSONObject(i);
                        String Id = data.getString(TAG_ID);
                        String Name = data.getString(TAG_NAME);

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(TAG_ID, Id);
                        map.put(TAG_NAME, Name);
                        mCommentSpinner.add(map);
                    }
                    updateSpinner();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"There is no internet connection.\n please  Check it and try again !",Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {//ارسال البيانات اللازمة لاضافة Challenge
            public Map<String,String> getParams(){
                Map<String, String> params=new HashMap<String, String>();
                params.put("id", userId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void updateSpinner() {
        SimpleAdapter adapter = new SimpleAdapter(this, mCommentSpinner,
                R.layout.spn_style_layout, new String[] { TAG_ID,TAG_NAME
        }, new int[] {  R.id.txtGetCoffeeId,
                R.id.txtCoffeeName});
        spnCoffee.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
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