package com.example.paymentgateway;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.payu.base.models.ErrorResponse;
import com.payu.base.models.PayUPaymentParams;
import com.payu.checkoutpro.PayUCheckoutPro;
import com.payu.checkoutpro.utils.PayUCheckoutProConstants;
import com.payu.ui.model.listeners.PayUCheckoutProListener;
import com.payu.ui.model.listeners.PayUHashGenerationListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements PaymentResultListener {

    private Button startpayment;
    private EditText orderamount, paymentType;
    private SessionManger manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = new SessionManger(getApplicationContext());
        startpayment = findViewById(R.id.startpayment);
        paymentType = findViewById(R.id.paymentType);
        orderamount = findViewById(R.id.orderamount);

        startpayment.setOnClickListener(view -> {
            manager.setPaymentType(paymentType.getText().toString());
            if (orderamount.getText().toString().equals("")) {
                Toast.makeText(MainActivity.this, "Amount is empty", Toast.LENGTH_LONG).show();
            } else {
                if (manager.getPaymentType().equals("1")) {
                    startPayment();
                }else {
                    payUMoney();
                }
            }
        });
    }

    private void startPayment() {
        final Checkout co = new Checkout();
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Shopping Jugaad");
            options.put("description", "App Payment");
            options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
            options.put("currency", "INR");

            String payment = orderamount.getText().toString();
            double total = Double.parseDouble(payment);
            total = total * 100;

            options.put("amount", total);
            JSONObject preFill = new JSONObject();
            preFill.put("email", "ritikkeshri@gmail.com");
            preFill.put("contact", "8401585664");
            options.put("prefill", preFill);
            //It is use to checkout or run the activity.
            co.open(this, options);
        } catch (Exception e) {
            Toast.makeText(this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment successfully done! " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        try {
            Toast.makeText(this, "Payment error please try again", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("OnPaymentError", "Exception in onPaymentError", e);
        }
    }

    private String testKey = "gtKFFX";
    private String testSalt = "eCwWELxi";
    private String prodKey = "0MQaQP";
    private String prodSalt = "13p0PXZk";

    //Key and salt correction else working fine
    public void payUMoney(){
        PayUPaymentParams.Builder builder = new PayUPaymentParams.Builder();
        builder.setAmount("50")
                .setIsProduction(true)
                .setProductInfo("Testing")
                .setKey(prodKey)
                .setPhone("8401585664")
                .setTransactionId(String.valueOf(System.currentTimeMillis()))
                .setFirstName("Ritik")
                .setEmail("ritikkeshri@gmail.com")
                .setSurl("https://payuresponse.firebaseapp.com/success")
                .setFurl("https://payuresponse.firebaseapp.com/failure");
        PayUPaymentParams payUPaymentParams = builder.build();

        PayUCheckoutPro.open(
                this,
                payUPaymentParams,
                new PayUCheckoutProListener() {

                    @Override
                    public void onPaymentSuccess(Object response) {
                        //Cast response object to HashMap
                        HashMap<String,Object> result = (HashMap<String, Object>) response;
                        String payuResponse = (String)result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                        String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
                    }

                    @Override
                    public void onPaymentFailure(Object response) {
                        //Cast response object to HashMap
                        Log.e("TAG", "onPaymentFailure: "+response.toString() );
                        HashMap<String,Object> result = (HashMap<String, Object>) response;
                        String payuResponse = (String)result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                        String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
                    }

                    @Override
                    public void onPaymentCancel(boolean isTxnInitiated) {
                        Log.e("TAG", "Cancel: " );
                    }

                    @Override
                    public void onError(ErrorResponse errorResponse) {
                        Log.e("TAG", "Error: "+errorResponse.getErrorMessage() );
                        String errorMessage = errorResponse.getErrorMessage();
                    }

                    @Override
                    public void setWebViewProperties(@Nullable WebView webView, @Nullable Object o) {
                        //For setting webview properties, if any. Check Customized Integration section for more details on this
                    }

                    @Override
                    public void generateHash(HashMap<String, String> valueMap, PayUHashGenerationListener hashGenerationListener) {
                        String hashName = valueMap.get(PayUCheckoutProConstants.CP_HASH_NAME);
                        String hashData = valueMap.get(PayUCheckoutProConstants.CP_HASH_STRING);
                        if (!TextUtils.isEmpty(hashName) && !TextUtils.isEmpty(hashData)) {
                            //Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.
                            String hash = HashGenrationUtils.genrateFromServer(hashData, testSalt,"8567219");
                            HashMap<String, String> dataMap = new HashMap<>();
                            dataMap.put(hashName, hash);
                            hashGenerationListener.onHashGenerated(dataMap);
                        }
                    }
                }
        );
    }
}