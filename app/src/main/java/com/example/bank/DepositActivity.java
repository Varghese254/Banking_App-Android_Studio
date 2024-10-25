package com.example.bank;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DepositActivity extends AppCompatActivity {

    private EditText editTextAmount;
    private DatabaseHelper dbHelper;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        editTextAmount = findViewById(R.id.editTextAmount);
        Button btnConfirmDeposit = findViewById(R.id.btnConfirmDeposit);

        dbHelper = new DatabaseHelper(this);

        userEmail = getIntent().getStringExtra("email");

        btnConfirmDeposit.setOnClickListener(v -> depositCash());
    }

    private void depositCash() {
        String amountStr = editTextAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        if (amount <= 0) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isDeposited = dbHelper.depositCash(userEmail, amount);
        if (isDeposited) {
            Toast.makeText(this, "Deposit successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DepositActivity.this, ViewAccountActivity.class);
            intent.putExtra("email", userEmail);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Deposit failed", Toast.LENGTH_SHORT).show();
        }
    }
}