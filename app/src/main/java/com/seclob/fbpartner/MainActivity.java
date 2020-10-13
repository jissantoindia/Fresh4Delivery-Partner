package com.seclob.fbpartner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lucifer.org.snackbartest.MySnack;

public class MainActivity extends AppCompatActivity {
    
    SharedPreferences sharedPreferences;
    String userName,upass,lId;
    LinearLayout LoaderLayout;
    TextView resName,resId,resAddress,resStatus;
    ImageView resImage;

    RecyclerView resultview;
    List<EnqModelSearch> enqModelSearches =new ArrayList<>();
    private EnqAdapterSearch enqAdapterSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        resId = findViewById(R.id.resId);
        resName = findViewById(R.id.resName);
        resImage = findViewById(R.id.resImage);
        resAddress = findViewById(R.id.resAddress);
        resStatus = findViewById(R.id.resStatus);

        resultview = findViewById(R.id.resultview);
        enqAdapterSearch = new EnqAdapterSearch(this);
        resultview.setAdapter(enqAdapterSearch);
        resultview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        sharedPreferences = getSharedPreferences("SECGRO",MODE_PRIVATE);
        userName=sharedPreferences.getString("uName","");
        upass=sharedPreferences.getString("uPass","");
        lId=sharedPreferences.getString("lId","");
        LoginApi();
        Orders();
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

                                Glide.with(MainActivity.this)
                                        .load(getString(R.string.asset_url)+personaldataObj.getString("restfrontimage")).placeholder(R.drawable.placeholder)
                                        .optionalFitCenter()
                                        .into(resImage);

                                resName.setText(personaldataObj.getString("restname"));
                                resId.setText("Restaurant ID: "+personaldataObj.getString("rest_id"));
                                resAddress.setText(personaldataObj.getString("restaddress"));
                                if (personaldataObj.getString("reststatus").equalsIgnoreCase("Open"))
                                {
                                    resStatus.setBackground(getDrawable(R.drawable.open));
                                    resStatus.setText(personaldataObj.getString("reststatus"));
                                }
                                else
                                {
                                    resStatus.setBackground(getDrawable(R.drawable.danger));
                                    resStatus.setText(personaldataObj.getString("reststatus"));
                                }


                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("uName", userName);
                                editor.putString("uPass", upass);
                                editor.putString("lId", dataObj.getString("login_id"));
                                editor.putString("rId", personaldataObj.getString("rest_id"));
                                editor.apply();



                            }
                            else
                            {
                                new MySnack.SnackBuilder(MainActivity.this)
                                        .setText(msg)
                                        .setTextColor("#ffffff")   //optional
                                        .setTextSize(14).setBgColor("#474747")
                                        .setDurationInSeconds(3).build();
                            }


                        }catch (Exception e){
                            Log.e("catcherror",e+"d");
                            new MySnack.SnackBuilder(MainActivity.this)
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
                            new MySnack.SnackBuilder(MainActivity.this)
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
                params.put("username",userName);
                params.put("password",upass);

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
        LoaderLayout = findViewById(R.id.main_loader);
        if(status)
            LoaderLayout.setVisibility(View.VISIBLE);
        else
            LoaderLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        Orders();
        super.onResume();
    }

    public void Logout(View view)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uName", "");
        editor.putString("uPass", "");
        editor.putString("lId", "");
        editor.putString("rId", "");
        editor.apply();

        Intent intents = new Intent(MainActivity.this, LoginActivity.class);
        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intents);
        finish();
    }

    public void Orders()
    {
        Loader(true);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"and_restaurant_order_list";
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

                                String orderlist    = Res.getString("orderlist");
                                JSONArray Results = new JSONArray(orderlist);
                                enqModelSearches.clear();
                                for(int i=0; i<Results.length(); i++) {
                                    String Result = Results.getString(i);
                                    JSONObject rst = new JSONObject(Result);

                                    EnqModelSearch enqModelSearch = new EnqModelSearch();
                                    enqModelSearch.setOid(rst.getString("id"));
                                    enqModelSearch.setOname(rst.getString("fname")+" "+rst.getString("lname"));
                                    enqModelSearch.setOmobile(rst.getString("phone"));
                                    enqModelSearch.setOsts(rst.getString("admin_status"));
                                    enqModelSearch.setOadsid(rst.getString("addressid"));
                                    enqModelSearches.add(enqModelSearch);

                                   // Log.e("SEARCH",rst.getString("type") + "-" + rst.getString("name"));
                                }
                                enqAdapterSearch.renewItems(enqModelSearches);
                                Log.e("ORDERLIST",orderlist);

                            }
                            else
                            {
                                new MySnack.SnackBuilder(MainActivity.this)
                                        .setText(msg)
                                        .setTextColor("#ffffff")   //optional
                                        .setTextSize(14).setBgColor("#474747")
                                        .setDurationInSeconds(3).build();
                            }


                        }catch (Exception e){
                            Log.e("catcherror",e+"d");
                            new MySnack.SnackBuilder(MainActivity.this)
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
                            new MySnack.SnackBuilder(MainActivity.this)
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
                params.put("login_id",lId);

                Log.e("Volley Parameters", params.toString());
                return params;
            }

        };

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);

    }

}