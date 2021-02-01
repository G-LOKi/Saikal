package com.fittoos.saikal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
            String playerName = sharedPreferences.getString(USER_NAME, "");

            Intent intent = new Intent(view.getContext(), CreateRaceActivity.class);
            intent.putExtra("playerName", playerName);
            startActivity(intent);
        });

        mButtonJoin.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), JoinRaceActivity.class);
            startActivity(intent);
        });
    }
}