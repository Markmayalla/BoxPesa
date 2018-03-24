package com.example.boxpesa;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mark on 1/14/18.
 */

public class ussd extends AccessibilityService {

    public static String tag = "XXXXXXXXXX USSD";


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        Log.d(tag, "onAccessibilityEvent");
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


        if (event.getClassName().equals("android.app.AlertDialog")) {
            Log.d(tag, "android.app.AlertDialog Detected, Performing global action");

                Log.d(tag, msg);
                performGlobalAction(GLOBAL_ACTION_BACK);
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
