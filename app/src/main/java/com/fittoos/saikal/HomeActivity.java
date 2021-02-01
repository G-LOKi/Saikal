package com.fittoos.saikal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.fittoos.saikal.Keys.IS_REGISTERED;
import static com.fittoos.saikal.Keys.OWNER;
import static com.fittoos.saikal.Keys.ROOMS_STR;
import static com.fittoos.saikal.Keys.USER_DETAILS;
import static com.fittoos.saikal.Keys.USER_NAME;

public class HomeActivity extends AppCompatActivity {


    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;

    private Button mButtonCreate;
    private Button mButtonJoin;

    //Firebase vars
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //****************** link ui elements *********************************
        mButtonCreate = findViewById(R.id.button_create);
        mButtonJoin = findViewById(R.id.button_join);

        //****************** onClick Listeners of buttons **********************
        mButtonCreate.setOnClickListener(view -> {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSION_REQUEST_FINE_LOCATION);

            } else {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
                String playerName = sharedPreferences.getString(USER_NAME, "");

                Intent intent = new Intent(view.getContext(), CreateRaceActivity.class);
                intent.putExtra("playerName", playerName);
                startActivity(intent);
            }

        });

        mButtonJoin.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSION_REQUEST_FINE_LOCATION);

            } else {
                Intent intent = new Intent(view.getContext(), JoinRaceActivity.class);
                startActivity(intent);
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_REQUEST_FINE_LOCATION);

        } else {
            //do nothing
        }


    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //do nothing
                } else {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSION_REQUEST_FINE_LOCATION);

                    Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}