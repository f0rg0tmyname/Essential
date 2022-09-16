package com.android.retail.hawk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.collection.LLRBBlackValueNode;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OrderConfirmation extends AppCompatActivity {

    private LinearLayout contShopping;
    String ordId;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.WHITE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView date = findViewById(R.id.date_of_purchase);
        TextView amount = findViewById(R.id.amount);
        TextView payMethod = findViewById(R.id.pay_method);
        TextView amountText = findViewById(R.id.amount_text);
        TextView orderID = findViewById(R.id.order_id);
        contShopping = findViewById(R.id.continue_shopping);

        String method = getIntent().getStringExtra("METHOD");
        String totAmount = getIntent().getStringExtra("AMOUNT");
        ordId = getIntent().getStringExtra("ORDERID");
        String phoneNo = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(3);
        firestore = FirebaseFirestore.getInstance();

        if (method.equals("COD")){
            payMethod.setText("Cash on Delivery");
            amountText.setText("Amount to be paid");
        }else {
            payMethod.setText("Online");
            amountText.setText("Amount paid");
        }
        amount.setText(totAmount);

        String d = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        date.setText(d);

        orderID.setText("ORDER ID: "+ordId);

        contShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        sendSMSConfirmation(phoneNo);
    }

    private void sendSMSConfirmation(String phoneNumber){

        String smsAPI = "https://www.fast2sms.com/dev/bulkV2";
        String message = "Dear Customer, your order has been confirmed with ORDER ID: " + ordId + ". Your package is expected to be delivered within 1-2 working day(s)";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, smsAPI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OrderConfirmation.this, "SMS could not be sent", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", "uJAfnBoqPI5KjEXsCRUOS749QMvTaVhey20rDWGFcmY1zdpZwl63hNO4EKZgFVinLRC5WpaPYclybBGQ");
                return headers;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> body = new HashMap<>();
                body.put("sender_id","TXTIND");
                body.put("message", message);
                body.put("language", "english");
                body.put("route", "v3");
                body.put("numbers", phoneNumber);
                return body;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        RequestQueue requestQueue = Volley.newRequestQueue(OrderConfirmation.this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DeliveryActivity.getQtyIDs = false;
        for (int x = 0; x < DatabaseQueries.cartModelList.size() - 1; x++){

            for (String qtyID : DatabaseQueries.cartModelList.get(x).getQtyIDs()){
                firestore.collection("PRODUCTS").document(DatabaseQueries.cartModelList.get(x)
                        .getProductId()).collection("QUANTITY").document(qtyID)
                        .update("user_ID", FirebaseAuth.getInstance().getUid());
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}