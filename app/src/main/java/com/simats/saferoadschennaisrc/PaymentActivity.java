package com.simats.saferoadschennaisrc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    private LinearLayout rowGPay, rowPhonePe, rowPaytm, rowAmazon, rowPaytmWallet;
    private ImageView checkGPay, checkPhonePe, checkPaytm;
    private List<LinearLayout> paymentRows = new ArrayList<>();
    private List<ImageView> checkIcons = new ArrayList<>();
    private String selectedMethod = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        ImageView btnClose = findViewById(R.id.btnClose);
        Button btnPay = findViewById(R.id.btnPay);

        // Initialize rows
        rowGPay = findViewById(R.id.rowGPay);
        rowPhonePe = findViewById(R.id.rowPhonePe);
        rowPaytm = findViewById(R.id.rowPaytm);
        rowAmazon = findViewById(R.id.rowAmazon);
        rowPaytmWallet = findViewById(R.id.rowPaytmWallet);

        // Initialize checks
        checkGPay = findViewById(R.id.checkGPay);
        checkPhonePe = findViewById(R.id.checkPhonePe);
        checkPaytm = findViewById(R.id.checkPaytm);

        paymentRows.add(rowGPay);
        paymentRows.add(rowPhonePe);
        paymentRows.add(rowPaytm);
        paymentRows.add(rowAmazon);
        paymentRows.add(rowPaytmWallet);

        checkIcons.add(checkGPay);
        checkIcons.add(checkPhonePe);
        checkIcons.add(checkPaytm);

        setupClickListeners();
        setupSpinner();
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMethod.isEmpty()) {
                    Toast.makeText(PaymentActivity.this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Simulate payment success
                Toast.makeText(PaymentActivity.this, "Payment Successful via " + selectedMethod, Toast.LENGTH_LONG).show();
                
                Intent intent = new Intent(PaymentActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupClickListeners() {
        rowGPay.setOnClickListener(v -> selectMethod(rowGPay, checkGPay, "Google Pay"));
        rowPhonePe.setOnClickListener(v -> selectMethod(rowPhonePe, checkPhonePe, "PhonePe"));
        rowPaytm.setOnClickListener(v -> selectMethod(rowPaytm, checkPaytm, "Paytm"));
        rowAmazon.setOnClickListener(v -> selectMethod(rowAmazon, null, "Amazon Pay"));
        rowPaytmWallet.setOnClickListener(v -> selectMethod(rowPaytmWallet, null, "Paytm Wallet"));
    }

    private void selectMethod(LinearLayout row, ImageView check, String methodName) {
        // Reset all
        for (LinearLayout r : paymentRows) {
            r.setSelected(false);
        }
        for (ImageView c : checkIcons) {
            if (c != null) c.setVisibility(View.INVISIBLE);
        }

        // Select current
        row.setSelected(true);
        if (check != null) check.setVisibility(View.VISIBLE);
        selectedMethod = methodName;
    }

    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.spinnerBank);
        String[] banks = {"Select Bank", "State Bank of India", "HDFC Bank", "ICICI Bank", "Axis Bank", "Canara Bank"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, banks);
        spinner.setAdapter(adapter);
    }
}
