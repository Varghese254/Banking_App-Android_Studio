package com.example.bank;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView welcomeTextView = findViewById(R.id.textViewWelcome);
        Button btnViewUsers = findViewById(R.id.btnViewUsers);

        Button btnLogout = findViewById(R.id.btnLogout);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        // Get the user's name from the database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getUserDetails(email);
        String username = "User";
        if (cursor != null && cursor.moveToFirst()) {
            username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
            cursor.close();
        }
        dbHelper.close();

        String welcomeMessage = "Welcome, " + username + "!";
        welcomeTextView.setText(welcomeMessage);

        btnViewUsers.setOnClickListener(v -> {
            Intent viewAccountIntent = new Intent(HomeActivity.this, ViewAccountActivity.class);
            viewAccountIntent.putExtra("email", email);
            startActivity(viewAccountIntent);
        });



        btnLogout.setOnClickListener(v -> {
            // Logs out the user and navigates to the Login page
            Intent logoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            finish();
        });
    }
}
