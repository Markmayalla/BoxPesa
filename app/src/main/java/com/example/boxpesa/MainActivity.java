package com.example.boxpesa;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    String mobile_money_holder;


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("XXXXXXXXXX_BR", "Intent: " + intent.toString());
            Bundle extras = intent.getExtras();
            Log.d("XXXXXXXXXX_BR", "Extras: " + extras);
            String registeredName = extras.getString("name");
            String activePhone = extras.getString("phone");
            Log.d("XXXXXXXXXX_BR", "Intent Detected: name = " + registeredName + ", phone = " + activePhone);
            dialNumber(registeredName, activePhone);

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLiteDatabase boxpesa = this.openOrCreateDatabase("boxpesa", MODE_PRIVATE, null);
        boxpesa.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY, " +
                "username VARCHAR," +
                "phone VARCHAR," +
                "mobile_money VARCHAR," +
                "password VARCHAR)");
        boxpesa.execSQL("CREATE TABLE IF NOT EXISTS savings (" +
                "id INTEGER PRIMARY KEY," +
                "user_id INTEGER(11)," +
                "date_in TEXT," +
                "date_out TEXT," +
                "amount VARCHAR," +
                "saving_type VARCHAR," +
                "extra VARCHAR," +
                "extra_plus VARCHAR)");
        boxpesa.execSQL("CREATE TABLE IF NOT EXISTS savings_pre (" +
                "id INTEGER PRIMARY KEY," +
                "user_id INTEGER(11)," +
                "date_in TEXT," +
                "date_out TEXT," +
                "amount VARCHAR," +
                "saving_type VARCHAR," +
                "status VARCHAR," +
                "extra VARCHAR," +
                "extra_plus VARCHAR)");
        boxpesa.execSQL("CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY," +
                "user_id INTEGER(11)," +
                "date_in TEXT," +
                "amount VARCHAR," +
                "serial VARCHAR," +
                "status VARCHAR," +
                "extra VARCHAR)");
        boxpesa.execSQL("CREATE TABLE IF NOT EXISTS saving_accounts (" +
                "id INTEGER PRIMARY KEY," +
                "user_id INTEGER(11)," +
                "account_name VARCHAR," +
                "date_created TEXT," +
                "date_expiry TEXT," +
                "saving_goal VARCHAR," +
                "extra VARCHAR)");
        boxpesa.execSQL("CREATE TABLE IF NOT EXISTS loans (" +
                "id INTEGER PRIMARY KEY," +
                "user_id INTEGER(11)," +
                "amount FLOAT," +
                "date_lend TEXT," +
                "date_pay TEXT," +
                "interest INTEGER(3)," +
                "extra VARCHAR)");



        String sql = "SELECT * FROM users WHERE phone = '0654303353'";
        Cursor c = boxpesa.rawQuery(sql, null);
        c.moveToFirst();
        if (c.getCount() < 1) {
            try {
                String sql_insert = "INSERT INTO users (username, phone, mobile_money, password) VALUES (" +
                        "'MARK MAYALLA'," +
                        "'0654303353'," +
                        "'tigopesa'," +
                        "'admin@123')";
                boxpesa.execSQL(sql_insert);

            } catch (Exception e) {
                Log.d("XXXXXXXXXX_reg", "Exception: " + e);
            }
        }

        String sql2 = "SELECT * FROM users WHERE phone = '0716006262'";
        Cursor c2 = boxpesa.rawQuery(sql, null);
        c2.moveToFirst();
        if (c2.getCount() < 1) {
            try {
                String sql_insert = "INSERT INTO users (username, phone, mobile_money, password) VALUES (" +
                        "'OTHMAN WAZIRI'," +
                        "'0716006262'," +
                        "'tigopesa'," +
                        "'1234')";
                boxpesa.execSQL(sql_insert);

            } catch (Exception e) {
                Log.d("XXXXXXXXXX_reg", "Exception: " + e);
            }
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECEIVE_SMS}, 2);
        } else {
            SmsReceiver.bindListner(new SmsListener() {
                @Override
                public void messageReceived(String messageText) {
                    msgReceiver(messageText);
                }
            });
        }



        registerReceiver(broadcastReceiver, new IntentFilter("REGISTERED_NAME"));
        EditText textView = (EditText) findViewById(R.id.login_phone);
        login(textView);
    }

    public int getActiveUser() {
        try {
            SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.boxpesa", Context.MODE_PRIVATE);
            int user_id = sharedPreferences.getInt("user_id",0);
            return user_id;
        } catch (Exception e) {

            Log.e("xxxxxxxxxxx", e.toString());
            return 0;
        }
    }

    public void sessionStart(int userID) {
        Toast.makeText(MainActivity.this, "Session Starting", Toast.LENGTH_LONG).show();
        try {
            SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.boxpesa", Context.MODE_PRIVATE);
            sharedPreferences.edit().putInt("user_id", userID).apply();
            Log.d("xxxxxxxxxx SESSION", "The user id is " + userID );
            Log.d("xxxxxxxxxx SESSION", "The user id is from sharedP: " + sharedPreferences.getInt("user_id",0));
        } catch (Exception e) {
            Log.e("xxxxxxxxxx SESSION", e.toString());
        }
    }

    public void msgReceiver(String msgText) {

        Log.i("XXXXXXXXXX MSG", msgText);

        Pattern p = Pattern.compile("Umetuma TSh (.*?) kwenda kwa (.*?) - (.*?)\\. Ada TSh (.*?)\\. VAT " +
                "TSh (.*?)\\. Kumbukumbu no\\.: (.*?)\\.(.*?)\\. Salio lako jipya ni TSh (.*?)\\. Asa");
        Matcher m = p.matcher(msgText);
        Log.i("XXXXXXXXXX P.Match", "Text Length" + msgText.length());
        try {
            if (m.find()) {
                Log.i("XXXXXXXXXX P.Match", "Match Found");
                String amount = m.group(1);
                String phone = m.group(2);
                String name = m.group(3);
                String fee = m.group(4);
                String vat = m.group(5);
                String serial = m.group(6);
                String dateTime = m.group(7);
                String balance = m.group(8);


                Log.i("XXXXXXXXXX P.Match", amount);
                Log.i("XXXXXXXXXX P.Match", phone);
                Log.i("XXXXXXXXXX P.Match", name);
                Log.i("XXXXXXXXXX P.Match", fee);
                Log.i("XXXXXXXXXX P.Match", vat);
                Log.i("XXXXXXXXXX P.Match", serial);
                Log.i("XXXXXXXXXX P.Match", dateTime);
                Log.i("XXXXXXXXXX P.Match", balance);
                Log.i("XXXXXXXXXX P.Match", "Calling validate payment");
                validatePayment(amount);

            } else {
                Log.i("XXXXXXXXXX P.Match", "No Match Found");
            }
        } catch (Exception e) {
            Log.e("XXXXXXXXXX P.Match", e.toString());
        }
    }

    public void register(View view) {
        setContentView(R.layout.register);
        EditText username = (EditText) findViewById(R.id.rUsername);
        EditText phone = (EditText) findViewById(R.id.rPhone);
        EditText password = (EditText) findViewById(R.id.rPassword);
        EditText passwordConfirm = (EditText) findViewById(R.id.rPasswordConfirm);
        Button registerButton = (Button) findViewById(R.id.registerButton);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText) findViewById(R.id.rUsername);
                EditText phone = (EditText) findViewById(R.id.rPhone);
                EditText password = (EditText) findViewById(R.id.rPassword);
                EditText passwordConfirm = (EditText) findViewById(R.id.rPasswordConfirm);
                Button registerButton = (Button) findViewById(R.id.registerButton);

                String passwordString = password.getText().toString();
                String passwordConfirmString = passwordConfirm.getText().toString();
                String phoneString = phone.getText().toString();
                String usernameString = username.getText().toString();

                if (passwordString.length() > 0 &&
                        passwordConfirmString.length() > 0 &&
                        phoneString.length() > 0 &&
                        usernameString.length() > 0) {
                    if (!passwordString.equals(passwordConfirmString)) {
                        passwordConfirm.setError("Passwords do not Match");
                    } else {
                        SQLiteDatabase boxpesa = openOrCreateDatabase("boxpesa", MODE_PRIVATE, null);
                        String sql = "SELECT * FROM users WHERE phone = '" + phoneString + "'";
                        Cursor c = boxpesa.rawQuery(sql, null);
                        c.moveToFirst();
                        if (c.getCount() < 1) {
                            try {
                                String sql_insert = "INSERT INTO users (username, phone, mobile_money, password) VALUES (" +
                                        "'" + usernameString + "'," +
                                        "'" + phoneString + "'," +
                                        "'" + mobile_money_holder + "'," +
                                        "'" + passwordString + "')";
                                boxpesa.execSQL(sql_insert);
                                Toast.makeText(getApplicationContext(),
                                        "Bravo, You are successfully registered", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Log.d("XXXXXXXXXX_reg", "Exception: " + e);
                            }
                        } else {
                            Log.d("XXXXXXXXXX_reg", "cursor: " + c);
                            Log.d("XXXXXXXXXX_reg", "cursor column count: " + c.getCount());
                            Log.d("XXXXXXXXXX_reg", "get count: " + c.getCount());
                            try {
                                int idIndex = c.getColumnIndex("id");
                                int usernameIndex = c.getColumnIndex("username");
                                int phoneIndex = c.getColumnIndex("phone");
                                int passwordIndex = c.getColumnIndex("password");
                                int mobileMoneyIndex = c.getColumnIndex("mobile_money");

                                Log.d("XXXXXXXXXX_reg", "username: " + c.getString(usernameIndex));
                                Log.d("XXXXXXXXXX_reg", "phone: " + c.getString(phoneIndex));
                                Log.d("XXXXXXXXXX_reg", "mobile_money: " + c.getString(mobileMoneyIndex));
                                Log.d("XXXXXXXXXX_reg", "password: " + c.getString(passwordIndex));
                            } catch (Exception e) {
                                Log.d("XXXXXXXXXX_reg", "Exception: " + e);
                            }
                            Toast.makeText(getApplicationContext(), "Phone number exist in existing users, Please Log in", Toast.LENGTH_LONG).show();
                        }
                    }

                }
            }
        });

        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    Log.d("FOCUS", "Phone Field Focus Out");
                    String tigopesa[] = {"0651","0655","0654", "0653", "0652", "0656","0657","0658","0659",
                            "0711","0712","0713","0714","0715","0716","0717","0718","0719",
                            "0671","0672","0673","0674","0675","0676","0677","0678","0679"};
                    String mpesa[] = {"0751", "0752", "0753", "0754", "0755", "0756","0757","0758","0759",
                                        "0761","0762","0763","0764","0765","0766","0767","0768","0769"};
                    String airtel[] = {"0781", "0782", "0783", "0784", "0785", "0786", "0787","0788","0789",
                                        "0681","0682","0683","0684","0685","0686","0687","0688","0689"};

                    List<String> tigoList = Arrays.asList(tigopesa);
                    List<String> mpesaList = Arrays.asList(mpesa);
                    List<String> airtelList = Arrays.asList(airtel);

                    EditText phoneNumber = (EditText) findViewById(R.id.rPhone);
                    String phoneNumberString = phoneNumber.getText().toString();
                    Log.d("FOCUS", "Phone number lenght " + phoneNumberString.length() );
                    if (phoneNumberString.length() > 0) {
                        if (phoneNumberString.length() != 10) {
                            phoneNumber.setError("Phone should have 10 numbers");
                        } else {
                            String firstNumber = phoneNumberString.substring(0, 1);
                            Log.d("FOCUS", "First Number = " + firstNumber);
                            String phoneCode = phoneNumberString.substring(0,4);
                            if (!firstNumber.equals("0")) {
                                phoneNumber.setError("Phone number should start with 0");
                            } else {
                                if (tigoList.contains(phoneCode)) {
                                    try {
                                        if (ContextCompat.checkSelfPermission(MainActivity.this,
                                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(MainActivity.this,
                                                    new String[]{Manifest.permission.CALL_PHONE}, 1);
                                        } else {
                                            String mtext = "Mobile Money: TigoPesa";
                                            mobile_money_holder = "tigo_pesa";
                                            Toast.makeText(getApplicationContext(), mtext, Toast.LENGTH_LONG).show();
                                            dialNumber("", "");
                                        }
                                    } catch (Exception e) {
                                        Log.d("XXXXXXXXXX REG", "ERROR: " + e);
                                    }
                                } else if (mpesaList.contains(phoneCode)) {
                                    String mtext = "Mobile Money: Mpesa";
                                    Toast.makeText(getApplicationContext(), mtext, Toast.LENGTH_LONG).show();
                                } else if (airtelList.contains(phoneCode)) {
                                    String mtext = "Mobile Money: Airtel Money";
                                    Toast.makeText(getApplicationContext(), mtext, Toast.LENGTH_LONG).show();
                                } else {
                                    phoneNumber.setError("Unknown MobileMoney");
                                }
                            }

                        }
                    } else {
                        phoneNumber.setError("Phone Number is Required");
                    }
                }
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    String mtext = "Mobile Money: TigoPesa";
                    mobile_money_holder = "tigo_pesa";
                    Toast.makeText(getApplicationContext(), mtext, Toast.LENGTH_LONG).show();
                    dialNumber("", "");

                } else {

                    Toast.makeText(getApplicationContext(), "Sorry, BoxPesa cant validate you", Toast.LENGTH_LONG).show();
                }
                return;
            }

            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsReceiver.bindListner(new SmsListener() {
                        @Override
                        public void messageReceived(String messageText) {
                            msgReceiver(messageText);
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG);
                }
            }

            case 3: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsReceiver.bindListner(new SmsListener() {
                        @Override
                        public void messageReceived(String messageText) {
                            EditText amount = (EditText) findViewById(R.id.direct_amount);
                            sendMoney(amount.getText().toString());
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG);
                }
            }

            case 4: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsReceiver.bindListner(new SmsListener() {
                        @Override
                        public void messageReceived(String messageText) {
                            EditText amount = (EditText) findViewById(R.id.direct_amount);
                            sendMoney(amount.getText().toString());
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG);
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    public void addPocket(View view) {
        EditText pocket = (EditText) findViewById(R.id.pocket);
        EditText pocket_goal = (EditText) findViewById(R.id.pocket_goal);
        EditText maturity_date = (EditText) findViewById(R.id.maturityDate);

        SQLiteDatabase boxpesa = this.openOrCreateDatabase("boxpesa", MODE_PRIVATE, null);

        if (pocket.getText().toString().length() > 0) {
            String pocketName = pocket.getText().toString();
            Boolean flag = true;

            if (Integer.valueOf(pocket_goal.getText().toString()) < 20000 && Integer.valueOf(pocket_goal.getText().toString()) > 3000000) {
                pocket.setError("The Pocket Goal can only be 20,000 to 3,000,000");
                flag = false;
            }

            if (flag == true) {
                int userID = getActiveUser();
                Log.d("xxxxxxxxxxx POCKETS", String.valueOf(userID));
                String sql = "SELECT * FROM users WHERE id = '" + userID + "'";
                Cursor c = boxpesa.rawQuery(sql, null);
                Log.d("xxxxxxxxxx POCKET", "Count: " + c.getCount());
                c.moveToFirst();
                int username = c.getColumnIndex("username");
                Log.d("xxxxxxxxxx POCKET", "Username: " + c.getString(username));
                Log.d("xxxxxxxxxx POCKET", "Count: " + c.getCount());
                if (c.getCount() > 0) {
                    String account_name = pocketName;
                    Date date = new Date();
                    SimpleDateFormat iso8601Format = new SimpleDateFormat("dd/mm/yyyy");
                    String dateCreated = iso8601Format.format(date);
                    String maturityDate = maturity_date.getText().toString();
                    String pocketGoal = pocket_goal.getText().toString();
                    String sql1 = "INSERT INTO saving_accounts " +
                            "(user_id, account_name, date_created, date_expiry, saving_goal) VALUES (" +
                            "'" + userID + "'," +
                            "'" + account_name + "'," +
                            "'" + dateCreated + "', " +
                            "'" + maturityDate + "', " +
                            "'" + pocketGoal + "')";
                    try {
                        boxpesa.execSQL(sql1);
                    } catch (Exception e) {
                        Log.e("xxxxxxxxxxxx POCKETS", e.toString());
                    }
                    Toast.makeText(MainActivity.this, "Pocket Added Successfully", Toast.LENGTH_LONG).show();



                } else {
                    pocket.setError("Permission Denied, Log in again");
                }
            }

        } else {
            pocket.setError("Field cannot be blank");
        }
    }

    public void addDirectSaving(View view) {
        EditText direct_amount = (EditText) findViewById(R.id.direct_amount);
        EditText direct_withdraw = (EditText) findViewById(R.id.direct_withdraw);

        SQLiteDatabase boxpesa = this.openOrCreateDatabase("boxpesa", MODE_PRIVATE, null);

        if (direct_amount.getText().toString().length() > 0) {
            int amount = Integer.valueOf(direct_amount.getText().toString());
            if (amount >= 2000 && amount <= 5000000) {
                if (direct_withdraw.getText().toString().length() > 0) {
                    try {
                        if (ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE}, 3);
                        } else {


                            SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
                            Date date = new Date();
                            String date_in = sdf.format(date);
                            String date_out = direct_withdraw.getText().toString();
                            String amount_direct = direct_amount.getText().toString();
                            String saving_type = "direct_saving";



                            String saving = "INSERT INTO savings " +
                                    "(user_id, date_in, date_out, amount, saving_type) VALUES (" +
                                    "'" + getActiveUser() + "'," +
                                    "'" + date_in + "', " +
                                    "'" + date_out + "', " +
                                    "'" + amount_direct + "', " +
                                    "'" + saving_type + "')";

                            boxpesa.execSQL(saving);

                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("DIRECT SAVING")
                                    .setMessage(amount_direct + " Has been successfully added to your BoxPesa A/C")
                                    .setNegativeButton("Ok", null)
                                    .show();
                            Log.d("xxxxxxxxx DIRECT", "Pre Saving Record Added Successfully");
                        }
                    } catch (Exception e) {
                        Log.d("XXXXXXXXXX REG", "ERROR: " + e);
                    }
                } else {
                    direct_withdraw.setError("Withdraw date cannot be empty");
                }
            } else {
                direct_amount.setError("Amount Out of Range, You can only save from 2,000 to 5,000,000");
            }
        } else {
            direct_amount.setError("Amount cannot be empty");
        }
    }

    private void validatePayment(String amount) {
        Log.i("XXXXXXXXXX P.Match", "Validate payment called");
        EditText direct_amount = (EditText) findViewById(R.id.direct_amount);
        Log.i("XXXXXXXXXX P.Match", "Getting form amount");
        String form_amount = direct_amount.getText().toString();

        String[] amountArray;
        String amount_sent = "";
        Log.i("XXXXXXXXXX P.Match", "Checking amount sent if exceeds 3");
        if (amount.length() > 3) {
            Log.i("XXXXXXXXXX P.Match", "Amount length exceeds 3, Apply array split for comma");
            amountArray = amount.split(",");
            Log.i("XXXXXXXXXX P.Match", "for lloping the array and concatinating the amount");
            for (int i=0; i< amountArray.length; i++) {
                amount_sent += amountArray[i];
            }
        } else {
            Log.i("XXXXXXXXXX P.Match", "Amount does not exceed 3");
            amount_sent = amount;
        }

        SQLiteDatabase boxpesa = this.openOrCreateDatabase("boxpesa", MODE_PRIVATE, null);
        String sql = "SELECT * FROM savings_pre WHERE status = 'pending' AND user_id = '" + getActiveUser() + "'" ;
        Cursor c = boxpesa.rawQuery(sql, null);
        if (c.moveToFirst()) {
            Log.i("XXXXXXXXXX $-VALIDATION", "Comparing amount sent to amount promiced");
            do {
                int idIndex = c.getColumnIndex("id");
                int amountIndex = c.getColumnIndex("amount");
                int date_inIndex = c.getColumnIndex("date_in");
                int date_outIndex = c.getColumnIndex("date_out");
                int saving_typeIndex = c.getColumnIndex("saving_type");
                int extraIndex = c.getColumnIndex("extra");

                String id_pre = c.getString(idIndex);
                String amount_pre = c.getString(amountIndex);
                String date_inPre = c.getString(date_inIndex);
                String date_outPre = c.getString(date_outIndex);
                String saving_typePre = c.getString(saving_typeIndex);
                String extra_pre = c.getString(extraIndex);
                if (amount_sent.equals(amount_pre)) {
                    //Add to Saving
                    add_saving(id_pre, amount_pre, date_inPre, date_outPre, saving_typePre, extra_pre);
                } else {
                    //Add to Wallet
                }
            } while (c.moveToNext());
        }



    }

    public void add_saving(String id, String amount, String date_in, String date_out, String saving_type, String extra) {
        String tag = "xxxxxxxxxx ADD_SAVING";
        SQLiteDatabase boxpesa = this.openOrCreateDatabase("boxpesa", MODE_PRIVATE, null);
        String sql = "UPDATE savings_pre SET status = 'successfull' WHERE id = " + Integer.valueOf(id) + "";
        boxpesa.execSQL(sql);
        Log.d(tag, "Add Saving Triggered");

        if (saving_type.equals("direct_saving")) {
            Log.d(tag, "Adding direct Saving");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
            Date date = new Date();
            String date_in_new = sdf.format(date);
            String sql2 = "INSERT INTO savings (user_id, date_in, date_out, amount, saving_type) VALUES (" +
                    "'" + getActiveUser() + ", " +
                    "'" + date_in_new + "', " +
                    "'" + date_out + "', " +
                    "'" + amount + "', " +
                    "'" + saving_type + "')";

            boxpesa.execSQL(sql2);
            Toast.makeText(MainActivity.this, "Direct Saving Added Successfully", Toast.LENGTH_LONG).show();
        }

        if (saving_type.equals("saving_pockets")) {
            Log.d(tag, "Adding Saving Pockets");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
            Date date = new Date();
            String date_in_new = sdf.format(date);
            String sql2 = "INSERT INTO savings (user_id, date_in, date_out, amount, saving_type, extra) VALUES (" +
                    "'" + getActiveUser() + ", " +
                    "'" + date_in_new + "', " +
                    "'" + date_out + "', " +
                    "'" + amount + "', " +
                    "'" + saving_type + "', " +
                    "'" + extra + "')";

            boxpesa.execSQL(sql2);
            Toast.makeText(MainActivity.this, "Direct Saving Added Successfully", Toast.LENGTH_LONG).show();
        }

    }

    public void dialNumber(String name, String phone) {
        if (name == "") {
            Intent ussd = new Intent(this, com.example.boxpesa.ussd.class);
            startService(ussd);
            String code = "106*02";
            String ussdCode = "*" + code + Uri.encode("#");

            startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCode)));
        } else {
            TextView usernameValid = (TextView) findViewById(R.id.usernameValid);
            TextView phoneValid = (TextView) findViewById(R.id.phoneValid);
            EditText username = (EditText) findViewById(R.id.rUsername);
            EditText phoneNumber = (EditText) findViewById(R.id.rPhone);
            String usernameString = username.getText().toString();
            String phoneNumberString = phoneNumber.getText().toString();
            usernameString = usernameString.toUpperCase();
            Log.d("XXXXXXXXXX_D","Username: " + usernameString);
            Log.d("XXXXXXXXXX_D", "TCRA suggests: " + name);
            Log.d("XXXXXXXXXX_D", "Phone: " + phoneNumberString);
            Log.d("XXXXXXXXXX_D", "Detected SIM: " + phone);
            if (usernameString.equals(name)) {
                String valid = "Valid Username: " + name;
                usernameValid.setText(valid);
            } else {
                username.setError("Invalid Username, TCRA suggests " + name);
            }
            if (phoneNumberString.equals(phone)) {
                String validPhone = "Valid Phone No: " + phone;
                phoneValid.setText(validPhone);
            } else {
                phoneNumber.setError("Invalid Phone No, Detected SIM is " + phone);
            }

            //Prompt alert to turn of ussd service
        }

    }

    public void login(View view) {

        setContentView(R.layout.login);

        Button login_button = (Button) findViewById(R.id.loginButton);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("XXXXXXXXXX login", "Login Button Clicked");
                EditText phone = (EditText) findViewById(R.id.login_phone);
                EditText password = (EditText) findViewById(R.id.login_password);
                SQLiteDatabase boxpesa = openOrCreateDatabase("boxpesa", MODE_PRIVATE, null);
                String sql = "SELECT * FROM users WHERE phone = '" + phone.getText().toString() + "'";
                Cursor c = boxpesa.rawQuery(sql, null);
                c.moveToFirst();
                if (c.getCount() > 0) {
                    int passwordIndex = c.getColumnIndex("password");
                    int userIdIndex = c.getColumnIndex("id");
                    String passwordString = password.getText().toString();

                    if (passwordString.equals(c.getString(passwordIndex))) {
                        int userid = c.getInt(userIdIndex);
                        sessionStart(userid);
                        loginAction(password);
                    } else {
                        Log.e("XXXXXXXXXX PASS", "CORRECT PASSWORD: " + c.getString(passwordIndex));
                        password.setError("Incorrect password");
                    }
                } else {
                    phone.setError("Phone number does not exist in BoxPesa Database, Kindly Register");
                }
            }
        });
    }

    public void loginAction(View view) {

        setContentView(R.layout.activity_main);

        CardView balanceRequest = (CardView) findViewById(R.id.balance_request);
        balanceRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SQLiteDatabase boxpesa = MainActivity.this.openOrCreateDatabase("boxpesa", MODE_PRIVATE, null);
                    String sql = "SELECT * FROM savings WHERE user_id = " + getActiveUser() + "";
                    Cursor c = boxpesa.rawQuery(sql, null);
                    int total_amount = 0;
                    if (c.moveToFirst()) {
                        do {
                            int amountIndex = c.getColumnIndex("amount");
                            total_amount += Integer.valueOf(c.getString(amountIndex));
                        } while (c.moveToNext());
                    }

                    new AlertDialog.Builder(MainActivity.this)
                            .setIcon(R.drawable.money_zungusha)
                            .setTitle("BALANCE REQUEST / SALIO LAKO")
                            .setMessage("Your current BoxPesa balance is Tsh. " + total_amount + "/= . Salio lako la " +
                                    "BoxPesa ni Tsh." + total_amount + "/=")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                } catch (Exception e) {
                    Log.d("XXXXXXXXXX loginAction", "Error: " + e);
                }

            }
        });
    }

    public void directSaving(View view) {
        setContentView(R.layout.direct_saving);

        final Calendar myCalender = Calendar.getInstance();
        final EditText direct_withdraw = (EditText) findViewById(R.id.direct_withdraw);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //Todo Auto Generate Method Stub
                myCalender.set(Calendar.YEAR, year);
                myCalender.set(Calendar.MONTH, monthOfYear);
                myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLable(direct_withdraw, myCalender);
            }
        };

        direct_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, date,
                        myCalender.get(Calendar.YEAR), myCalender.get(Calendar.MONTH), myCalender.get(Calendar.DAY_OF_MONTH));
                Date today = new Date();
                Log.i("xxxxxxxxxx date", today.toString());
                Calendar c = Calendar.getInstance();
                Log.i("xxxxxxxxxx date", c.toString());
                c.setTime(today);
                Log.i("xxxxxxxxxx date", c.toString());
                c.add(Calendar.DAY_OF_MONTH, 14);
                Long minDate = c.getTime().getTime();
                c.setTime(today);
                c.add(Calendar.MONTH, 12);
                Long maxDate = c.getTime().getTime();
                datePickerDialog.getDatePicker().setMaxDate(maxDate);
                datePickerDialog.getDatePicker().setMinDate(minDate);
                datePickerDialog.show();

            }
        });
    }

    public void sendMoney(String amount) {
        Intent ussd = new Intent(this, com.example.boxpesa.ussdPro.class);
        ussd.putExtra("amount", amount);
        startService(ussd);
        String code = "150*01";
        String ussdCode = "*" + code + Uri.encode("#");

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, 4);
        } else {
            startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCode)));
        }

    }

    private void updateLable(EditText edittext, Calendar calendar) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        edittext.setText(sdf.format(calendar.getTime()));
        Log.i("xxxxxxxxxx date", "Calender: Get Time: " + calendar.getTime());
        Log.i("xxxxxxxxxx date", "Calender: Get Format: " + sdf.format(calendar.getTime()));
        Log.i("xxxxxxxxxx date", "Calender: String to Date: " );
    }

    public void savingPockets(View view) {
        setContentView(R.layout.saving_pockets);

        final Calendar myCalender = Calendar.getInstance();
        final EditText maturityDate = (EditText) findViewById(R.id.maturityDate);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                myCalender.set(myCalender.YEAR, year);
                myCalender.set(myCalender.MONTH, monthOfYear);
                myCalender.set(myCalender.DAY_OF_MONTH, dayOfMonth);
                updateLable(maturityDate, myCalender);
            }
        };

        maturityDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, date,
                        myCalender.get(Calendar.YEAR), myCalender.get(Calendar.MONTH), myCalender.get(Calendar.DAY_OF_MONTH));
                Date today = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(today);
                c.add(Calendar.DAY_OF_MONTH, 14);
                Long minDate = c.getTime().getTime();
                c.setTime(today);
                c.add(Calendar.MONTH, 12);
                Long maxDate = c.getTime().getTime();
                datePickerDialog.getDatePicker().setMaxDate(maxDate);
                datePickerDialog.getDatePicker().setMinDate(minDate);
                datePickerDialog.show();
            }
        });

        final ListView myListView = (ListView)findViewById(R.id.pockets);
        ArrayList<SearchResults> ourPockets = new ArrayList<SearchResults>();
        try {
            SQLiteDatabase boxpesa = this.openOrCreateDatabase("boxpesa", MODE_PRIVATE, null);
            String sql2 = "SELECT * FROM saving_accounts WHERE user_id = '" + getActiveUser() + "'";
            Cursor c = boxpesa.rawQuery(sql2, null);
            if (c.moveToFirst()) {
                do {

                    int account_id = c.getColumnIndex("id");
                    int account_nameIndex = c.getColumnIndex("account_name");
                    int saving_goalIndex = c.getColumnIndex("saving_goal");
                    int date_expiryIndex = c.getColumnIndex("date_expiry");
                    SearchResults searchResults = new SearchResults();
                    searchResults.setPocket_id(String.valueOf(c.getInt(account_id)));
                    searchResults.setPocket_name(c.getString(account_nameIndex));
                    searchResults.setPocket_goal("Pocket Goal: " + c.getString(saving_goalIndex));
                    searchResults.setMaturity_date("Maturity Date: " + c.getString(date_expiryIndex));
                    ourPockets.add(searchResults);

                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.e("xxxxxxxxxx", e.toString());
        }

        myListView.setAdapter(new MyCustomeAdapter(MainActivity.this, ourPockets));

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                Object obj = myListView.getItemAtPosition(position);
                final SearchResults results = (SearchResults) obj;


                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();

                builder.setView(inflater.inflate(R.layout.add_pocket_balance, null))
                        .setNegativeButton("Cancel", null);

                final AlertDialog alert = builder.create();
                alert.show();

                TextView pocket_name = (TextView) alert.findViewById(R.id.apb_pocket_name);
                pocket_name.setText("Selected A/C: " + results.getPocket_name());

                final Button add_balance = (Button) alert.findViewById(R.id.adb_add_balance);
                final ListView adb_listView = (ListView) alert.findViewById(R.id.adb_listView);
                TransactionsTheme ttheme = new TransactionsTheme();

                add_balance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText amount = (EditText) alert.findViewById(R.id.adb_amount);

                        if (amount.getText().toString().length() < 1) {
                            amount.setError("Sorry, Field cannot be Blank");
                        } else if (Integer.valueOf(amount.getText().toString()) < 500 ||
                                Integer.valueOf(amount.getText().toString()) > 3000000) {
                            amount.setError("Sorry, Allowable range 500 to 3000000");
                        } else {
                            alert.dismiss();

                            SQLiteDatabase boxpesa = MainActivity.this.openOrCreateDatabase("boxpesa", MODE_PRIVATE, null);
                            String amount_pockets = amount.getText().toString();
                            String status = "pending";
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
                            Date date = new Date();
                            String date_in = sdf.format(date);
                            String goal = results.getPocket_goal().substring(13);
                            Log.d("xxxxxxxxxx POCKETS", goal);
                            int new_balance = Integer.valueOf(amount_pockets) + Integer.valueOf(goal);
                            String saving_accounts = "UPDATE saving_accounts " +
                                    "SET saving_goal = '" + new_balance + "'" +
                                    "WHERE id = '" + results.getPocket_id() + "'";
                            boxpesa.execSQL(saving_accounts);
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("SAVING POCKETS")
                                    .setMessage(amount_pockets + " Has been successfully added to your BoxPesa '" + results.getPocket_name() + "' A/C")
                                    .setNegativeButton("Ok", null)
                                    .show();
                            Toast.makeText(MainActivity.this, "Balance Added Successfull", Toast.LENGTH_LONG).show();
                            Log.d("xxxxxxxxx POCKETS", "Pre Saving Record Added Successfully");

                        }

                    }
                });


            }
        });
    }

    public void moneyZungusha(View view) {
        setContentView(R.layout.money_zungusha);

        final ListView myListView = (ListView)findViewById(R.id.bankRates);
        ArrayList<SearchResults> bankRates = new ArrayList<SearchResults>();

        SearchResults obj1 = new SearchResults();
        obj1.setPocket_name("1000 to 9,999");
        obj1.setPocket_goal("Rate: 0.3% per Month");
        obj1.setMaturity_date("Minimum Duration: 6 Months");
        bankRates.add(obj1);

        SearchResults obj2 = new SearchResults();
        obj2.setPocket_name("10,000 to 49,999");
        obj2.setPocket_goal("Rate: 1.2% per Month");
        obj2.setMaturity_date("Minimum Duration: 5 Months");
        bankRates.add(obj2);

        SearchResults obj3 = new SearchResults();
        obj3.setPocket_name("50,000 to 99,999");
        obj3.setPocket_goal("Rate: 2.4% per Month");
        obj3.setMaturity_date("Minimum Duration: 4 Months");
        bankRates.add(obj3);

        SearchResults obj4 = new SearchResults();
        obj4.setPocket_name("100,000 to 199,999");
        obj4.setPocket_goal("Rate: 3.0% per Month");
        obj4.setMaturity_date("Minimum Duration: 3 Months");
        bankRates.add(obj4);

        SearchResults obj5 = new SearchResults();
        obj5.setPocket_name("200,000 to 499,999");
        obj5.setPocket_goal("Rate: 4.5% per Month");
        obj5.setMaturity_date("Minimum Duration: 3 Months");
        bankRates.add(obj5);

        myListView.setAdapter(new MyCustomeAdapter(MainActivity.this, bankRates));

        final EditText zungusha_date = (EditText) findViewById(R.id.zungusha_date);
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLable(zungusha_date, calendar);
            }
        };

        zungusha_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, date,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                Date today = new Date();
                Calendar min = Calendar.getInstance();
                Calendar max = Calendar.getInstance();
                min.setTime(today);
                max.setTime(today);
                min.add(Calendar.MONTH, 3);
                max.add(Calendar.YEAR, 2);
                Long minim = min.getTime().getTime();
                Long maxim = max.getTime().getTime();
                dialog.getDatePicker().setMinDate(minim);
                dialog.getDatePicker().setMaxDate(maxim);
                dialog.show();

            }
        });


    }

    public void emergency_loan(View view) {
        setContentView(R.layout.emergency_loan);

        final EditText paying_date = (EditText) findViewById(R.id.paying_date);

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLable(paying_date, calendar);
            }
        };

        paying_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,
                        date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                Date today = new Date();
                Calendar min = Calendar.getInstance();
                Calendar max = Calendar.getInstance();
                min.setTime(today);
                max.setTime(today);
                min.add(Calendar.MONTH, 3);
                max.add(Calendar.YEAR, 2);
                Long minim = min.getTime().getTime();
                Long maxim = max.getTime().getTime();
                dialog.getDatePicker().setMinDate(minim);
                dialog.getDatePicker().setMaxDate(maxim);
                dialog.show();
            }
        });
    }



}
