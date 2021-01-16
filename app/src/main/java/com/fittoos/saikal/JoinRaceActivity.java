package com.fittoos.saikal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.FirebaseDatabase;

public class JoinRaceActivity extends AppCompatActivity {

    private Button mButtonEnterRace;

    //Firebase vars
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_race);

        //****************** link ui elements *********************************
        mButtonEnterRace = findViewById(R.id.button_enter_race);

        //****************** onClick Listeners of buttons **********************
        mButtonEnterRace.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), MainRaceActivity.class);
            startActivity(intent);
        });
    }
}