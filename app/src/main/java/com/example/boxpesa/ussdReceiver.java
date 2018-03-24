package com.example.boxpesa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ussdReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.i("XXXXXXXXXX", "Broadcast receiver detected intent");
        Bundle extras = intent.getExtras();
        String myUSSDpackage = extras.getString("package");
        if (myUSSDpackage.equals("default")) {
            Log.d("XXXXXXXXXXX", "PACKAGE: default");
            String message = extras.getString("message");
            Pattern p = Pattern.compile("\\[(\\d{9})\\s+Namba hii imesajiliwa kama (\\w+)(\\s)(\\w+),", Pattern.CASE_INSENSITIVE);
            Log.i("XXXXXXXXXX", "Matching Pattern");
            Matcher m = p.matcher(message);
            Log.d("XXXXXXXXXX", message);
            if (m.find()) {
                Log.i("XXXXXXXXXX", "Match Found");
                String registeredName = m.group(2) + m.group(3) + m.group(4);
                Log.i("XXXXXXXXXXX", "Registered name: " + registeredName);
                String activePhone = "0" + m.group(1);
                Log.i("XXXXXXXXXXX", "active phone: " + activePhone);
                Intent i = new Intent("REGISTERED_NAME");
                i.putExtra("name", registeredName);
                i.putExtra("phone", activePhone);

                context.sendBroadcast(i);
            } else {

            }
        } else {
            Log.d("XXXXXXXXXX", "PACKAGE: advanced");
            String message = extras.getString("message");
            Pattern p = Pattern.compile("(\\d{9})\\s+Namba hii imesajiliwa kama (\\w+)(\\s)(\\w+),", Pattern.CASE_INSENSITIVE);
            Log.i("XXXXXXXXXXX", "Matching Pattern");
            Matcher m = p.matcher(message);
            Log.d("USSD", message);
            if (m.find()) {
                Log.i("XXXXXXXXXXX", "Match Found");
                String registeredName = m.group(2) + m.group(3) + m.group(4);
                Log.i("XXXXXXXXXXX", "Registered name = " + registeredName);
                String activePhone = "0" + m.group(1);
                Log.i("XXXXXXXXXX", "active phone = " + activePhone);
                Intent i = new Intent("REGISTERED_NAME");

                i.putExtra("name", registeredName);
                i.putExtra("phone", activePhone);

                context.sendBroadcast(i);
            } else {

            }
        }

    }
}
