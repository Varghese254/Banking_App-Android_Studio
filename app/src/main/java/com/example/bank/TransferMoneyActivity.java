package com.example.bank;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class TransferMoneyActivity extends AppCompatActivity {
    private ListView userListView;
    private EditText amountEditText;
    private Button sendMoneyButton;
    private DatabaseHelper dbHelper;
    private String senderEmail;
    private String selectedReceiverAccountNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_money);

        userListView = findViewById(R.id.userListView);
        amountEditText = findViewById(R.id.amountEditText);
        sendMoneyButton = findViewById(R.id.sendMoneyButton);

        dbHelper = new DatabaseHelper(this);
        senderEmail = getIntent().getStringExtra("email");

        loadUsers();

        userListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            selectedReceiverAccountNo = selectedItem.split(" - ")[1];
        });

        sendMoneyButton.setOnClickListener(v -> transferMoney());
    }

    private void loadUsers() {
        List<String> userList = dbHelper.getAllUsersExcept(senderEmail);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        userListView.setAdapter(adapter);
    }

    private void transferMoney() {
        if (selectedReceiverAccountNo == null) {
            Toast.makeText(this, "Please select a recipient", Toast.LENGTH_SHORT).show();
            return;
        }

        String amountStr = amountEditText.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        if (dbHelper.transferMoney(senderEmail, selectedReceiverAccountNo, amount)) {
            Toast.makeText(this, "Transfer successful", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Transfer failed. Insufficient balance or invalid recipient.", Toast.LENGTH_SHORT).show();
        }

    }
}