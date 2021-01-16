package com.fittoos.saikal;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Timer;
import java.util.TimerTask;

import static com.fittoos.saikal.Keys.IS_REGISTERED;
import static com.fittoos.saikal.Keys.USER_DETAILS;
import static com.fittoos.saikal.Keys.USER_NAME;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                boolean isRegistered = false;

                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
                isRegistered = sharedPreferences.getBoolean(IS_REGISTERED, false);

                if(isRegistered) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }else {
                    startActivity(new Intent(getApplicationContext(), UserRegistrationActivity.class));
                }
                finish();

            }
        }, 3000);


    }

}