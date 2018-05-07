package com.example.boxpesa;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ussdPro extends AccessibilityService {
    public static String tag = "XXXXXXXXXX USSDPRO";


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(tag, "onAccessibilityEvent PRO");
        String msg = event.getText().toString();
        Log.d(tag, "USSD msg " + msg );
        AccessibilityNodeInfo source = event.getSource();

        //Trying another solution
        Log.d(tag, "MASTER DESCRIPTION: EVENT_TYPE = " + event.getEventType());
        Log.d(tag, "MASTER DESCRIPTION: CLASS_NAME = " + event.getClassName());
        Log.d(tag, "MASTER DESCRIPTION: PACKAGE = " + event.getPackageName());
        Log.d(tag, "MASTER DESCRIPTION: EVENT_TIME = " + event.getEventTime());
        Log.d(tag, "MASTER DESCRIPTION: EVENT_TEXT = " + event.getText());
        Log.d(tag, "MASTER DESCRIPTION: EVENT = " + event);
        Log.d(tag, "MASTER DESCRIPTION: EVENT_TYPE = " + event.getSource());
        Log.d(tag, "MASTER DESCRIPTION: FETCH_RESPONSE = " + fetchResponse(source));

        try {

            //Bringing the Service to USSD Pro

            Pattern p = Pattern.compile("(\\d{9})\\s+Namba hii imesajiliwa kama (\\w+)(\\s)(\\w+),", Pattern.CASE_INSENSITIVE);
            Pattern p1 = Pattern.compile("(\\d{9})\\s+this number is registered under (\\w+)(\\s)(\\w+),", Pattern.CASE_INSENSITIVE);
            Pattern p2 = Pattern.compile("Try Again...\n(\\d{9})\\s+this number is registered under (\\w+)(\\s)(\\w+),", Pattern.CASE_INSENSITIVE);
            Log.d(tag, "Matching Pattern");
            Matcher m = p.matcher(fetchResponse(source));
            Matcher m1 = p1.matcher(fetchResponse(source));
            Matcher m2 = p2.matcher(fetchResponse(source));
            Log.d(tag, fetchResponse(source));
            if (m.find()) {
                Log.d(tag, "Pattern 1 Matched");
                Log.d(tag, "Minimizing USSD Dialog");
                List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByText("Send");
                for (AccessibilityNodeInfo node: list) {
                    Boolean actionClick = node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.i(tag, "PERFORMED CLICK: " + actionClick);
                }

                Log.d(tag, "Preparing and Sending Broadcast");
                Intent intent = new Intent("com.example.boxpesa.action.REFRESH");
                intent.putExtra("message", fetchResponse(source));
                intent.putExtra("package", "advanced");
                sendBroadcast(intent);

            } else if (m1.find()) {
                Log.d(tag, "Pattern 2 Matched");
                Log.d(tag, "Minimizing USSD Dialog");
                List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByText("Send");
                for (AccessibilityNodeInfo node: list) {
                    Boolean actionClick = node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.i(tag, "PERFORMED CLICK: " + actionClick);
                }

                Log.d(tag, "Preparing and Sending Broadcast");
                Intent intent = new Intent("com.example.boxpesa.action.REFRESH");
                intent.putExtra("message", fetchResponse(source));
                intent.putExtra("package", "advanced");
                sendBroadcast(intent);
            } else if (m2.find()) {
                Log.d(tag, "Pattern 3 Matched");
                Log.d(tag, "Minimizing USSD Dialog");
                List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByText("Send");
                for (AccessibilityNodeInfo node: list) {
                    Boolean actionClick = node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.i(tag, "PERFORMED CLICK: " + actionClick);
                }

                Log.d(tag, "Preparing and Sending Broadcast");
                Intent intent = new Intent("com.example.boxpesa.action.REFRESH");
                intent.putExtra("message", fetchResponse(source));
                intent.putExtra("package", "advanced");
                sendBroadcast(intent);
            }


            Pattern first = Pattern.compile("TioPesa");
            Matcher mFirst = first.matcher(fetchResponse(source));

            if (mFirst.find()) {
                Log.i(tag, fetchResponse(source));
                AccessibilityNodeInfo nodeInput = source.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
                Log.i(tag, "NODE INPUT: " + nodeInput.toString());
                Bundle bundle = new Bundle();
                CharSequence mPIN = "4";
                bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, mPIN);
                Log.i(tag, "BUNDLE: " + bundle.toString());
                Boolean actionPerformed = nodeInput.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);
                Log.i(tag, "PERFORMED SET TEXT ACTION: " + actionPerformed);
                Boolean refresh = nodeInput.refresh();
                Log.i(tag, "REFRESHED: " + refresh);

                List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByText("Send");
                for (AccessibilityNodeInfo node: list) {
                    Boolean actionClick = node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.i(tag, "PERFORMED CLICK: " + actionClick);
                }
            }

            Pattern second = Pattern.compile("Malipo");
            Matcher mSecond = second.matcher(fetchResponse(source));

            if (mSecond.find()) {
                Log.i(tag, fetchResponse(source));
                AccessibilityNodeInfo nodeInput = source.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
                Log.i(tag, "NODE INPUT: " + nodeInput.toString());
                Bundle bundle = new Bundle();
                CharSequence mPIN = "3";
                bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, mPIN);
                Log.i(tag, "BUNDLE: " + bundle.toString());
                Boolean actionPerformed = nodeInput.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);
                Log.i(tag, "PERFORMED SET TEXT ACTION: " + actionPerformed);
                Boolean refresh = nodeInput.refresh();
                Log.i(tag, "REFRESHED: " + refresh);

                List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByText("Send");
                for (AccessibilityNodeInfo node: list) {
                    Boolean actionClick = node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.i(tag, "PERFORMED CLICK: " + actionClick);
                }
            }

            Pattern third = Pattern.compile("Ingiza namba ya kampuni");
            Matcher mThird = third.matcher(fetchResponse(source));

            if (mThird.find()) {
                Log.i(tag, fetchResponse(source));
                AccessibilityNodeInfo nodeInput = source.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
                Log.i(tag, "NODE INPUT: " + nodeInput.toString());
                Bundle bundle = new Bundle();
                CharSequence mPIN = "800800";
                bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, mPIN);
                Log.i(tag, "BUNDLE: " + bundle.toString());
                Boolean actionPerformed = nodeInput.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);
                Log.i(tag, "PERFORMED SET TEXT ACTION: " + actionPerformed);
                Boolean refresh = nodeInput.refresh();
                Log.i(tag, "REFRESHED: " + refresh);

                List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByText("Send");
                for (AccessibilityNodeInfo node : list) {
                    Boolean actionClick = node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.i(tag, "PERFORMED CLICK: " + actionClick);
                }
            }

            Pattern forth = Pattern.compile("Weka");
            Matcher mForth = forth.matcher(fetchResponse(source));

            if (mForth.find()) {
                Log.i(tag, fetchResponse(source));
                AccessibilityNodeInfo nodeInput = source.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
                Log.i(tag, "NODE INPUT: " + nodeInput.toString());
                Bundle bundle = new Bundle();
                CharSequence mPIN = "333333";
                bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, mPIN);
                Log.i(tag, "BUNDLE: " + bundle.toString());
                Boolean actionPerformed = nodeInput.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);
                Log.i(tag, "PERFORMED SET TEXT ACTION: " + actionPerformed);
                Boolean refresh = nodeInput.refresh();
                Log.i(tag, "REFRESHED: " + refresh);

                List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByText("Send");
                for (AccessibilityNodeInfo node : list) {
                    Boolean actionClick = node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.i(tag, "PERFORMED CLICK: " + actionClick);
                }
            }

            Pattern fith = Pattern.compile("Ingiza Kiasi");
            Matcher mFith = fith.matcher(fetchResponse(source));

            if (mFith.find()) {
                Log.i(tag, fetchResponse(source));
                AccessibilityNodeInfo nodeInput = source.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
                Log.i(tag, "NODE INPUT: " + nodeInput.toString());
                Bundle bundle = new Bundle();
                CharSequence mPIN = "5000";
                bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, mPIN);
                Log.i(tag, "BUNDLE: " + bundle.toString());
                Boolean actionPerformed = nodeInput.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);
                Log.i(tag, "PERFORMED SET TEXT ACTION: " + actionPerformed);
                Boolean refresh = nodeInput.refresh();
                Log.i(tag, "REFRESHED: " + refresh);

                List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByText("Send");
                for (AccessibilityNodeInfo node : list) {
                    Boolean actionClick = node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.i(tag, "PERFORMED CLICK: " + actionClick);
                }
            }

            Pattern sixth = Pattern.compile("Ingiza Kiasi");
            Matcher mSixth = sixth.matcher(fetchResponse(source));

            if (mSixth.find()) {

                Intent intent = new Intent(this, com.example.boxpesa.ussdPro.class);
                Bundle extras = intent.getExtras();

                Log.i(tag, fetchResponse(source));
                Log.i(tag, "Amount: " + extras.getCharSequence("amount"));
                AccessibilityNodeInfo nodeInput = source.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
                Log.i(tag, "NODE INPUT: " + nodeInput.toString());
                Bundle bundle = new Bundle();
                CharSequence mPIN = extras.getString("amount");
                bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, mPIN);
                Log.i(tag, "BUNDLE: " + bundle.toString());
                Boolean actionPerformed = nodeInput.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);
                Log.i(tag, "PERFORMED SET TEXT ACTION: " + actionPerformed);
                Boolean refresh = nodeInput.refresh();
                Log.i(tag, "REFRESHED: " + refresh);

                List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByText("Send");
                for (AccessibilityNodeInfo node : list) {
                    Boolean actionClick = node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.i(tag, "PERFORMED CLICK: " + actionClick);
                }
            }
        } catch (Exception e) {
            Log.e(tag, e.toString());
        }

        /**
        try {
            List<AccessibilityNodeInfo> list = source.findAccessibilityNodeInfosByText("Send");
            for (AccessibilityNodeInfo node: list) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        } catch (Exception e) {
            Log.e(tag, e.toString());
        }
         **/


        /**
        if (event.getClassName().equals("android.app.AlertDialog")) {
            Log.d(tag, "android.app.AlertDialog Detected, Performing global action");
            performGlobalAction(GLOBAL_ACTION_BACK);
            Log.d(tag, msg);
            Intent intent = new Intent("com.example.boxpesa.action.REFRESH");
            intent.putExtra("message", msg);
            intent.putExtra("package", "default");
            sendBroadcast(intent);
        } else {
            Log.d(tag, event.getClassName() + " Detected, Performing global action");
            performGlobalAction(GLOBAL_ACTION_BACK);
            Log.d(tag, fetchResponse(source));
            Intent intent = new Intent("com.example.boxpesa.action.REFRESH");
            intent.putExtra("message", fetchResponse(source));
            intent.putExtra("package", "advanced");
            sendBroadcast(intent);
        }
         **/
    }

    private String fetchResponse(AccessibilityNodeInfo accessibilityNodeInfo) {

        String fetchedResponse = "";
        if (accessibilityNodeInfo != null) {
            for (int i = 0; i < accessibilityNodeInfo.getChildCount(); i++) {
                AccessibilityNodeInfo child = accessibilityNodeInfo.getChild(i);
                if (child != null) {

                    CharSequence text = child.getText();

                    if (text != null
                            && child.getClassName().equals(
                            Button.class.getName())) {

                        // dismiss dialog by performing action click in normal
                        // cases


                    } else if (text != null
                            && child.getClassName().equals(
                            TextView.class.getName())) {

                        // response of normal cases
                        if (text.toString().length() > 10) {
                            fetchedResponse = text.toString();
                        }

                    } else if (child.getClassName().equals(
                            ScrollView.class.getName())) {

                        // when response comes as phone then response can be
                        // retrived from subchild
                        for (int j = 0; j < child.getChildCount(); j++) {

                            AccessibilityNodeInfo subChild = child.getChild(j);
                            CharSequence subText = subChild.getText();

                            if (subText != null
                                    && subChild.getClassName().equals(
                                    TextView.class.getName())) {

                                // response of different cases
                                if (subText.toString().length() > 10) {
                                    fetchedResponse = subText.toString();
                                }

                            }

                            else if (subText != null
                                    && subChild.getClassName().equals(
                                    Button.class.getName())) {



                            }
                        }
                    }
                }
            }
        }
        return fetchedResponse;
    }


    @Override
    public void onInterrupt() {}

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(tag, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.packageNames = new String[]{"com.android.phone"};
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }


}

