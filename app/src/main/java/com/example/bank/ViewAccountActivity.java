package com.example.bank;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewAccountActivity extends AppCompatActivity {

    private TextView txtBalance;
    private DatabaseHelper db;
    private String email;
    private Button btnTransactionHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);

        txtBalance = findViewById(R.id.txtBalance);
        db = new DatabaseHelper(this);

        // Initialize TextViews
        TextView txtName = findViewById(R.id.txtName);
        TextView txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        TextView txtEmail = findViewById(R.id.txtEmail);
        TextView txtAccountNo = findViewById(R.id.txtAccountNo);
        TextView txtIfscCode = findViewById(R.id.txtIfscCode);




        // Enable the back arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }



        // Get the email from the Intent
        Intent intent = getIntent();
        email = intent.getStringExtra("email");



        Button btnViewBalance = findViewById(R.id.btnViewBalance);
        btnViewBalance.setOnClickListener(v -> {
            // Fetch and display the balance
            if (email != null && !email.isEmpty()) {
                try (Cursor cursor = db.getUserDetails(email)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        txtBalance.setText("Balance: $" + cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BALANCE)));
                        txtBalance.setVisibility(TextView.VISIBLE); // Show the balance
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(v -> {
            Intent editIntent = new Intent(ViewAccountActivity.this, EditAccountActivity.class);
            editIntent.putExtra("email", email);
            startActivity(editIntent);
        });

        Button btnDeposit = findViewById(R.id.btnDeposit);
        btnDeposit.setOnClickListener(v -> {
            Intent depositIntent = new Intent(ViewAccountActivity.this, DepositActivity.class);
            depositIntent.putExtra("email", email);
            startActivity(depositIntent);
        });

        Button btnTransfer = findViewById(R.id.btnTransfer);
        btnTransfer.setOnClickListener(v -> {
            Intent transferIntent = new Intent(ViewAccountActivity.this, TransferMoneyActivity.class);
            transferIntent.putExtra("email", email);
            startActivityForResult(transferIntent, 1);
        });




        if (email != null && !email.isEmpty()) {
            // Retrieve user details from the database
            Cursor cursor = null;
            try {
                cursor = db.getUserDetails(email);
                if (cursor != null && cursor.moveToFirst()) {
                    // Set data to TextViews
                    txtName.setText("Name: " + cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)));
                    txtPhoneNumber.setText("Phone Number: " + cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE_NUMBER)));
                    txtEmail.setText("Email: " + cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)));
                    txtAccountNo.setText("Account No: " + cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCOUNT_NO)));
                    txtIfscCode.setText("IFSC Code: " + cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IFSC_CODE)));
                } else {
                    // Handle the case where no data is returned
                    setDefaultTextViews(txtName, txtPhoneNumber, txtEmail, txtAccountNo, txtIfscCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                setDefaultTextViews(txtName, txtPhoneNumber, txtEmail, txtAccountNo, txtIfscCode);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            // Handle the case where email is null or empty
            setDefaultTextViews(txtName, txtPhoneNumber, txtEmail, txtAccountNo, txtIfscCode);
        }
    }

    private void setDefaultTextViews(TextView txtName, TextView txtPhoneNumber, TextView txtEmail, TextView txtAccountNo, TextView txtIfscCode) {
        txtName.setText("Name: N/A");
        txtPhoneNumber.setText("Phone Number: N/A");
        txtEmail.setText("Email: N/A");
        txtAccountNo.setText("Account No: N/A");
        txtIfscCode.setText("IFSC Code: N/A");
    }
}
