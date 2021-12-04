package com.example.coffee;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CoffeeChallenge extends AppCompatActivity {

    private final String getUrl="https://mypro222.000webhostapp.com/mycode/get_challenge.php";

    boolean isHaveQuestions=false;
    String request_id="Coffee";
    private JSONArray mJsonArrayData = null;

    private RequestQueue requestQueue;

    private String name;
    private TextView txtName,txtQuestion,txtScore;
    RadioButton rdb1,rdb2,rdb3,rdb4;
    Button btnNext,btnQuit;

    RadioGroup radio_g;

    int flag=0;
    public static int marks=0,correct=0,wrong=0;
    String [] questions;
    String [] opt;
    String [] answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_challenge);

        Intent intent=getIntent();
        String coffee=intent.getStringExtra("coffee");
       if(!coffee.equals("")){
           request_id=coffee;
       }

        radio_g =findViewById(R.id.answersgrp);

        txtName = findViewById(R.id.txtNameCh);
        txtQuestion = findViewById(R.id.txtQuestion);
        txtScore = findViewById(R.id.txtScore);

        btnNext = findViewById(R.id.btnNext);
        btnQuit = findViewById(R.id.buttonquit);

        rdb1 = findViewById(R.id.radioButton);
        rdb2 = findViewById(R.id.radioButton2);
        rdb3 = findViewById(R.id.radioButton3);
        rdb4 = findViewById(R.id.radioButton4);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        btnQuit.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(),"You quit this challenge Please Try Again",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),UserHome.class));
            finish();

        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(isHaveQuestions)
                     takeChallenge();
                else
                    Toast.makeText(getApplicationContext(),"No Questions Founded For this Challenge",Toast.LENGTH_SHORT).show();
            }

        });

    }
//This method  finds the answers and compares them with the correct answers and adds the
// coupon to the local database if all the answers are correct
    private  void  takeChallenge(){
        try {

            if (radio_g.getCheckedRadioButtonId() == -1) {
                Toast.makeText(getApplicationContext(), "Please select one choice", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton uans =  findViewById(radio_g.getCheckedRadioButtonId());
            String ansText = uans.getText().toString();

            if (ansText.equals(answers[flag])) {
                correct++;
                Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
            } else {
                wrong++;
                Toast.makeText(getApplicationContext(), "Wrong", Toast.LENGTH_SHORT).show();
            }
            flag++;

            if (txtScore != null)
                txtScore.setText("" + correct);

            if (flag < questions.length) {
                txtQuestion.setText(questions[flag]);
                rdb1.setText(opt[flag * 4]);
                rdb2.setText(opt[flag * 4 + 1]);
                rdb3.setText(opt[flag * 4 + 2]);
                rdb4.setText(opt[flag * 4 + 3]);
            } else {
                marks = correct;

                if(wrong==0) {
                    Calendar calendar = Calendar.getInstance();
                    String take_coupons = String.valueOf(calendar.getTime());
                    take_coupons = take_coupons.substring(0, 20);
                    calendar.add(Calendar.HOUR, 6);
                    String endCoupons = String.valueOf(calendar.getTime());
                    MySQLiteHelper myDb = new MySQLiteHelper(getApplicationContext());
                    myDb.insertCoupons(request_id, take_coupons, endCoupons);
                    Intent in = new Intent(getApplicationContext(), UserCoupons.class);
                    startActivity(in);
                }
                else {
                    Toast.makeText(getApplicationContext(),"You Lost In this Challenge Please Try Again",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),UserHome.class));
                    finish();
                }
            }
            radio_g.clearCheck();
        }
        catch (Exception me){
            Toast.makeText(getApplicationContext(),"No Questions Founded ",Toast.LENGTH_LONG).show();

        }
    }

    //get questions from realtime database based coffee Notification
    private void getChallenge() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    mJsonArrayData = new JSONArray(response);
                    int arrayLen=mJsonArrayData.length();
                    if(arrayLen>0) {
                        isHaveQuestions=true;
                        questions = new String[arrayLen];
                        answers = new String[arrayLen];
                        opt = new String[arrayLen * 4];

                        for (int i = 0; i < mJsonArrayData.length(); i++) {
                            JSONObject data = mJsonArrayData.getJSONObject(i);
                            String Q = data.getString("question");
                            String A = data.getString("answer");
                            String op1 = data.getString("option1");
                            String op2 = data.getString("option2");
                            String op3 = data.getString("option3");
                            questions[i] = Q;//اضافة الاسئلة الموجودة في قاعدة البيانات الى مصفوفة
                            answers[i] = A;//اضافة الاجوبة لكل سوال الى مصفوفة
                            if (i % 2 == 0) {
                                opt[i * 4] = A;// مصفوفة تحتوي  الخيارات المتاحة
                                opt[i * 4 + 1] = op3;
                                opt[i * 4 + 2] = op2;
                                opt[i * 4 + 3] = op1;
                            } else if (i % 3 == 0) {
                                opt[i * 4] = op1;// مصفوفة تحتوي  الخيارات المتاحة
                                opt[i * 4 + 1] = A;
                                opt[i * 4 + 2] = op2;
                                opt[i * 4 + 3] = op3;
                            } else if (i % 4 == 0) {
                                opt[i * 4] = op1;// مصفوفة تحتوي  الخيارات المتاحة
                                opt[i * 4 + 1] = op2;
                                opt[i * 4 + 2] = A;
                                opt[i * 4 + 3] = op3;
                            } else {
                                opt[i * 4] = op2;// مصفوفة تحتوي  الخيارات المتاحة
                                opt[i * 4 + 1] = op1;
                                opt[i * 4 + 2] = op3;
                                opt[i * 4 + 3] = A;
                            }
                        }
                        txtQuestion.setText(questions[flag]);
                        rdb1.setText(opt[0]);
                        rdb2.setText(opt[1]);
                        rdb3.setText(opt[2]);
                        rdb4.setText(opt[3]);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"No Questions For this Coffee Found ",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"No Questions For this Coffee",Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();

            }

        }){//ارسال البيانات اللازمة لاضافة user
            public Map<String,String> getParams(){
                Map<String, String> params=new HashMap<String, String>();
                params.put("coffee",request_id);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,1,1.0f));
        requestQueue.add(stringRequest);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),UserHome.class));
        finish();
       return;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sh = getSharedPreferences("USER_LOGIN", MODE_PRIVATE);
        name = sh.getString("name", "");

        if(name.equals(""))
            txtName.setText("User Name");
        else
            txtName.setText(name);

        getChallenge();
    }
}