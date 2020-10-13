package com.seclob.fbpartner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import lucifer.org.snackbartest.MySnack;

public class LoginActivity extends AppCompatActivity {

    LinearLayout LoaderLayout;
    EditText Username,Password;
    String Uname,Upass;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        LoaderLayout = findViewById(R.id.login_laoder);
        Username = findViewById(R.id.login_user);
        Password = findViewById(R.id.login_pass);

        sharedPreferences = getSharedPreferences("SECGRO",MODE_PRIVATE);
    }

    public void LoginBtn(View view)
    {

        if(Username.length()>3 && Password.length()>3)
        {
            Uname = Username.getText().toString();
            Upass = Password.getText().toString();
            LoginApi();

        }else
        {
            new MySnack.SnackBuilder(LoginActivity.this)
                    .setText("Invalid Entry!")
                    .setTextColor("#ffffff")   //optional
                    .setTextSize(14).setBgColor("#474747")
                    .setDurationInSeconds(3).build();
        }

    }

    public void LoginApi()
    {
        Loader(true);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"login";
        StringRequest request = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Loader(false);

                        Log.e("Server Respone", response);

                        try {
                            JSONObject Res=new JSONObject(response);
                            String msg    = Res.getString("msg");
                            String sts    = Res.getString("sts");

                            if(sts.equalsIgnoreCase("01")) {

                                String data = Res.getString("data");
                                String personaldata = Res.getString("personaldata");
                                JSONObject dataObj = new JSONObject(data);
                                JSONObject personaldataObj = new JSONObject(personaldata);

                                FirebaseMessaging.getInstance().subscribeToTopic("res" + personaldataObj.getString("rest_id"));

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("uName", Uname);
                                editor.putString("uPass", Upass);
                                editor.putString("lId", dataObj.getString("login_id"));
                                editor.putString("rId", personaldataObj.getString("rest_id"));
                                editor.apply();

                                    Intent intents = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intents);
                                    finish();


                            }
                            else
                            {
                                new MySnack.SnackBuilder(LoginActivity.this)
                                        .setText(msg)
                                        .setTextColor("#ffffff")   //optional
                                        .setTextSize(14).setBgColor("#474747")
                                        .setDurationInSeconds(3).build();
                            }


                        }catch (Exception e){
                            Log.e("catcherror",e+"d");
                            new MySnack.SnackBuilder(LoginActivity.this)
                                    .setText("Something went wrong! Please try again...")
                                    .setTextColor("#ffffff")   //optional
                                    .setTextSize(14).setBgColor("#474747")
                                    .setDurationInSeconds(3).build();
                            Loader(false);
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if(response != null && response.data != null){
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            new MySnack.SnackBuilder(LoginActivity.this)
                                    .setText("Network Error! Please try again...")
                                    .setTextColor("#ffffff")   //optional
                                    .setTextSize(14).setBgColor("#474747")
                                    .setDurationInSeconds(3).build();
                            Loader(false);

                        }
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username",Uname);
                params.put("password",Upass);

                Log.e("Volley Parameters", params.toString());
                return params;
            }

        };

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);

    }


    void Loader(boolean status)
    {
        LoaderLayout = findViewById(R.id.login_laoder);
        if(status)
            LoaderLayout.setVisibility(View.VISIBLE);
        else
            LoaderLayout.setVisibility(View.GONE);
    }


}