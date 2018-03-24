package com.example.boxpesa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by Mark on 2/6/18.
 */

public class SmsReceiver extends BroadcastReceiver {

    //Interface
    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");

        for(int i=0; i<pdus.length; i++) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            //Checking the Sender to filter out the msgs which we require to read
            String sender = smsMessage.getDisplayOriginatingAddress();
            String messageBody = smsMessage.getMessageBody();
            mListener.messageReceived(messageBody);
        }

    }

    public static void bindListner(SmsListener listener) {
        mListener = listener;
    }
}
