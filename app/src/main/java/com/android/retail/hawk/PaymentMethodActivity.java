package com.android.retail.hawk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.android.retail.hawk.DatabaseQueries.cartModelList;
import static com.android.retail.hawk.DatabaseQueries.firebaseAuth;
import static com.android.retail.hawk.DatabaseQueries.firebaseFirestore;

@SuppressWarnings("SpellCheckingInspection")
public class PaymentMethodActivity extends AppCompatActivity {

    PaymentMethodActivity activity;
    final int requestCode = 2;
    String ORDER_ID;
    float value;
    String bodyData = "";

    private TextView totalAmount, orderID;
    private String transactionToken;
    private String merchantId;
    private LinearLayout payWithPaytmBtn, codBtn;
    private FirebaseFirestore firestore;
    private static boolean paymentSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        payWithPaytmBtn = findViewById(R.id.paytm_btn);
        codBtn = findViewById(R.id.cod_btn);

        ORDER_ID = "UP" + System.currentTimeMillis();

        totalAmount = findViewById(R.id.total_amount);
        orderID = findViewById(R.id.order_id);

        String total = getIntent().getStringExtra("TOTAL");
        totalAmount.setText("₹" + total);

        firestore = FirebaseFirestore.getInstance();

        payWithPaytmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeliveryActivity.getQtyIDs = false;
                startPayment();
            }
        });

        codBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeliveryActivity.getQtyIDs = false;
                paymentSuccess = true;
                orderID.setText("COD selected");
                startActivity(new Intent(getApplicationContext(), OrderConfirmation.class).putExtra("METHOD", "COD")
                        .putExtra("AMOUNT", totalAmount.getText())
                        .putExtra("ORDERID", ORDER_ID)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();

                Map<String, Object> updateCartList = new HashMap<>();

                long cartListSize = 0;
                List<Integer> indexList = new ArrayList<>();

                for (int x = 0; x < DatabaseQueries.cartList.size(); x++) {
                    if (!DatabaseQueries.cartModelList.get(x).isInStock()) {
                        updateCartList.put("product_ID_" + cartListSize, DatabaseQueries.cartModelList.get(x).getProductId());
                        cartListSize++;
                    } else {
                        indexList.add(x);
                    }
                }
                updateCartList.put("list_size", cartListSize);
                firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA").document("CART")
                        .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            for (int x = 0; x < indexList.size(); x++) {
                                DatabaseQueries.cartList.remove(indexList.get(x).intValue());
                                DatabaseQueries.cartModelList.remove(indexList.get(x).intValue());
                                DatabaseQueries.cartModelList.remove(DatabaseQueries.cartModelList.size() - 1);
                            }

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(PaymentMethodActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private void startPayment() {
        bodyData = getPaytmParams();

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.CHECKSUM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            try {
                                JSONObject paytmParams = new JSONObject();

                                JSONObject head = new JSONObject();

                                String checksum = new JSONObject(response).getString("checksum");
                                head.put("signature", checksum);

                                paytmParams.put("head", head);
                                paytmParams.put("body", new JSONObject(bodyData));

                                orderID.setText("Completing...");

                                String url = "https://securegw-stage.paytm.in/theia/api/v1/initiateTransaction?mid=" + Constants.MERCHANT_ID + "&orderId=" + ORDER_ID;

                                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if (response != null) {
                                            try {
                                                processPaytmTransaction(new JSONObject(response));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            } finally {
                                                ORDER_ID = "ID" + System.currentTimeMillis();
                                                orderID.setText(ORDER_ID);
                                            }
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(PaymentMethodActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PaymentMethodActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);

    }

    String getPaytmParams() {
        JSONObject paytmParams;
        try {
            JSONObject body = new JSONObject();
            body.put("requestType", "Payment");
            body.put("mid", Constants.MERCHANT_ID);
            body.put("websiteName", Constants.WEBSITE);
            body.put("orderId", ORDER_ID);
            body.put("callbackUrl", Constants.CALLBACK);

            JSONObject txnAmount = new JSONObject();
            try {
                value = Float.parseFloat(this.totalAmount.getText().toString());
            } catch (Exception e) {
                value = 0f;
            }
            txnAmount.put("value", String.format(Locale.getDefault(), "%.2f", value));
            txnAmount.put("currency", "INR");

            JSONObject userInfo = new JSONObject();
            userInfo.put("custId", FirebaseAuth.getInstance().getUid());

            body.put("txnAmount", txnAmount);
            body.put("userInfo", userInfo);

            paytmParams = body;
        } catch (Exception e) {
            paytmParams = new JSONObject();
        }
        return paytmParams.toString();
    }

    private void processPaytmTransaction(JSONObject data) {
        try {
            PaytmOrder paytmOrder = new PaytmOrder(ORDER_ID, Constants.MERCHANT_ID, data.getJSONObject("body").getString("txnToken"),
                    String.format(Locale.getDefault(), "%.2f", value), Constants.CALLBACK);
            TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
                @Override
                public void onTransactionResponse(@Nullable Bundle bundle) {
                    Toast.makeText(PaymentMethodActivity.this, "Payment Transaction response " + bundle.toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void networkNotAvailable() {
                    Toast.makeText(PaymentMethodActivity.this, "networkNotAvailable", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onErrorProceed(String s) {
                    Toast.makeText(PaymentMethodActivity.this, "onErrorProceed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void clientAuthenticationFailed(String s) {
                    Toast.makeText(PaymentMethodActivity.this, "clientAuthenticationFailed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void someUIErrorOccurred(String s) {
                    Toast.makeText(PaymentMethodActivity.this, "someUIErrorOccurred", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onErrorLoadingWebPage(int i, String s, String s1) {
                    Toast.makeText(PaymentMethodActivity.this, "onErrorLoadingWebPage", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onBackPressedCancelTransaction() {
                    Toast.makeText(PaymentMethodActivity.this, "onBackPressedCancelTransaction ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onTransactionCancel(String s, Bundle bundle) {
                    Toast.makeText(PaymentMethodActivity.this, "onTransactionCancel ", Toast.LENGTH_SHORT).show();
                }
            });
            transactionManager.startTransaction(this.activity, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.requestCode && data != null) {
            String nsdk = data.getStringExtra("nativeSdkForMerchantMessage");
            String response = data.getStringExtra("response");
            Toast.makeText(this, nsdk + response, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}