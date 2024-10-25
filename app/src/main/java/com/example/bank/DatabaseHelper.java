package com.example.bank;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bank.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_USERS = "user_table";
    private static final String TABLE_TRANSACTIONS = "transactions";

    // Columns for user_table
    public static final String COLUMN_PHONE_NUMBER = "PHONENUMBER";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_BALANCE = "BALANCE";
    public static final String COLUMN_EMAIL = "EMAIL";
    public static final String COLUMN_ACCOUNT_NO = "ACCOUNT_NO";
    public static final String COLUMN_IFSC_CODE = "IFSC_CODE";
    public static final String COLUMN_PASSWORD = "PASSWORD";

    // Columns for transactions
    private static final String COLUMN_TRANSACTION_ID = "TRANSACTIONID";

    public static final String COLUMN_FROM_NAME = "FROMNAME";
    public static final String COLUMN_TO_NAME = "TONAME";
    public static final String COLUMN_AMOUNT = "AMOUNT";
    public static final String COLUMN_DATE = "DATE";
    public static final String COLUMN_STATUS = "STATUS";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_PHONE_NUMBER + " TEXT PRIMARY KEY, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_BALANCE + " REAL, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_ACCOUNT_NO + " TEXT, " +
                COLUMN_IFSC_CODE + " TEXT, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_FROM_NAME + " TEXT, " +
                COLUMN_TO_NAME + " TEXT, " +
                COLUMN_AMOUNT + " REAL, " +
                COLUMN_STATUS + " TEXT)";
        db.execSQL(CREATE_TRANSACTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }

    public boolean insertUser(String phoneNumber, String name, double balance, String email, String accountNo, String ifscCode, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE_NUMBER, phoneNumber);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_BALANCE, balance);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_ACCOUNT_NO, accountNo);
        values.put(COLUMN_IFSC_CODE, ifscCode);
        values.put(COLUMN_PASSWORD, password);

        long result = -1;
        try {
            result = db.insert(TABLE_USERS, null, values);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting user: " + e.getMessage());
        } finally {
            db.close();
        }

        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_PHONE_NUMBER};
        String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        return cursorCount > 0;
    }

    public Cursor getUserDetails(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?";
        return db.rawQuery(query, new String[]{email});
    }

    public boolean updateUserDetails(String originalEmail, String newName, String newPhoneNumber, String newEmail, String newAccountNo, String newIfscCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, newName);
        values.put(COLUMN_PHONE_NUMBER, newPhoneNumber);
        values.put(COLUMN_EMAIL, newEmail);
        values.put(COLUMN_ACCOUNT_NO, newAccountNo);
        values.put(COLUMN_IFSC_CODE, newIfscCode);

        int result = db.update(TABLE_USERS, values, COLUMN_EMAIL + " = ?", new String[]{originalEmail});
        db.close();
        return result > 0;
    }

    public boolean depositCash(String email, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String query = "SELECT " + COLUMN_BALANCE + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        if (cursor.moveToFirst()) {
            double currentBalance = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_BALANCE));
            double newBalance = currentBalance + amount;

            values.put(COLUMN_BALANCE, newBalance);

            int result = db.update(TABLE_USERS, values, COLUMN_EMAIL + " = ?", new String[]{email});
            cursor.close();
            db.close();
            return result > 0;
        }

        cursor.close();
        db.close();
        return false;
    }

    public List<String> getAllUsersExcept(String excludeEmail) {
        List<String> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_NAME + ", " + COLUMN_ACCOUNT_NO + ", " + COLUMN_IFSC_CODE +
                " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_EMAIL + " != ?";

        Cursor cursor = db.rawQuery(query, new String[]{excludeEmail});

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String accountNo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT_NO));
                String ifscCode = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IFSC_CODE));
                userList.add(name + " - " + accountNo + " - " + ifscCode);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }





    
    


    public boolean transferMoney(String senderEmail, String receiverAccountNo, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String query = "SELECT " + COLUMN_BALANCE + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?";
            Cursor senderCursor = db.rawQuery(query, new String[]{senderEmail});
            if (senderCursor.moveToFirst()) {
                double senderBalance = senderCursor.getDouble(senderCursor.getColumnIndexOrThrow(COLUMN_BALANCE));
                if (senderBalance >= amount) {
                    ContentValues senderValues = new ContentValues();
                    senderValues.put(COLUMN_BALANCE, senderBalance - amount);
                    db.update(TABLE_USERS, senderValues, COLUMN_EMAIL + " = ?", new String[]{senderEmail});

                    query = "SELECT " + COLUMN_BALANCE + " FROM " + TABLE_USERS + " WHERE " + COLUMN_ACCOUNT_NO + " = ?";
                    Cursor receiverCursor = db.rawQuery(query, new String[]{receiverAccountNo});
                    if (receiverCursor.moveToFirst()) {
                        double receiverBalance = receiverCursor.getDouble(receiverCursor.getColumnIndexOrThrow(COLUMN_BALANCE));
                        ContentValues receiverValues = new ContentValues();
                        receiverValues.put(COLUMN_BALANCE, receiverBalance + amount);
                        db.update(TABLE_USERS, receiverValues, COLUMN_ACCOUNT_NO + " = ?", new String[]{receiverAccountNo});

                        db.setTransactionSuccessful();
                        receiverCursor.close();
                        return true;
                    }
                    receiverCursor.close();
                }
            }
            senderCursor.close();
        } finally {
            db.endTransaction();
        }
        return false;
    }
}


