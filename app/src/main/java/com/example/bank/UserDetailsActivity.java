package com.example.bank;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class UserDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        // Initialize views
        TextView tvUserName = findViewById(R.id.tvUserName);
        TextView tvUserEmail = findViewById(R.id.tvUserEmail);
        TextView tvAccountNumber = findViewById(R.id.tvAccountNumber);
        TextView tvBalance = findViewById(R.id.tvBalance);
        TextView tvIfscCode = findViewById(R.id.tvIfscCode);
        Button btnBack = findViewById(R.id.btnBack);

        // Get the email from the Intent
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        // Create a DatabaseHelper instance
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // Query the database for user details based on the email
        Cursor cursor = databaseHelper.getUserDetails(email);

        // Check if the cursor is not null and has at least one result
        if (cursor != null && cursor.moveToFirst()) {
            // Retrieve data from the cursor
            int nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME);
            int accountNoIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ACCOUNT_NO);
            int balanceIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_BALANCE);
            int ifscCodeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_IFSC_CODE);

            if (nameIndex != -1 && accountNoIndex != -1 && balanceIndex != -1 && ifscCodeIndex != -1) {
                String name = cursor.getString(nameIndex);
                String accountNumber = cursor.getString(accountNoIndex);
                double balance = cursor.getDouble(balanceIndex);
                String ifscCode = cursor.getString(ifscCodeIndex);

                // Set the data to the TextViews
                tvUserName.setText(name);
                tvUserEmail.setText(email);
                tvAccountNumber.setText(accountNumber);
                tvBalance.setText(String.format(Locale.getDefault(), "$%.2f", balance));
                tvIfscCode.setText(ifscCode);
            }
        }

        // Close the cursor to release resources
        if (cursor != null) {
            cursor.close();
        }

        // Set a click listener for the Back button
        btnBack.setOnClickListener(v -> finish());
    }
}
