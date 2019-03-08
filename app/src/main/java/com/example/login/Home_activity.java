package com.example.login;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;


import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Home_activity extends AppCompatActivity{

    CardView card;
    CardView card1;
    CardView card2;
    private TextView gold, silver;
    Button parse;

    private ArrayList<String> students;

    //JSON Array
    private JSONArray result;

    String getGoldData, getSilverData;
    private static final String JSON_URL = "http://35.200.175.51/myRateVal/";
    private static final String JSONURL = "http://35.200.175.51/myRate/";
    RequestQueue requestQueue,req;
    private static final String TAG = MainActivity.class.getName();
    private static final String TAG1 = MainActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activity);

      requestQueue = Volley.newRequestQueue(this);
      req = Volley.newRequestQueue(this);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.view_Page);
        card = (CardView) findViewById(R.id.card1);
        card1 = (CardView) findViewById(R.id.card2);
        card2 = (CardView) findViewById(R.id.card3);
        ImageAdapter adapterView = new ImageAdapter(this);
//        parse =(Button)findViewById(R.id.data);
        gold = findViewById(R.id.gold);
        silver = findViewById(R.id.silvr);

        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();


        mViewPager.setAdapter(adapterView);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home_activity.this, Alert.class);
                startActivity(intent);
            }
        });
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home_activity.this, Alert.class);
                startActivity(intent);
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home_activity.this, Alert.class);
                startActivity(intent);
            }
        });

/*
        parse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonParse();
            }
        });
*/


        gold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),"Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen
                        requestQueue.stop();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG,"Error :" + error.toString());
                    }
                });

                requestQueue.add(stringRequest);
            }
        });


        silver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
                Network network = new BasicNetwork(new HurlStack());
                req = new RequestQueue(cache, network);
                req.start();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, JSONURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),"Silver :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen
                        req.stop();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG1,"Error :" + error.toString());
                    }
                });

                req.add(stringRequest);
            }
        });


    }

/*
    private  void jsonParse()
    {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, JSON_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{
                    JSONArray jsonArray =response.getJSONArray("description");

                    for (int i = 0; i<jsonArray.length();i++) {
                        JSONObject descreption= jsonArray.getJSONObject(i);

                        String goldRate = descreption.getString("b24k");
                        String silverRate = descreption.getString("bsilver");

                        gold.append(goldRate  );
                        silver.append(silverRate );
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(request);
    }
*/






}








