package com.example.webtech4;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class VolleySingleton {
    private static VolleySingleton instance;
    private RequestQueue requestQueue;
    private Context ctx;
    private static final String BASE_URL = "https://webtech-assign-3.wl.r.appspot.com/";

    private VolleySingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public void getData(String endpoint, final VolleyCallback callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, BASE_URL + endpoint, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle response
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        callback.onError(error.getMessage());
                    }
                });

        addToRequestQueue(jsonObjectRequest);
    }


    public void getData2(String endpoint, final VolleyCallback2 callback) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, BASE_URL + endpoint, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        // Handle response
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        callback.onError(error.getMessage());
                    }
                });

        addToRequestQueue(jsonArrayRequest);
    }

    public void postData(String endpoint, String ticker, final VolleyCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("symbol", ticker);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, BASE_URL + endpoint, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle response
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        callback.onError(error.getMessage());
                    }
                });

        addToRequestQueue(jsonObjectRequest);
    }

    public void postData2(String endpoint, JSONObject jsonBody2, final VolleyCallback callback) {

        Log.d("JSON body in POST adding item to portfolio", ""+jsonBody2);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, BASE_URL + endpoint, jsonBody2, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle response
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.d("ERROR in DELETE", ""+error);
                        callback.onError(error.getMessage());
                    }
                });
        addToRequestQueue(jsonObjectRequest);

    }

    public void sellStock(String endpoint, JSONObject jsonBody2, final VolleyCallback callback) {

        Log.d("JSON body in PUT sellling stock item to portfolio", ""+jsonBody2);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, BASE_URL + endpoint, jsonBody2, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle response
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.d("ERROR in DELETE", ""+error);
                        callback.onError(error.getMessage());
                    }
                });
        addToRequestQueue(jsonObjectRequest);

    }


    public interface VolleyCallback {
        void onSuccess(JSONObject response);
        void onError(String errorMessage);
    }

    public interface VolleyCallback2{
        void onSuccess(JSONArray response);
        void onError(String errorMessage);
    }
}
