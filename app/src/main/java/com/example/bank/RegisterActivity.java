package com.example.bank;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextPhoneNumber, editTextName, editTextBalance, editTextEmail, editTextAccountNo, editTextIfscCode, editTextPassword, editTextVerifyPassword;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextName = findViewById(R.id.editTextName);
        editTextBalance = findViewById(R.id.editTextBalance);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextAccountNo = findViewById(R.id.editTextAccountNo);
        editTextIfscCode = findViewById(R.id.editTextIfscCode);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextVerifyPassword = findViewById(R.id.editTextVerifyPassword);
        Button buttonRegister = findViewById(R.id.buttonRegister);
        Button buttonCancel = findViewById(R.id.buttonCancel);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Register button click listener
        buttonRegister.setOnClickListener(v -> {
            String phoneNumber = editTextPhoneNumber.getText().toString().trim();
            String name = editTextName.getText().toString().trim();
            String balanceStr = editTextBalance.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String accountNo = editTextAccountNo.getText().toString().trim();
            String ifscCode = editTextIfscCode.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String verifyPassword = editTextVerifyPassword.getText().toString().trim();

            // Validate input
            if (phoneNumber.isEmpty() || name.isEmpty() || balanceStr.isEmpty() || email.isEmpty() || accountNo.isEmpty() || ifscCode.isEmpty() || password.isEmpty() || verifyPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(verifyPassword)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            double balance;
            try {
                balance = Double.parseDouble(balanceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(RegisterActivity.this, "Invalid balance amount", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add user to database
            boolean isInserted = dbHelper.insertUser(phoneNumber, name, balance, email, accountNo, ifscCode, password);
            if (isInserted) {
                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                finish(); // Close activity after registration
            } else {
                Toast.makeText(RegisterActivity.this, "Registration Failed: User with the same phone number or email already exists, or insert operation failed", Toast.LENGTH_LONG).show();
                Log.e("RegisterActivity", "Registration failed for email: " + email);
            }
        });

        // Cancel button click listener
        buttonCancel.setOnClickListener(v -> finish()); // Close activity
    }
}
