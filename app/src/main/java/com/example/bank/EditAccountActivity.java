package com.example.bank;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditAccountActivity extends AppCompatActivity {

    private EditText editName, editPhoneNumber, editEmail, editAccountNo, editIfscCode;
    private DatabaseHelper dbHelper;
    private String originalEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        editName = findViewById(R.id.editName);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        editEmail = findViewById(R.id.editEmail);
        editAccountNo = findViewById(R.id.editAccountNo);
        editIfscCode = findViewById(R.id.editIfscCode);
        Button btnSave = findViewById(R.id.btnSave);

        dbHelper = new DatabaseHelper(this);

        // Get the email from the intent
        originalEmail = getIntent().getStringExtra("email");

        // Populate the fields with current user data
        populateUserData();

        btnSave.setOnClickListener(v -> saveUserData());
    }

    private void populateUserData() {
        Cursor cursor = dbHelper.getUserDetails(originalEmail);
        if (cursor != null && cursor.moveToFirst()) {
            editName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)));
            editPhoneNumber.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE_NUMBER)));
            editEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)));
            editAccountNo.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCOUNT_NO)));
            editIfscCode.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IFSC_CODE)));
            cursor.close();
        }
    }

    private void saveUserData() {
        String newName = editName.getText().toString().trim();
        String newPhoneNumber = editPhoneNumber.getText().toString().trim();
        String newEmail = editEmail.getText().toString().trim();
        String newAccountNo = editAccountNo.getText().toString().trim();
        String newIfscCode = editIfscCode.getText().toString().trim();

        boolean isUpdated = dbHelper.updateUserDetails(originalEmail, newName, newPhoneNumber, newEmail, newAccountNo, newIfscCode);

        if (isUpdated) {
            Toast.makeText(this, "User details updated successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditAccountActivity.this, ViewAccountActivity.class);
            intent.putExtra("email", newEmail);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to update user details", Toast.LENGTH_SHORT).show();
        }
    }
}