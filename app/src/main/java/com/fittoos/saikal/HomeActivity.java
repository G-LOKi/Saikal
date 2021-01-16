package com.fittoos.saikal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

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
            Intent intent = new Intent(view.getContext(), CreateRaceActivity.class);
            startActivity(intent);
            DatabaseReference roomRef = mDatabase.getReference("rooms");
            String roomID =  roomRef.push().getKey();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(roomID + "/data", "data");
            roomRef.updateChildren(childUpdates);

        });

        mButtonJoin.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), JoinRaceActivity.class);
            startActivity(intent);
        });
    }
}