package com.seclob.fbpartner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class OrderActivity extends AppCompatActivity {

    LinearLayout LoaderLayout;
    TextView orderID,oderAmount,orderPaymentStatus,orderStatus,cusName,cusEmail,cusPhone,cusAddress1,cusAddress2;
    String orderid,addressid;
    Spinner Status;
    RecyclerView resultview;
    List<EnqModelSearch1> enqModelSearches =new ArrayList<>();
    private EnqAdapterSearch1 enqAdapterSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        orderID = findViewById(R.id.orderID);
        oderAmount = findViewById(R.id.oderAmount);
        orderPaymentStatus = findViewById(R.id.orderPaymentStatus);
        orderStatus = findViewById(R.id.orderStatus);
        cusName = findViewById(R.id.cusName);
        Status = findViewById(R.id.classSpinner);
        cusEmail = findViewById(R.id.cusEmail);
        cusPhone = findViewById(R.id.cusPhone);
        cusAddress1 = findViewById(R.id.cusAddress1);
        cusAddress2 = findViewById(R.id.cusAddress2);

        resultview = findViewById(R.id.FoodList);
        enqAdapterSearch = new EnqAdapterSearch1(this);
        resultview.setAdapter(enqAdapterSearch);
        resultview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));


        try {

            Intent intent = getIntent();
            orderid=intent.getStringExtra("oid");
            addressid=intent.getStringExtra("lid");
            LoginApi();
        }catch (Exception e)
        {
            Log.e("Error",e+"");
        }


        Status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(parent.getItemAtPosition(position).toString().equalsIgnoreCase("Change Status"))
                {}else
                {
                    changeStatus(parent.getItemAtPosition(position).toString());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return true;
    }

    public void LoginApi()
    {
        Loader(true);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"and_order_details_product";
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

                                String orderlist = Res.getString("orderlist");
                                JSONObject orderlistObj = new JSONObject(orderlist);

                                String deliveraddress = Res.getString("deliveraddress");
                                JSONObject deliveraddressObj = new JSONObject(deliveraddress);

                                orderID.setText("Order ID: "+orderlistObj.getString("id"));
                                oderAmount.setText("Amount: Rs."+orderlistObj.getString("amount"));
                                orderPaymentStatus.setText("Payment Status: "+orderlistObj.getString("status"));
                                orderStatus.setText("Order Status: "+orderlistObj.getString("admin_status"));


                                String foodlist    = Res.getString("orderitems");
                                JSONArray Results = new JSONArray(foodlist);
                                enqModelSearches.clear();
                                for(int i=0; i<Results.length(); i++) {
                                    String Result = Results.getString(i);
                                    JSONObject rst = new JSONObject(Result);
                                    String Status= "";
                                    EnqModelSearch1 enqModelSearch = new EnqModelSearch1();
                                    if(rst.getString("admin_status").equalsIgnoreCase("Cancelled"))
                                    {Status=" (Cancelled)";}
                                    enqModelSearch.setOid("Rs."+rst.getString("typeamount"));
                                    enqModelSearch.setOname(rst.getString("foodname")+"("+rst.getString("quantity")+")"+Status);
                                    enqModelSearch.setOsts(rst.getString("admin_status"));
                                    enqModelSearches.add(enqModelSearch);

                                    // Log.e("SEARCH",rst.getString("type") + "-" + rst.getString("name"));
                                }
                                enqAdapterSearch.renewItems(enqModelSearches);

                                cusName.setText("Name: "+deliveraddressObj.getString("name"));
                                cusEmail.setText("Email: "+deliveraddressObj.getString("email"));
                                cusPhone.setText("Phone: "+deliveraddressObj.getString("phone"));
                                cusAddress1.setText(deliveraddressObj.getString("address"));
                                cusAddress2.setText(deliveraddressObj.getString("districtname")+", "+deliveraddressObj.getString("statename"));


                            }
                            else
                            {
                                new MySnack.SnackBuilder(OrderActivity.this)
                                        .setText(msg)
                                        .setTextColor("#ffffff")   //optional
                                        .setTextSize(14).setBgColor("#474747")
                                        .setDurationInSeconds(3).build();
                            }


                        }catch (Exception e){
                            Log.e("catcherror",e+"d");
                            new MySnack.SnackBuilder(OrderActivity.this)
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
                            new MySnack.SnackBuilder(OrderActivity.this)
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
                params.put("orderid",orderid);
                params.put("addressid",addressid);
                params.put("type","food");

                Log.e("Volley Parameters", params.toString());
                return params;
            }

        };

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);

    }


    public void changeStatus(final String status)
    {
        Loader(true);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"and_oder_status_change";
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

                                LoginApi();
                                new MySnack.SnackBuilder(OrderActivity.this)
                                        .setText(msg)
                                        .setTextColor("#ffffff")   //optional
                                        .setTextSize(14).setBgColor("#474747")
                                        .setDurationInSeconds(3).build();

                            }
                            else
                            {
                                new MySnack.SnackBuilder(OrderActivity.this)
                                        .setText(msg)
                                        .setTextColor("#ffffff")   //optional
                                        .setTextSize(14).setBgColor("#474747")
                                        .setDurationInSeconds(3).build();
                            }


                        }catch (Exception e){
                            Log.e("catcherror",e+"d");
                            new MySnack.SnackBuilder(OrderActivity.this)
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
                            new MySnack.SnackBuilder(OrderActivity.this)
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
                params.put("orderid",orderid);
                params.put("orderstatus",status);

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
        LoaderLayout = findViewById(R.id.order_loader);
        if(status)
            LoaderLayout.setVisibility(View.VISIBLE);
        else
            LoaderLayout.setVisibility(View.GONE);
    }


}